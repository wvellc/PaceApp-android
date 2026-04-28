package com.example.paceapp.features.authentication.verifyotp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppButtonStyle
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.OtpField
import com.example.paceapp.core.components.ResendOtpButton
import com.example.paceapp.core.components.animation.FadeInUpWrapper
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.core.utils.AppConstants
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.data.enums.title
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.wvelabs.core_ui.extensions.fadeInUpTransition
import kotlin.time.Duration


@Composable
internal fun VerifyOtpContent(
    state: State,
    onEvent: (Event) -> Unit,
    otpDuration: Duration = Duration.ZERO,
    otpLength: Int = AppConstants.OTP_LENGTH
) {
    val title = when (state.loginType) {
        LoginTypes.EMAIL -> stringResource(R.string.email_verification)
        LoginTypes.PHONE -> stringResource(R.string.phone_verification)
    }
    val focusManager = LocalFocusManager.current
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(focusManager),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            CommonAppBar(title = title, onBackClick = {
                focusManager.clearFocus(force = true)
                onEvent(Event.OnBackClick)
            })
        },
        animationWrapper = { content -> FadeInUpWrapper(enterTransition = fadeInUpTransition(from = 0.02f)) { content() } }
    ) { innerPaddings ->
        //Scroll container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollOnIme()
                .padding(innerPaddings)
                .padding(16.dp),
        ) {
            //Title
            Text(
                modifier = Modifier.padding(2.dp),
                text = stringResource(
                    R.string.verify_otp_message,
                    state.loginType.title.lowercase()
                ),
                style = AppTheme.typography.size20.copy(
                    fontWeight = FontWeight.Medium,
                    color = AppColors.White,
                    lineHeight = 32.sp
                )
            )
            Spacer(Modifier.height(40.dp))

            OtpField(
                modifier = Modifier.fillMaxWidth(),
                otpValue = state.otp,
                onOtpValueChange = { onEvent(Event.OnOtpChange(it)) },
                otpLength = otpLength,
                shape = RoundedCornerShape(12.dp), // Dynamic shape
                isError = false
            )
            Spacer(Modifier.height(24.dp))
            //Resend button
            ResendOtpButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                duration = otpDuration,
                onClick = {
                    onEvent(Event.OnResendOtpClicked)
                },
            )
            Spacer(Modifier.weight(1f))
            //Next button
            AppButton(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(R.string.next),
                enabled = state.otp.length >= otpLength
            ) {
                focusManager.clearFocus(force = true)
                onEvent(Event.OnNextClick)
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() = VerifyOtpContent(state = State(), onEvent = {})
