package net.paceapp.features.main.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.core.components.AppDialogOverlay
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.features.main.home.models.WatchMetric
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun WatchMetricsInfoDialog(
    metricInfo: WatchMetric,
    currentIndex: Int = 0,
    totalSteps: Int = 1,
    cancelable: Boolean = false,
    onCloseDialog: () -> Unit = {},
    onNextClick: () -> Unit,
    onPrevClick: () -> Unit
) {

    AppDialogOverlay(
        cancelable = cancelable,
        onDismissRequest = {
            if (cancelable) {
                onCloseDialog()
            }
        },
    ) { dismiss ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.screenPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //Close button
            Image(
                painter = painterResource(R.drawable.ic_close_white),
                contentDescription = stringResource(R.string.close),
                modifier = Modifier
                    .clip(CircleShape)
                    .defaultClickable(onClick = { dismiss() })
                    .align(Alignment.End)
            )

            //Dialog
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.White)
                    .padding(16.dp)
            ) {
                //Content
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Stepper
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(totalSteps) { index ->

                            val animatedAlpha by animateFloatAsState(
                                targetValue = if (currentIndex >= index) 1f else 0.2f,
                                label = "step_alpha_anim"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(AppColors.NeonAquaBlue.copy(alpha = animatedAlpha))
                            )
                        }
                    }

                    //Animated metric info
                    AnimatedContent(
                        targetState = Pair(currentIndex, metricInfo),
                        transitionSpec = {
                            if (targetState.first > initialState.first) {
                                // Moving Forward: Slide in from right, fade out to left
                                (slideInHorizontally { width -> width / 2 } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width / 2 } + fadeOut()
                                )
                            } else {
                                // Moving Backward: Slide in from left, fade out to right
                                (slideInHorizontally { width -> -width / 2 } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width / 2 } + fadeOut()
                                )
                            }
                        },
                        label = "metric_content_animation",
                    ) { (_, metric) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            //Icon
                            Image(
                                painter = painterResource(metric.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(128.dp),
                            )
                            Spacer(Modifier.height(24.dp))
                            //Title
                            Text(
                                text = stringResource(id = metric.titleRes),
                                textAlign = TextAlign.Center,
                                style = AppTheme.typography.medium.copy(
                                    fontSize = 24.sp,
                                    lineHeight = 24.sp,
                                    color = AppColors.DarkCharcoal,
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            //Description
                            Text(
                                text = stringResource(id = metric.descriptionRes),
                                textAlign = TextAlign.Center,
                                style = AppTheme.typography.medium.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp,
                                    color = AppColors.FashionGray,
                                )
                            )
                        }
                    }
                    val animationProgress by animateFloatAsState(
                        targetValue = if (currentIndex > 0) 1f else 0f,
                        animationSpec = tween(durationMillis = 400),
                        visibilityThreshold = 0.001f,
                        label = "ButtonAnimationProgress"
                    )

                    //Bottom buttons
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        if (animationProgress > 0f) {
                            val microStepWeight =
                                if (animationProgress > 0.01f) animationProgress else 0.001f
                            AppButton(
                                title = stringResource(R.string.previous),
                                onClick = onPrevClick,
                                backgroundColor = AppColors.HintGray,
                                contentColor = AppColors.Error,
                                style = AppButtonStyle.NONE,
                                modifier = Modifier
                                    .weight(microStepWeight)
                                    .graphicsLayer {
                                        val currentScale = 0.4f + (animationProgress * 0.6f)
                                        alpha = animationProgress       // Fades out to 0
                                        scaleX = currentScale      // Shrinks horizontally
                                        scaleY = currentScale      // Shrinks vertically
                                    }
                                    .clipToBounds()
                            )
                        }
                        //Next or Done button
                        AppButton(
                            title = when {
                                currentIndex == totalSteps - 1 -> stringResource(R.string.done)
                                else -> stringResource(R.string.next)
                            },
                            onClick = onNextClick,
                            style = AppButtonStyle.FILLED_GRADIENT,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}