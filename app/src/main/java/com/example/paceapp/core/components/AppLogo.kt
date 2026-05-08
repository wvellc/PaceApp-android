package com.example.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

enum class LogoStyle {
    Horizontal, Vertical
}

@Composable
fun AppLogo(
    modifier: Modifier,
    showLabel: Boolean = true,
    logoStyle: LogoStyle = LogoStyle.Horizontal,
    imageSize: DpSize = DpSize(154.dp, 104.dp),
    titleTextStyle: TextStyle = AppTheme.typography.size34,
) {
    when (logoStyle) {
        LogoStyle.Horizontal -> Row(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(36.dp, 24.dp)
            )
            if (showLabel) {
                Text(
                    text = stringResource(R.string.app_name_capitalize),
                    color = AppColors.White,
                    style = titleTextStyle.copy(
                        fontSize = 19.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                )
            }
        }

        LogoStyle.Vertical -> Column(
            verticalArrangement = Arrangement.spacedBy(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            if (showLabel) {
                Text(
                    text = stringResource(R.string.app_name_capitalize),
                    color = AppColors.White,
                    style = titleTextStyle.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                )
            }
        }
    }

}