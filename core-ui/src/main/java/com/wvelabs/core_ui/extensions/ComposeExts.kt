package com.wvelabs.core_ui.extensions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutBack
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Dp.toPx() = with(LocalDensity.current) { this@toPx.roundToPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Int.pxToSp() = with(LocalDensity.current) { this@pxToSp.toSp() }


fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Float = 0.01f, // Use Float for better animation control
    offsetY: Float = 0f,
    offsetX: Float = 0f,
) = drawBehind {

    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()

    drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = transparentColor
        frameworkPaint.setShadowLayer(
            shadowBlurRadius,
            offsetX,
            offsetY,
            shadowColor
        )
        it.drawRoundRect(
            0f,
            0f,
            this.size.width,
            this.size.height,
            cornersRadius.toPx(),
            cornersRadius.toPx(),
            paint
        )
    }
}

fun Modifier.bounceClick(
    animationDuration: Int = 300,
    scaleDown: Float = 0.9f,
    onClick: () -> Unit,
): Modifier = composed {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val animateScale = remember { Animatable(1f) }

    Modifier
        .graphicsLayer {
            val scale = animateScale.value
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                coroutineScope.launch {
                    launch {
                        val animationSpec = defaultAnimSpec<Float>(duration = animationDuration)
                        animateScale.animateTo(scaleDown, animationSpec = animationSpec)
                        animateScale.animateTo(1f, animationSpec)
                    }
                    delay(animationDuration.toLong())
                    onClick()
                }
            })
        }
}

fun <T> defaultAnimSpec(
    duration: Int = 800,
    delay: Int = 0,
    easing: Easing = EaseInOutBack,
): TweenSpec<T> =
    tween(easing = easing, durationMillis = duration, delayMillis = delay)

fun fadeInUpTransition(
    index: Int,
    duration: Int = 800,
    delay: Int = 100,
    offset: Int = 50,
): EnterTransition {
    val totalDelay = delay * index
    return fadeIn(defaultAnimSpec(duration, totalDelay)) + slideInVertically(
        defaultAnimSpec(duration, totalDelay)
    ) { offset + (index * 12) }
}

fun fadeInDownTransition(
    index: Int,
    duration: Int = 800,
    delay: Int = 100,
    offset: Int = 50,
): EnterTransition {
    val totalDelay = delay * index
    return fadeIn(defaultAnimSpec(duration, totalDelay)) + slideInVertically(
        defaultAnimSpec(duration, totalDelay)
    ) { -(offset + (index * 12)) }
}

fun slideInLeftTransition(
    index: Int,
    duration: Int = 800,
    delay: Int = 100,
    offset: Int = 50,
): EnterTransition {
    return fadeIn(defaultAnimSpec(duration, delay)) + slideInHorizontally(
        defaultAnimSpec(duration, delay)
    ) { offset + (index * 12) }
}
