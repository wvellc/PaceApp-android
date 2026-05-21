package com.example.paceapp.core.extensions

import androidx.compose.foundation.LocalIndication
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.example.paceapp.theme.AppColors
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.continuities.G2ContinuityProfile
import com.wvelabs.core_ui.alerts.MessageType
import kotlinx.coroutines.delay

/**
 * A highly performant, debounce-protected clickable modifier.
 * It prevents double-navigation without triggering UI recompositions.
 */
fun Modifier.defaultClickable(
    interactionSource: MutableInteractionSource? = null,
    rippleColor: Color? = AppColors.NeonAquaBlue20,
    debounceTime: Long = 500L,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {

    val actualInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val indication = rippleColor?.let {
        ripple(bounded = true, color = it)
    }
    val lastClickTime = remember { longArrayOf(0L) }

    this.clickable(
        enabled = enabled,
        interactionSource = actualInteractionSource, // 3. Use the resolved source here
        indication = indication,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime[0] > debounceTime) {
                lastClickTime[0] = currentTime
                onClick()
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
    scrollState: ScrollState = rememberScrollState(),
    delayMs: Long = 100L
): Modifier {
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            delay(delayMs)
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

//Message type colors
val MessageType.tintColor: Color
    get() = when (this) {
        MessageType.Success -> AppColors.FluorescentMint
        MessageType.Error -> AppColors.Error
        MessageType.Warning -> AppColors.Orange
        MessageType.Info -> AppColors.RadiantBlue
        MessageType.Loading -> AppColors.HintGray
    }
