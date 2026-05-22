package net.paceapp.features.main.home.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
fun HomeSquareButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.ic_new_event,
    @StringRes titleRes: Int = R.string.new_event,
    iconSize: Dp = 70.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(ContinuousRoundedRectangle(16.dp))
            .background(AppColors.White)
            .defaultClickable(
                onClick = onClick
            )
    ) {

        Image(
            painter = painterResource(R.drawable.ic_run_faded),
            contentDescription = null,
            modifier = Modifier
                .heightIn(105.dp)
                .wrapContentWidth()
                .align(Alignment.BottomEnd)
                .offset(y = 12.dp, x = (-3).dp)
                .alpha(0.8f)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(iconRes),
                contentDescription = stringResource(titleRes),
                modifier = Modifier.size(iconSize)
            )

            Text(
                text = stringResource(titleRes),
                style = AppTheme.typography.semiBold.copy(
                    fontSize = 17.sp,
                    color = AppColors.DarkCharcoal,
                    lineHeight = 17.sp,
                    letterSpacing = 0.34.sp,
                )
            )
        }

    }
}

