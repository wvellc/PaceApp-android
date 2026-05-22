package net.paceapp.features.main.history.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors

@Composable
fun SwipeActionButtons(
    @DrawableRes id: Int = R.drawable.ic_delete,
    background: Color = AppColors.Error,
    shape: RoundedCornerShape = CircleShape,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .background(background)
            .defaultClickable(
                interactionSource = remember { MutableInteractionSource() },
                rippleColor = AppColors.Black,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id),
            contentDescription = null,
        )
    }
}
