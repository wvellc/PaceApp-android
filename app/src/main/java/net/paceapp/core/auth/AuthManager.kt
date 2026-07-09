package net.paceapp.core.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.data.firestore.FavoriteRepository
import net.paceapp.core.data.firestore.UserDocument
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.data.firestore.await
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.core.domain.models.UserData
import net.paceapp.core.domain.models.isProfileComplete
import net.paceapp.session.AppSessionManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

// Firebase Auth engine — mirrors iOS AuthManager. Handles phone OTP + passwordless
// email link, and on any successful sign-in ensures a `users/{uid}` document exists
// and mirrors identity into the local session so AuthRouteManager keeps working.
@Singleton
class AuthManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val userProfileRepository: UserProfileRepository,
    private val eventRepository: EventRepository,
    private val favoriteRepository: FavoriteRepository,
    private val garminDeviceManager: GarminDeviceManager,
    private val sessionManager: AppSessionManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {
    // Carried between onCodeSent and confirmOtp so the OTP screen doesn't need to
    // thread the verificationId through navigation.
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val prefs by lazy { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }

    // Phases of the out-of-band email-link sign-in, so the nav host can show the
    // Authenticating screen then route (mirrors iOS onOpenURL → .authenticating → root).
    // StateFlow (not SharedFlow) so a cold-start nav host that subscribes AFTER the
    // link is handled still receives the current phase (a SharedFlow emit would be lost).
    private val _emailLinkPhase = MutableStateFlow<EmailLinkPhase?>(null)
    val emailLinkPhase: StateFlow<EmailLinkPhase?> = _emailLinkPhase.asStateFlow()

    val currentUid: String? get() = auth.currentUser?.uid
    val isSignedIn: Boolean get() = auth.currentUser != null

    // True while an email-link re-authentication (for account deletion) is in flight,
    // so MainActivity treats the returning link as reauth-then-delete, not a fresh
    // sign-in. Mirrors iOS AuthManager.isReauthenticatingForDeletion.
    var isReauthenticatingForDeletion = false

    // MARK: - Phone OTP

    // Starts phone verification. Requires an Activity (Play Integrity / reCAPTCHA).
    // onAutoVerified fires when the SMS is auto-retrieved and sign-in completes
    // without the user typing a code.
    fun sendPhoneOtp(
        activity: Activity,
        phoneE164: String,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit,
        onAutoVerified: () -> Unit,
    ) {
        AppLogger.d("[Auth] verifyPhoneNumber start for $phoneE164")
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AppLogger.d("[Auth] onVerificationCompleted (auto-retrieval)")
                appScope.launch {
                    runCatching { signInWithPhoneCredential(credential) }
                        .onSuccess { onAutoVerified() }
                        .onFailure { onError(AuthErrorMapper.message(it)) }
                }
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                val code = (e as? FirebaseAuthException)?.errorCode
                AppLogger.e("[Auth] onVerificationFailed code=$code", e)
                onError(AuthErrorMapper.message(e))
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                AppLogger.d("[Auth] onCodeSent — verificationId received")
                verificationId = id
                resendToken = token
                onCodeSent(id)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneE164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Confirms the 6-digit code against the verificationId carried from onCodeSent
    // (passed via the nav route, mirroring iOS). Falls back to the stored field.
    suspend fun confirmOtp(verificationId: String?, code: String): Result<Unit> = runCatching {
        val id = verificationId?.takeIf { it.isNotEmpty() } ?: this.verificationId
        ?: error("No verification in progress")
        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithPhoneCredential(credential)
    }

    private suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).await()
        ensureUserAndSession()
    }

    // Populate the local session from the active Firebase user (fetch-or-create the
    // profile), so routing never bounces a signed-in user to login for a missing token.
    suspend fun syncSessionIfSignedIn() {
        if (auth.currentUser != null) ensureUserAndSession()
    }

    // MARK: - Email link

    fun isEmailSignInLink(link: String): Boolean = auth.isSignInWithEmailLink(link)

    // Orchestrates email-link sign-in for MainActivity: emits Authenticating (nav host
    // shows the loading screen), completes sign-in, then emits Success/Failed so the
    // nav host routes to the dashboard/build-profile or back to login. Returns false
    // (no-op) when the incoming link is not an email sign-in link.
    fun handleIncomingLinkIfEmailSignIn(link: String): Boolean {
        if (!isEmailSignInLink(link)) return false
        appScope.launch {
            _emailLinkPhase.value = EmailLinkPhase.Authenticating
            completeEmailLink(link)
                .onSuccess {
                    val complete = sessionManager.getUserDetails()?.isProfileComplete == true
                    _emailLinkPhase.value = EmailLinkPhase.Success(isProfileComplete = complete)
                }
                .onFailure {
                    AppLogger.e("[Auth] email link sign-in failed", it)
                    _emailLinkPhase.value = EmailLinkPhase.Failed
                }
        }
        return true
    }

    suspend fun sendEmailLink(email: String): Result<Unit> = runCatching {
        val settings = ActionCodeSettings.newBuilder()
            .setUrl(EMAIL_LINK_CONTINUE_URL)
            .setHandleCodeInApp(true)
            .setAndroidPackageName(context.packageName, true, null)
            .build()
        auth.sendSignInLinkToEmail(email, settings).await()
        prefs.edit().putString(KEY_PENDING_EMAIL, email).apply()
    }

    // Completes email-link sign-in when the app is opened via the link.
    suspend fun completeEmailLink(link: String): Result<Unit> = runCatching {
        val email = prefs.getString(KEY_PENDING_EMAIL, null)
            ?: error("No pending email for this sign-in link")
        auth.signInWithEmailLink(email, link).await()
        prefs.edit().remove(KEY_PENDING_EMAIL).apply()
        ensureUserAndSession()
    }

    // MARK: - Re-authentication + account deletion

    // How the current user signed in, with the contact used — drives the reauth flow.
    val authProviderKind: AuthProviderKind
        get() {
            val user = auth.currentUser ?: return AuthProviderKind.Unknown
            user.phoneNumber?.takeIf { it.isNotBlank() }?.let { return AuthProviderKind.Phone(it) }
            user.email?.takeIf { it.isNotBlank() }?.let { return AuthProviderKind.Email(it) }
            // Fall back to provider records (identity can live there when the top-level
            // phoneNumber/email is empty).
            for (p in user.providerData) {
                when (p.providerId) {
                    PhoneAuthProvider.PROVIDER_ID ->
                        p.phoneNumber?.takeIf { it.isNotBlank() }?.let { return AuthProviderKind.Phone(it) }
                    EmailAuthProvider.PROVIDER_ID ->
                        p.email?.takeIf { it.isNotBlank() }?.let { return AuthProviderKind.Email(it) }
                }
            }
            return AuthProviderKind.Unknown
        }

    // Sends a fresh OTP to the signed-in user's OWN phone number (reuses the phone
    // verification flow, but for reauth — no sign-in happens). onDone fires only on
    // auto-retrieval, where we reauthenticate directly without the user typing a code.
    fun sendReauthOtp(
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit,
        onDone: () -> Unit,
    ) {
        val number = (authProviderKind as? AuthProviderKind.Phone)?.number
        if (number.isNullOrBlank()) {
            onError("This account has no phone number to verify.")
            return
        }
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Auto-retrieval — reauthenticate straight away, no code entry needed.
                appScope.launch {
                    runCatching { auth.currentUser?.reauthenticate(credential)?.await() }
                        .onSuccess { onDone() }
                        .onFailure { onError(AuthErrorMapper.message(it)) }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                AppLogger.e("[Auth] reauth onVerificationFailed", e)
                onError(AuthErrorMapper.message(e))
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                resendToken = token
                onCodeSent(id)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Re-authenticates the current user with a phone OTP (NO sign-out).
    suspend fun reauthenticateWithPhone(verificationId: String?, code: String): Result<Unit> = runCatching {
        val id = verificationId?.takeIf { it.isNotEmpty() } ?: this.verificationId
        ?: error("No verification in progress")
        val user = auth.currentUser ?: error("You're not signed in.")
        val credential = PhoneAuthProvider.getCredential(id, code)
        user.reauthenticate(credential).await()
        Unit
    }

    // Sends a sign-in link to the signed-in user's OWN email for reauth (no sign-out),
    // and flags the returning link so MainActivity routes it to reauth-then-delete.
    suspend fun sendReauthEmailLink(): Result<Unit> = runCatching {
        val email = (authProviderKind as? AuthProviderKind.Email)?.email
        require(!email.isNullOrBlank()) { "This account has no email to verify." }
        sendEmailLink(email).getOrThrow()
        isReauthenticatingForDeletion = true
    }

    // Re-authenticates the current user with an email link, then clears the reauth flag.
    suspend fun reauthenticateWithEmailLink(link: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: error("You're not signed in.")
        val email = (authProviderKind as? AuthProviderKind.Email)?.email
            ?: prefs.getString(KEY_PENDING_EMAIL, null)
            ?: error("This account has no email to verify.")
        val credential = EmailAuthProvider.getCredentialWithLink(email, link)
        user.reauthenticate(credential).await()
        prefs.edit().remove(KEY_PENDING_EMAIL).apply()
        isReauthenticatingForDeletion = false
    }

    // Permanently deletes the account. ORDER MATTERS: disconnect the watch first so its
    // live sync can't re-create events mid-deletion, then wipe cloud data, then the Auth
    // account (which requires a recent reauth), then the local session. Cloud cleanup is
    // best-effort (runCatching) — a failed read/write must NOT strand the Auth account;
    // but a hard user.delete() failure propagates so the caller can surface it.
    suspend fun deleteAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        runCatching { garminDeviceManager.disconnect() }
        runCatching { eventRepository.deleteAllForUser(uid) }
        runCatching { favoriteRepository.deleteAllForUser(uid) }
        runCatching { userProfileRepository.deleteUser(uid) }

        user.delete().await()

        runCatching { auth.signOut() }
        runCatching { sessionManager.clearSession() }
        isReauthenticatingForDeletion = false
    }

    // MARK: - Session

    suspend fun signOut() {
        auth.signOut()
        sessionManager.clearSession()
    }

    // Fetch-or-create users/{uid}, then mirror identity into the local session so
    // AuthRouteManager routes to BUILD_PROFILE (no name yet) or TAB_HOST.
    private suspend fun ensureUserAndSession() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        var doc = userProfileRepository.getUser(uid)
        if (doc == null) {
            doc = UserDocument(
                uuid = uid,
                email = user.email.orEmpty(),
                phoneNumber = user.phoneNumber.orEmpty(),
            )
            userProfileRepository.upsertUser(doc)
        }

        val loginType = if (!user.phoneNumber.isNullOrBlank()) LoginTypes.PHONE else LoginTypes.EMAIL
        sessionManager.saveToken(uid)
        sessionManager.setUserDetails(
            UserData(
                id = uid,
                firstName = doc.firstName.ifBlank { null },
                lastName = doc.lastName.ifBlank { null },
                loginType = loginType,
                email = doc.email.ifBlank { null } ?: user.email,
                phoneNumber = doc.phoneNumber.ifBlank { null } ?: user.phoneNumber,
            )
        )
    }

    companion object {
        // Must be an authorized domain in the Firebase console + backed by an
        // assetlinks.json App Link on the hosting site (parity with iOS continueURL).
        private const val EMAIL_LINK_CONTINUE_URL = "https://thepaceapp.firebaseapp.com/emailSignIn"
        private const val KEY_PENDING_EMAIL = "pending_email_for_signin"
    }
}

// How the current user signed in, with the contact used — drives the reauth-for-
// deletion flow. Mirrors iOS AuthManager.AuthProviderKind.
sealed interface AuthProviderKind {
    data class Phone(val number: String) : AuthProviderKind
    data class Email(val email: String) : AuthProviderKind
    data object Unknown : AuthProviderKind
}

// Phases of an email-link sign-in, consumed by AppNavHost to drive navigation.
sealed interface EmailLinkPhase {
    data object Authenticating : EmailLinkPhase
    data class Success(val isProfileComplete: Boolean) : EmailLinkPhase
    data object Failed : EmailLinkPhase
}
