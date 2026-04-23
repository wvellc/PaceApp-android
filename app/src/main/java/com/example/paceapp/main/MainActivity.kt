package com.example.paceapp.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.paceapp.navigation.AppNavHost
import com.example.paceapp.theme.PaceAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen
        val splashScreen = installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.Companion.dark(Color.TRANSPARENT)
        )
//        splashScreen.setKeepOnScreenCondition {
//            !mainViewModel.isSdkReady.value
//        }

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            PaceAppTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}

