package net.paceapp.core.components.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.wvelabs.core_ui.extensions.defaultAnimSpec

@Composable
fun AnimationWrapper(
    enter: EnterTransition = fadeIn(animationSpec = defaultAnimSpec(duration = 300)),
    exit: ExitTransition = ExitTransition.None,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    // State to trigger the animation
    var isVisible by rememberSaveable { mutableStateOf(false) }

    // Trigger it exactly once when the screen first composes
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,// You can leave the container enter blank if you want the children to handle everything
        enter = enter,
        exit = exit
    ) {
        // Yield to the Scaffold/Content
        content()
    }
}