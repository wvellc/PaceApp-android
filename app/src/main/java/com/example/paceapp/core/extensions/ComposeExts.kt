package com.example.paceapp.core.extensions

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.example.paceapp.theme.AppColors
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.continuities.G2ContinuityProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Modifier.defaultClickable(
    rippleColor: Color? = AppColors.NeonAquaBlue20,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    debounceTime: Long = 500L,
    enable: Boolean = true,// 500 milliseconds is the standard UX sweet spot
    onClick: () -> Unit,
): Modifier {
    val scope = rememberCoroutineScope()
    var isClickable by remember { mutableStateOf(true) }

    return this.clickable(
        enabled = isClickable && enable,
        interactionSource = interactionSource,
        indication = rippleColor?.let { ripple(bounded = true, color = it) },
        onClick = {
            if (isClickable) {
                onClick()          // Fire the action
                isClickable = false // Lock the button

                // 3. Launch a coroutine to unlock it after the delay
                scope.launch {
                    delay(debounceTime)
                    isClickable = true
                }
            }
        }
    )
}

/**
 * Extension to clear focus (and hide the keyboard) when tapping outside of a text field.
 */
fun Modifier.clearFocusOnTap(focusManager: FocusManager): Modifier =
    this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            focusManager.clearFocus()
        })
    }


/**
 * Automatically applies IME padding, makes the column scrollable,
 * and glides to the absolute bottom whenever the software keyboard opens.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Modifier.verticalScrollOnIme(
    // 🚀 We allow the parent to pass one, but default to our own if they don't!
    scrollState: ScrollState = rememberScrollState()
): Modifier {
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            delay(100)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    // Chain the required modifiers and return them
    return this
        .imePadding()
        .verticalScroll(scrollState)
}
val g2Continuity = G2Continuity(
    profile = G2ContinuityProfile.RoundedRectangle.copy(
        extendedFraction = 0.5,
        arcFraction = 0.5,
        bezierCurvatureScale = 1.1,
        arcCurvatureScale = 1.1
    ),
    capsuleProfile = G2ContinuityProfile.Capsule.copy(
        extendedFraction = 0.5,
        arcFraction = 0.25
    )
)