package net.paceapp.features.main.createevent.steps

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppButtonStyle
import net.paceapp.features.main.createevent.components.EventCardContainer
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun SegmentChoiceStep(
    @StringRes titleRes: Int? = null,
    hasSegments: Boolean = false,
    onInfoClick: () -> Unit,
    onSegmentChoiceUpdated: (Boolean) -> Unit,
) {
    EventCardContainer(
        titleRes = titleRes,
        showInfo = true,
        onInfoClick = onInfoClick
    ) {
        val idleAlpha = 0.25f

        //Animate the alpha
        val yesAlpha by animateFloatAsState(
            targetValue = if (hasSegments) 1f else idleAlpha,
            label = "YesAlpha"
        )

        val noAlpha by animateFloatAsState(
            targetValue = if (!hasSegments) 1f else idleAlpha,
            label = "NoAlpha"
        )

        val textStyle = AppTheme.typography.medium.copy(
            fontSize = 18.sp, lineHeight = 24.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Yes button
            AppButton(
                modifier = Modifier
                    .weight(1f)
                    .alpha(yesAlpha)
                    .border(
                        width = 1.2.dp,
                        color = AppColors.FashionGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                title = stringResource(R.string.yes),
                cornerShape = RoundedCornerShape(8.dp),
                height = 46.dp,
                style = AppButtonStyle.NONE,
                textStyle = textStyle.copy(
                    color = AppColors.DarkCharcoal
                ),
                backgroundColor = AppColors.FluorescentMint,
                onClick = { onSegmentChoiceUpdated(true) }
            )
            //No button
            AppButton(
                modifier = Modifier
                    .weight(1f)
                    .alpha(noAlpha)
                    .border(
                        width = 1.2.dp,
                        color = AppColors.FashionGray,
                        shape = RoundedCornerShape(8.dp)
                    ),
                title = stringResource(R.string.no),
                cornerShape = RoundedCornerShape(8.dp),
                height = 46.dp,
                style = AppButtonStyle.NONE,
                textStyle = textStyle.copy(
                    color = AppColors.White
                ),
                backgroundColor = AppColors.Error,
                onClick = { onSegmentChoiceUpdated(false) }
            )
        }
    }
}