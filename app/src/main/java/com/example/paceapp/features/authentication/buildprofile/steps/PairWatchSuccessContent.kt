package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.paceapp.R
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.core.garmin.WatchModel
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.AppNetworkImage
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
fun PairWatchSuccessContent(
    device: WatchModel? = null,
) {
    //Check device for safety purpose
    if (device == null) {
        return
    }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    // Duration of one full breath in/out
    val animationDuration = 2000

    // Inner Circle (Starts immediately)
    val minScale = 1f
    val maxScale = 1.1f
    val scaleInner by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_inner"
    )

    // Middle Circle (Offset by 300ms so it follows the inner one)
    val scaleMiddle by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(
                offsetMillis = (animationDuration * 0.2).toInt(),
                offsetType = StartOffsetType.FastForward
            )
        ),
        label = "pulse_middle"
    )

    // Outer Circle (Offset by 600ms so it trails the middle one)
    val scaleOuter by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(
                offsetMillis = (animationDuration * 0.45).toInt(),
                offsetType = StartOffsetType.FastForward
            )
        ),
        label = "pulse_outer"
    )

    val circleColor = Color.White.copy(alpha = 0.10f)
    AnimationWrapper {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .requiredWidth(screenWidth)
                .wrapContentWidth(unbounded = true)
                .animateEnterExit(
                    enter = fadeIn(
                        animationSpec = defaultAnimSpec(
                            duration = 500,
                            easing = EaseInOut
                        )
                    ) + scaleIn(initialScale = 0.6f)
                ),
            contentAlignment = Alignment.Center
        ) {
            //Outer Circle
            // Using requiredSize so it freely bleeds off the edges of the screen
            Box(
                modifier = Modifier
                    .requiredSize(492.dp)
                    .scale(scaleOuter)
                    .background(color = circleColor, shape = CircleShape)
            )

            //Middle Circle
            Box(
                modifier = Modifier
                    .requiredSize(400.dp)
                    .scale(scaleMiddle)
                    .background(color = circleColor, shape = CircleShape)
            )

            //Inner Circle
            Box(
                modifier = Modifier
                    .requiredSize(318.dp)
                    .scale(scaleInner)
                    .background(color = circleColor, shape = CircleShape)
            )


            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                //Device image
                AppNetworkImage(
                    imageUrl = null,
                    placeholder = painterResource(id = R.drawable.ic_selected_watch_placeholder),
                    size = 240.dp,
                    contentScale = ContentScale.Inside,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.height(38.dp))
                //Device name
                Text(
                    text = device.name,
                    style = AppTheme.typography.size24.copy(
                        color = AppColors.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        }
    }
}