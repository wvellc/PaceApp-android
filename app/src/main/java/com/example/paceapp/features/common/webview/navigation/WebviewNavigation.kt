package com.example.paceapp.features.common.webview.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.paceapp.features.common.webview.WebviewScreen
import kotlinx.serialization.Serializable

@Serializable
data class WebviewRoute(
    val url: String,
    val title: String? = null,
    val isZoomEnabled: Boolean = false
)


fun NavGraphBuilder.webviewScreen(
) {
    composable<WebviewRoute> {
        WebviewScreen()
    }
}
