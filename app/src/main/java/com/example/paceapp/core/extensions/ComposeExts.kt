package com.example.paceapp.core.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.paceapp.ui.theme.AppColors
import com.kyant.capsule.continuities.G2Continuity
import com.kyant.capsule.continuities.G2ContinuityProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Modifier.defaultClickable(
    rippleColor: Color? = AppColors.NeonAquaBlue20  ,
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