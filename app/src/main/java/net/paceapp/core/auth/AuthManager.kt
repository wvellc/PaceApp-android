package net.paceapp.core.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.paceapp.core.data.firestore.UserDocument
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.data.firestore.await
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.core.domain.models.UserData
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
    private val sessionManager: AppSessionManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {
    // Carried between onCodeSent and confirmOtp so the OTP screen doesn't need to
    // thread the verificationId through navigation.
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val prefs by lazy { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }

    val currentUid: String? get() = auth.currentUser?.uid
    val isSignedIn: Boolean get() = auth.currentUser != null

    // MARK: - Phone OTP

    // Starts phone verification. Requires an Activity (Play Integrity / reCAPTCHA).
    // onAutoVerified fires when the SMS is auto-retrieved and sign-in completes
    // without the user typing a code.
    fun sendPhoneOtp(
        activity: Activity,
        phoneE164: String,
        onCodeSent: () -> Unit,
        onError: (String) -> Unit,
        onAutoVerified: () -> Unit,
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                appScope.launch {
                    runCatching { signInWithPhoneCredential(credential) }
                        .onSuccess { onAutoVerified() }
                        .onFailure { onError(it.message ?: "Verification failed") }
                }
            }

            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                AppLogger.e("[Auth] phone verification failed", e)
                onError(e.message ?: "Verification failed")
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                resendToken = token
                onCodeSent()
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

    // Confirms the 6-digit code the user typed against the pending verificationId.
    suspend fun confirmOtp(code: String): Result<Unit> = runCatching {
        val id = verificationId ?: error("No verification in progress")
        val credential = PhoneAuthProvider.getCredential(id, code)
        signInWithPhoneCredential(credential)
    }

    private suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).await()
        ensureUserAndSession()
    }

    // MARK: - Email link

    fun isEmailSignInLink(link: String): Boolean = auth.isSignInWithEmailLink(link)

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
