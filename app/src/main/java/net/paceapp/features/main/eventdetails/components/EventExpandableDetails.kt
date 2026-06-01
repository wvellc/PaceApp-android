package net.paceapp.features.main.eventdetails.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun EventExpandableDetails(
    title: String,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit = {},
    isExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    // Smooth rotation math (Rotates 180 degrees while switching icons)
    val iconRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "icon_rotation"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultClickable(onClick = onToggle)
                .padding(
                    top = 16.dp,
                    bottom = when {
                        isExpanded -> 8.dp
                        else -> 16.dp
                    }
                ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = AppTheme.typography.semiBold.copy(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    letterSpacing = 0.32.sp,
                    color = AppColors.DarkCharcoal,
                )
            )

            // --- ICON ANIMATION ---
            Crossfade(
                targetState = isExpanded,
                animationSpec = tween(durationMillis = 300),
                label = "icon_crossfade"
            ) { expanded ->
                Icon(
                    // Swap between minus and plus
                    painter = painterResource(if (expanded) R.drawable.ic_minus else R.drawable.ic_plus),
                    contentDescription = if (expanded) "Collapse Details" else "Expand Details",
                    tint = AppColors.RadiantBlue,
                    modifier = Modifier.graphicsLayer {
                        rotationZ = iconRotation
                    }
                )
            }
        }

        // --- EXPANDABLE CONTENT ---
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
            exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp) // Spacing for the child content
            ) {
                // Render the specific event data (like segments, intervals, etc.)
                content()
            }
        }
    }
}