package com.wvelabs.core_ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
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
    titleContent: @Composable (RowScope.() -> Unit)? = null,
    showBackButton: Boolean = true,
    isCollapsed: Boolean = true,
    collapsedHeight: Dp = 56.dp,
    expandedHeight: Dp = 56.dp,// If these are equal, it behaves as a normal App Bar
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
        // 1. Background Layer (Bottom)
        background()

        // 2. Control Layer (Top)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK BUTTON SLOT
            if (showBackButton) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    backButtonIcon()
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }

            // DYNAMIC TITLE AREA
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                when {
                    titleContent != null -> {
                        Row(content = titleContent)
                    }

                    title != null -> {
                        Text(
                            text = title,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            style = titleStyle
                        )
                    }
                }
            }

            // ACTIONS SLOT
            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = actionsArrangement,
                content = actions
            )

            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}