package net.paceapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.components.AppActionDialog
import net.paceapp.core.components.CustomToast
import net.paceapp.navigation.AppNavHost
import net.paceapp.session.AppSessionManager
import net.paceapp.theme.PaceAppTheme
import com.wvelabs.core_ui.alerts.AppAlertContainer
import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: AppSessionManager

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        // Complete a passwordless email-link sign-in if the app was opened via the link.
        handleEmailSignInLink(intent)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT)
        )

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

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
                        sessionManager = sessionManager
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleEmailSignInLink(intent)
    }

    // If the launching intent is a Firebase email sign-in link, complete it. The
    // splash screen re-evaluates the session shortly after and routes accordingly.
    private fun handleEmailSignInLink(intent: Intent?) {
        val link = intent?.data?.toString() ?: return
        if (!authManager.isEmailSignInLink(link)) return
        lifecycleScope.launch {
            authManager.completeEmailLink(link)
                .onFailure {
                    AppLogger.e("Email link sign-in failed", it)
                    AppAlerts.showToast(it.message.orEmpty(), type = MessageType.Error)
                }
        }
    }
}
