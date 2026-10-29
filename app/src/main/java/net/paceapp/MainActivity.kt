package net.paceapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler
import net.paceapp.core.auth.AuthErrorMapper
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.domain.repositories.UserRepository
import net.paceapp.core.garmin.BluetoothStateReceiver
import net.paceapp.core.garmin.GarminDeviceManager
import net.paceapp.core.strava.StravaConst
import net.paceapp.core.strava.StravaManager
import net.paceapp.core.components.AppActionDialog
import net.paceapp.core.components.CustomToast
import net.paceapp.core.revenuecat.AppCustomerCenterHost
import net.paceapp.core.revenuecat.PaywallController
import net.paceapp.core.revenuecat.RevenueCatConst
import net.paceapp.navigation.AppNavHost
import net.paceapp.session.AppSessionManager
import net.paceapp.theme.PaceAppTheme
import com.wvelabs.core_ui.alerts.AppAlertContainer
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: AppSessionManager

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var stravaManager: StravaManager

    @Inject
    lateinit var garminDeviceManager: GarminDeviceManager

    @Inject
    lateinit var userRepository: UserRepository

    // Re-arms the remembered-watch reconnect when the phone's Bluetooth turns back on.
    private val bluetoothStateReceiver = BluetoothStateReceiver { restoreWatchConnection() }

    // Presents the dashboard paywall full-screen via RevenueCat's PaywallActivity. Registered
    // in onCreate (ActivityResultLaunchers require registration before STARTED) and driven by
    // PaywallController. A purchase/pace_app_pro-active restore proceeds the gated flow.
    private lateinit var paywallLauncher: PaywallActivityLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // A Strava OAuth redirect takes priority; otherwise complete a passwordless
        // email-link sign-in if the app was opened via the link.
        if (!handleStravaCallback(intent)) {
            handleEmailSignInLink(intent)
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT)
        )

        super.onCreate(savedInstanceState)

        // Register before STARTED. Purchase or a pace_app_pro-active restore proceeds the gated
        // flow; cancel/error just drops it.
        paywallLauncher = PaywallActivityLauncher(this, object : PaywallResultHandler {
            override fun onActivityResult(result: PaywallResult) {
                when (result) {
                    is PaywallResult.Purchased -> PaywallController.completeAndDismiss()
                    is PaywallResult.Restored -> {
                        val proActive = result.customerInfo
                            .entitlements[RevenueCatConst.entitlementPro]?.isActive == true
                        if (proActive) PaywallController.completeAndDismiss() else PaywallController.dismiss()
                    }
                    is PaywallResult.Cancelled, is PaywallResult.Error -> PaywallController.dismiss()
                }
            }
        })

        setContent {
            val navController = rememberNavController()

            // Present the dashboard paywall full-screen whenever a ViewModel asks via
            // PaywallController. launch(offering = null) uses the current offering.
            LaunchedEffect(Unit) {
                PaywallController.launchRequests.collect {
                    paywallLauncher.launch(shouldDisplayDismissButton = true)
                }
            }

            PaceAppTheme {
                // Snackbar / Dialogs / Toasts container
                AppAlertContainer(
                    toastContent = { toast, dismissAction ->
                        CustomToast(
                            toast = toast,
                            onDismiss = dismissAction
                        )
                    },
                    dialogContent = { dialog, dismissAction ->
                        AppActionDialog(alert = dialog, closeDialog = dismissAction)
                    }
                ) {
                    // The Navigation Flow
                    AppNavHost(
                        navController = navController,
                        sessionManager = sessionManager,
                        authManager = authManager
                    )

                    // Customer Center — floats over the app from Settings → Manage Subscription.
                    AppCustomerCenterHost()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (!handleStravaCallback(intent)) {
            handleEmailSignInLink(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Catch a remote account deletion promptly: force a token check. If the account was
        // deleted/disabled elsewhere, AuthManager routes this device to the login flow.
        lifecycleScope.launch { authManager.verifyAccountStillValid() }

        // Auto-reconnect the remembered Garmin watch: listen for Bluetooth turning back on,
        // and attempt a restore now (covers returning to the app after a Bluetooth off/on).
        ContextCompat.registerReceiver(
            this,
            bluetoothStateReceiver,
            BluetoothStateReceiver.intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        restoreWatchConnection()
    }

    override fun onPause() {
        super.onPause()
        runCatching { unregisterReceiver(bluetoothStateReceiver) }
    }

    // Re-run the ConnectIQ restore against the persisted paired-watch id. No-ops safely when
    // there is no paired watch; won't clobber a live connection (guarded in restoreConnection).
    private fun restoreWatchConnection() {
        lifecycleScope.launch {
            val watchId = userRepository.getPairedWatchId()
            garminDeviceManager.restoreConnection(watchId, this@MainActivity)
        }
    }

    // Hand a Strava OAuth redirect (paceapp://strava-callback?code=…) to StravaManager,
    // which exchanges the code via a Cloud Function. Returns true if it was a Strava link.
    private fun handleStravaCallback(intent: Intent?): Boolean {
        val uri = intent?.data ?: return false
        if (uri.scheme == StravaConst.CALLBACK_SCHEME && uri.host == StravaConst.CALLBACK_HOST) {
            stravaManager.handleCallback(uri)
            return true
        }
        return false
    }

    // If the launching intent is a Firebase email sign-in link, kick off completion.
    // AuthManager emits Authenticating → Success/Failed phases which AppNavHost routes
    // on (Authenticating screen → dashboard/build-profile, or back to login).
    private fun handleEmailSignInLink(intent: Intent?) {
        val link = intent?.data?.toString() ?: return

        // Account-deletion re-auth: a returning email link for a signed-in user who
        // asked to delete their account. Reauthenticate then delete — do NOT run the
        // normal fresh-sign-in path. Mirrors iOS PaceApp.onOpenURL reauth branch.
        if (authManager.isReauthenticatingForDeletion && authManager.isSignedIn &&
            authManager.isEmailSignInLink(link)
        ) {
            lifecycleScope.launch {
                runCatching {
                    authManager.reauthenticateWithEmailLink(link).getOrThrow()
                    authManager.deleteAccount()
                }
                    .onSuccess {
                        AppAlerts.showToast(
                            getString(R.string.account_deleted_message),
                            type = MessageType.Success,
                        )
                        sessionManager.onSessionExpired() // routes to login (AppNavHost)
                    }
                    .onFailure {
                        authManager.isReauthenticatingForDeletion = false
                        AppAlerts.showToast(AuthErrorMapper.message(it), type = MessageType.Error)
                    }
            }
            return
        }

        authManager.handleIncomingLinkIfEmailSignIn(link)
    }
}
