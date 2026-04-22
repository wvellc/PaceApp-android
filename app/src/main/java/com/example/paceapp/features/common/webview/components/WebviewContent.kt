package com.example.paceapp.features.common.webview.components

import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.features.common.webview.WebviewContract.Event
import com.example.paceapp.features.common.webview.WebviewContract.State
import com.example.paceapp.theme.AppColors

@Composable
internal fun WebviewContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    var lastRequestedUrl by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            isInitialized = true
        }
    }
    val webViewAlpha by animateFloatAsState(
        targetValue = if (!isInitialized) 0f else 1f,
        animationSpec = tween(durationMillis = 400), // 400ms fade is the Android standard
        label = "WebView Fade"
    )

    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = false,
        hasPattern = true,
        appBar = { CommonAppBar(title = state.title) },
        appLoader = {}
    ) { innerPaddings ->

        val currentBottomPadding by rememberUpdatedState(innerPaddings.calculateBottomPadding().value)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPaddings.calculateTopPadding()),
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Transparent)
                    .alpha(webViewAlpha),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        setBackgroundColor(Color.TRANSPARENT)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            displayZoomControls = false
                            useWideViewPort = true
                            loadWithOverviewMode = true
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                onEvent(Event.OnPageStarted)
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                onEvent(Event.OnPageFinished)
                                val jsScript =
                                    """document.body.style.paddingBottom = '${currentBottomPadding}px';
                                       document.body.style.backgroundColor = 'transparent';
                                       document.documentElement.style.backgroundColor = 'transparent';
                                       """.trimIndent()
                                view?.evaluateJavascript(jsScript, null)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                return false // Ensure all URLs open in the WebView
                            }
                        }
                    }
                },
                update = { webView ->
                    // Safely apply dynamic settings
                    webView.settings.builtInZoomControls = state.isZoomEnabled
                    webView.settings.setSupportZoom(state.isZoomEnabled)
                    // Only load the URL
                    if (state.url.isNotBlank() && lastRequestedUrl != state.url) {
                        lastRequestedUrl = state.url
                        webView.loadUrl(state.url)
                    }
                }
            )

            AnimatedVisibility(
                visible = state.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AppColors.NeonAquaBlue,
                    strokeCap = StrokeCap.Round,
                    gapSize = 0.dp,
                    trackColor = AppColors.White20
                )
            }

        }
    }
}
