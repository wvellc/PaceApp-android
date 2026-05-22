package net.paceapp.core.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun AnimatedBellIcon(
    initialTilt: Float = 15f,
    delayMillis: Long = 300L,
    @DrawableRes iconRes: Int = R.drawable.ic_notification_bell,
    onClick: () -> Unit,
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        // Snap to the side immediately
        rotation.animateTo(initialTilt, animationSpec = tween(durationMillis = 150))
        // Ring out until it settles at 0
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy, // Creates the back-and-forth "ring"
                stiffness = Spring.StiffnessMediumLow         // Controls the speed of the settle
            )
        )
    }

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AppColors.White)
            .defaultClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = rotation.value
                    // Pivot at the handle (Top Center)
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        )
    }
}