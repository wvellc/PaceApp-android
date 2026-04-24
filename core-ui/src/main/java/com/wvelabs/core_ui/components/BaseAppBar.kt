package com.wvelabs.core_ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BaseAppBar(
    title: String? = null,
    titleStyle: TextStyle = TextStyle.Default,
    titleContent: (@Composable () -> Unit)? = null,
    showBackButton: Boolean = true,
    isCollapsed: Boolean = true,
    collapsedHeight: Dp = 56.dp,
    expandedHeight: Dp = 56.dp,
    backButtonIcon: @Composable () -> Unit = { /* Default Back Icon */ },
    background: @Composable BoxScope.() -> Unit = {},
    actionsArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    actions: @Composable RowScope.() -> Unit = {},
) {
    val currentHeight by animateDpAsState(
        targetValue = if (isCollapsed) collapsedHeight else expandedHeight,
        label = "AppBarHeight"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(currentHeight)
            .statusBarsPadding()
    ) {
        // Background Layer
        background()

        // Centered Title Layer (This stays in the absolute center of the screen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Use a safe horizontal padding to prevent the title
                // from ever touching/overlapping the buttons.
                // 72dp-80dp is usually safe for 1-2 icons.
                .padding(horizontal = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (titleContent != null) {
                titleContent()
            } else if (title != null) {
                Text(
                    text = title,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = titleStyle,
                )
            }
        }

        // Back Button
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    backButtonIcon()
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }

        // Actions
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = actionsArrangement,
            content = actions
        )
    }
}