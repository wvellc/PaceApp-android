package net.paceapp.core.components.animation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import com.wvelabs.core_ui.extensions.defaultAnimSpec
import com.wvelabs.core_ui.extensions.fadeInUpTransition


@Composable
fun FadeInUpWrapper(
    initialOffsetFraction: Float = 0.5f,
    enterTransition: EnterTransition = fadeInUpTransition(from = initialOffsetFraction),
    content: @Composable AnimatedVisibilityScope.() -> Unit
) = AnimationWrapper(
    content = content,
    enter = enterTransition,
)