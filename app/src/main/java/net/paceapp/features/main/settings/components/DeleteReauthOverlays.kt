package net.paceapp.features.main.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.paceapp.R
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppTextButton
import net.paceapp.core.components.OtpField
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.SettingsContract.ReauthPhase
import net.paceapp.features.main.settings.SettingsContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Account-deletion re-authentication surfaces. Mirrors iOS ReauthDeleteSheets.
// Rendered above SettingsContent; each phase is a modal Dialog and the blocking
// processing overlay floats above everything while the deletion runs.
@Composable
internal fun DeleteReauthOverlays(
    state: State,
    onEvent: (Event) -> Unit,
) {
    when (val phase = state.reauthPhase) {
        is ReauthPhase.PhoneOtp -> ReauthOtpDialog(phase = phase, onEvent = onEvent)
        is ReauthPhase.EmailWait -> EmailReauthWaitDialog(phase = phase, onEvent = onEvent)
        null -> Unit
    }

    // Blocking overlay while deleting — disables interaction + back.
    if (state.isDeleting) ProcessingOverlay()
}

// Phone reauth — enter the OTP sent to the signed-in number, then delete. Verifying
// does NOT sign the user out.
@Composable
private fun ReauthOtpDialog(
    phase: ReauthPhase.PhoneOtp,
    onEvent: (Event) -> Unit,
) {
    Dialog(
        onDismissRequest = { if (!phase.isVerifying) onEvent(Event.OnReauthCancel) },
        properties = DialogProperties(
            dismissOnBackPress = !phase.isVerifying,
            dismissOnClickOutside = false,
        ),
    ) {
        DialogSurface {
            Text(
                text = stringResource(R.string.reauth_confirm_title),
                style = AppTheme.typography.bold.copy(fontSize = 22.sp, color = AppColors.White),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.reauth_otp_message, phase.phone),
                style = AppTheme.typography.medium.copy(fontSize = 14.sp, color = AppColors.White85),
                textAlign = TextAlign.Center,
            )
            OtpField(
                modifier = Modifier.fillMaxWidth(),
                otpValue = phase.otp,
                onOtpValueChange = { onEvent(Event.OnReauthOtpChanged(it)) },
                enabled = !phase.isVerifying,
            )
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.reauth_verify_and_delete),
                enabled = phase.otp.length == 6 && !phase.isVerifying,
                onClick = { onEvent(Event.OnReauthOtpSubmit) },
            )
            AppTextButton(
                text = stringResource(R.string.cancel),
                style = AppTheme.typography.semiBold.copy(fontSize = 14.sp, color = AppColors.White85),
                onClick = { if (!phase.isVerifying) onEvent(Event.OnReauthCancel) },
            )
        }
    }
}

// Email reauth — the link was sent to the signed-in email. Deletion runs from
// MainActivity once the user taps it; this dialog just waits.
@Composable
private fun EmailReauthWaitDialog(
    phase: ReauthPhase.EmailWait,
    onEvent: (Event) -> Unit,
) {
    Dialog(
        onDismissRequest = { onEvent(Event.OnReauthCancel) },
        properties = DialogProperties(dismissOnClickOutside = false),
    ) {
        DialogSurface {
            CircularProgressIndicator(color = AppColors.White)
            Text(
                text = stringResource(R.string.reauth_email_title),
                style = AppTheme.typography.bold.copy(fontSize = 22.sp, color = AppColors.White),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.reauth_email_message, phase.email),
                style = AppTheme.typography.medium.copy(fontSize = 14.sp, color = AppColors.White85),
                textAlign = TextAlign.Center,
            )
            AppTextButton(
                text = stringResource(R.string.cancel),
                style = AppTheme.typography.semiBold.copy(fontSize = 14.sp, color = AppColors.White85),
                onClick = { onEvent(Event.OnReauthCancel) },
            )
        }
    }
}

// Full-screen blocking scrim shown while the account is being deleted.
@Composable
private fun ProcessingOverlay() {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Black40),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = AppColors.White)
        }
    }
}

// Shared gradient card used by the reauth dialogs (matches the app background).
@Composable
private fun DialogSurface(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(AppColors.backgroundGradient))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        content()
    }
}
