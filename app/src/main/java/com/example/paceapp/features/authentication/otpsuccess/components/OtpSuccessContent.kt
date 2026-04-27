package com.example.paceapp.features.authentication.otpsuccess.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppSuccessView
import com.example.paceapp.features.authentication.data.enums.title
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.Event
import com.example.paceapp.features.authentication.otpsuccess.OtpSuccessContract.State

@Composable
internal fun OtpSuccessContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val loginTypeTitle = state.loginType.title.lowercase()
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
    ) { innerPaddings ->
        AppSuccessView(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings),
            title = stringResource(R.string.otp_success_title, loginTypeTitle),
            subtitle = stringResource(R.string.otp_success_subtitle),
            logoRes = R.drawable.ic_otp_success,
            buttonLabel = stringResource(R.string.get_started),
            onButtonClick = {
                onEvent(Event.OnContinueClicked)
            }
        )
    }
}
