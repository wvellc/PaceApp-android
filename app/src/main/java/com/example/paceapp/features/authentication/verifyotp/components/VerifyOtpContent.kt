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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.paceapp.core.components.AppTextButton
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.components.animation.FadeInUpWrapper
import com.example.paceapp.core.components.animation.fadeInUpTransition
import com.example.paceapp.core.components.otpfield.OtpField
import com.example.paceapp.core.extensions.clearFocusOnTap
import com.example.paceapp.core.extensions.verticalScrollOnIme
import com.example.paceapp.features.authentication.data.enums.LoginTypes
import com.example.paceapp.features.authentication.data.enums.title
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
internal fun VerifyOtpContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val title = when (state.loginType) {
        LoginTypes.EMAIL -> stringResource(R.string.email_verification)
        LoginTypes.PHONE -> stringResource(R.string.phone_verification)
    }

    val (otp, onOtpChange) = remember { mutableStateOf("") }

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(LocalFocusManager.current),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = { CommonAppBar(title = title) },
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
                otpValue = otp,
                onOtpValueChange = onOtpChange,
                otpLength = 6,
                shape = RoundedCornerShape(12.dp), // Dynamic shape
                isError = false
            )
            Spacer(Modifier.height(24.dp))

            AppTextButton(
                "Resend Code",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {},
                style = AppTheme.typography.size14.copy(
                    color = AppColors.NeonAquaBlue,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpScreenPreview() = VerifyOtpContent(state = State(), onEvent = {})
