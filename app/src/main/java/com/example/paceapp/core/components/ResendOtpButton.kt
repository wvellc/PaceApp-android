package com.example.paceapp.core.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.convertMillisToMinSec
import kotlin.time.Duration

@Composable
fun ResendOtpButton(
    modifier: Modifier,
    duration: Duration,
    onClick: () -> Unit,
) {
    val isTimerRunning = duration > Duration.ZERO

    val buttonContent = when {
        isTimerRunning -> buildAnnotatedString {
            append(stringResource(R.string.didn_t_receive_otp_resend_in))
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum"
                )
            ) {
                append(duration.inWholeMilliseconds.convertMillisToMinSec())
            }
        }

        else -> AnnotatedString(stringResource(R.string.resend_code))
    }
    val timerTextStyle = when {
        isTimerRunning -> AppTheme.typography.medium
        else -> AppTheme.typography.semiBold
    }
    AppTextButton(
        text = buttonContent,
        onClick = onClick,
        modifier = modifier,
        enabled = !isTimerRunning,
        disabledContentColor = AppColors.White,
        style = timerTextStyle.copy(
            fontSize = 14.sp
        ),
    )
}
