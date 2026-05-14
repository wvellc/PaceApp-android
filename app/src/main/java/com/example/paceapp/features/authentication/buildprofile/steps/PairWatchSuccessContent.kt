package com.example.paceapp.features.authentication.buildprofile.steps

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.animation.AnimationWrapper
import com.example.paceapp.core.garmin.models.WatchModel
import com.example.paceapp.features.authentication.buildprofile.components.RippleContainer
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
    val screenWidth = LocalWindowInfo.current.containerSize.width.dp

    AnimationWrapper {
        RippleContainer(
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
            circleSize = 400.dp,
            staggerDelayMillis = 800L,
            circleColor = AppColors.White,
            rippleCount = 4,
            peakAlpha = 0.2f,
        ) {
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
                    style = AppTheme.typography.semiBold.copy(
                        color = AppColors.White,
                        fontSize = 24.sp,
                    ),
                )
            }
        }
    }
}