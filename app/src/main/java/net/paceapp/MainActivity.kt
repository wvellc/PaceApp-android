package net.paceapp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import net.paceapp.core.components.AppActionDialog
import net.paceapp.core.components.CustomToast
import net.paceapp.navigation.AppNavHost
import net.paceapp.session.AppSessionManager
import net.paceapp.theme.PaceAppTheme
import com.wvelabs.core_ui.alerts.AppAlertContainer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var sessionManager: AppSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
}
