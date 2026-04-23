package com.example.paceapp.features.authentication.verifyotp.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.State
import com.example.paceapp.features.authentication.verifyotp.VerifyOtpContract.Event

@Composable
internal fun VerifyOtpContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "VerifyOtp Screen")
    }
}
