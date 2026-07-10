package net.paceapp.core.strava

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import net.paceapp.core.data.firestore.await
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

// UI-facing connection state, mirrored from `users/{uid}.strava` + local in-flight flag.
data class StravaUiState(
    val isConnected: Boolean = false,
    val athleteName: String? = null,
    val isWorking: Boolean = false,
)

private const val TAG = "StravaManager"

// On-device half of the Strava integration (mirrors iOS StravaManager). It ONLY runs the
// OAuth authorize step, hands the returned `code` to a Cloud Function, and mirrors the
// connection summary the functions write to `users/{uid}.strava`. Token exchange, refresh
// and every activity upload happen server-side — the device never holds a Strava token.
@Singleton
class StravaManager @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val api: StravaApi,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {
    private val _state = MutableStateFlow(StravaUiState())
    val state: StateFlow<StravaUiState> = _state.asStateFlow()

    private var listener: ListenerRegistration? = null
    private val json = Json { ignoreUnknownKeys = true }

    // MARK: - Connection state (Firestore)

    // Mirrors `users/{uid}.strava` → isConnected / athleteName. The Cloud Functions write
    // this summary; the app only reads the raw nested map (never the POJO, so a profile
    // upsert can't clobber it). Idempotent — safe to call from every screen's onAppear.
    fun startObserving() {
        if (listener != null) return
        val uid = auth.currentUser?.uid ?: return
        listener = firestore.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    AppLogger.e("[$TAG] user listener failed", error)
                    return@addSnapshotListener
                }
                @Suppress("UNCHECKED_CAST")
                val strava = snapshot?.get("strava") as? Map<String, Any?>
                val connected = strava?.get("connected") as? Boolean ?: false
                val name = strava?.get("athleteName") as? String
                _state.update { it.copy(isConnected = connected, athleteName = name) }
            }
    }

    // Tears down the listener + resets state (e.g. on logout).
    fun stopObserving() {
        listener?.remove()
        listener = null
        _state.value = StravaUiState()
    }

    // MARK: - Connect (OAuth authorize)

    // Prefers the installed Strava app for a native handoff; falls back to a Custom Tab.
    // The callback returns via paceapp://strava-callback → MainActivity → handleCallback().
    fun connect(context: Context) {
        if (StravaConst.clientId.isBlank()) {
            reportError("Strava isn't set up yet. Please try again later.")
            AppLogger.e("[$TAG] connect blocked — STRAVA_CLIENT_ID is empty")
            return
        }
        val appIntent = Intent(Intent.ACTION_VIEW, authorizeUri(StravaConst.APP_AUTHORIZE_URL)).apply {
            setPackage("com.strava")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(appIntent)
        } catch (_: ActivityNotFoundException) {
            openCustomTab(context, authorizeUri(StravaConst.WEB_AUTHORIZE_URL))
        }
    }

    // Parses the OAuth redirect (both native-app and Custom-Tab paths land here via
    // MainActivity). Self-guards on scheme+host. Mirrors iOS handleCallback.
    fun handleCallback(uri: Uri) {
        if (uri.scheme != StravaConst.CALLBACK_SCHEME || uri.host != StravaConst.CALLBACK_HOST) return

        uri.getQueryParameter("error")?.let { denied ->
            reportError(
                if (denied == "access_denied") "Strava connection was cancelled."
                else "Couldn't connect to Strava."
            )
            return
        }
        val code = uri.getQueryParameter("code")
        if (code.isNullOrBlank()) {
            reportError("Couldn't connect to Strava. Please try again.")
            return
        }
        // activity:write is required for uploads — warn (don't block) if it wasn't granted.
        val granted = uri.getQueryParameter("scope").orEmpty()
        if (!granted.contains("activity:write")) {
            reportError("Please allow activity upload access so we can sync your runs.")
        }
        exchange(code)
    }

    // MARK: - Server calls

    // code → tokens (server), connect. The Firestore listener flips isConnected; the name
    // is set optimistically for instant UI.
    private fun exchange(code: String) {
        runServerCall(
            block = { token ->
                val result = api.exchange(token, StravaExchangeRequest(code))
                _state.update { it.copy(athleteName = result.athleteName ?: it.athleteName) }
                AppAlerts.showToast("Connected to Strava.", type = MessageType.Success)
            },
            failureLog = "exchange failed",
        )
    }

    // Pushes recent completed activities that haven't reached Strava yet.
    fun syncRecent() {
        runServerCall(
            block = { token ->
                val count = api.backfill(token).synced
                val message = when {
                    count <= 0 -> "You're all caught up."
                    count == 1 -> "Synced 1 activity to Strava."
                    else -> "Synced $count activities to Strava."
                }
                AppAlerts.showToast(message, type = MessageType.Success)
            },
            failureLog = "backfill failed",
        )
    }

    fun disconnect() {
        runServerCall(
            block = { token ->
                api.disconnect(token)
                _state.update { it.copy(isConnected = false, athleteName = null) }
                AppAlerts.showToast("Disconnected from Strava.", type = MessageType.Success)
            },
            failureLog = "disconnect failed",
        )
    }

    // MARK: - Private

    // Shared wrapper: flips isWorking, fetches the Firebase ID token, runs the call, and
    // surfaces the function's friendly {error} message on failure.
    private fun runServerCall(block: suspend (bearer: String) -> Unit, failureLog: String) {
        setWorking(true)
        appScope.launch {
            try {
                val token = bearerToken()
                if (token == null) {
                    reportError("Please sign in again to continue.")
                    return@launch
                }
                block(token)
            } catch (t: Throwable) {
                AppLogger.e("[$TAG] $failureLog", t)
                reportError(errorMessage(t))
            } finally {
                setWorking(false)
            }
        }
    }

    private suspend fun bearerToken(): String? {
        val user = auth.currentUser ?: return null
        return runCatching { "Bearer " + user.getIdToken(false).await().token }.getOrNull()
    }

    private fun authorizeUri(base: String): Uri =
        base.toUri().buildUpon()
            .appendQueryParameter("client_id", StravaConst.clientId)
            .appendQueryParameter("redirect_uri", StravaConst.REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("approval_prompt", "auto")
            .appendQueryParameter("scope", StravaConst.SCOPE)
            .build()

    private fun openCustomTab(context: Context, uri: Uri) {
        val intent = CustomTabsIntent.Builder().build()
        intent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.launchUrl(context, uri)
    }

    private fun setWorking(working: Boolean) = _state.update { it.copy(isWorking = working) }

    private fun reportError(message: String) = AppAlerts.showToast(message, type = MessageType.Error)

    // Pulls the function's friendly `{ "error": "…" }` message out of a non-2xx response.
    private fun errorMessage(t: Throwable): String {
        if (t is HttpException) {
            val body = t.response()?.errorBody()?.string()
            if (!body.isNullOrBlank()) {
                runCatching { json.decodeFromString<StravaErrorResponse>(body).error }
                    .getOrNull()
                    ?.let { return it }
            }
        }
        return "Something went wrong. Please try again."
    }
}
