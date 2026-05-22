package net.paceapp.features.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.models.UserUiModel
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
fun HomeGreetingHeader(
    modifier: Modifier = Modifier,
    user: UserUiModel?,
    subtitle: String, ) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = stringResource(
                R.string.greetings_user, user?.firstName ?: ""
            ), style = AppTheme.typography.bold.copy(
                color = AppColors.White,
                lineHeight = 24.sp,
                fontSize = 24.sp,
            )
        )
        Text(
            text =subtitle, style = AppTheme.typography.medium.copy(
                fontSize = 13.sp,
                color = AppColors.White.copy(alpha = 0.5f),
                lineHeight = 13.sp,
            )
        )
    }
}
