package com.example.paceapp.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.paceapp.R
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.example.paceapp.theme.PaceAppTheme
import com.wvelabs.core_ui.alerts.AlertType
import com.wvelabs.core_ui.alerts.MessageType
import com.wvelabs.core_ui.alerts.UiText
import com.wvelabs.core_ui.alerts.icon
import com.wvelabs.core_ui.components.PureComposeOverlayDialog


@Composable
fun AppActionDialog(
    alert: AlertType.Dialog,
    closeDialog: () -> Unit = {}
) {
    // Enforce cancelable rules natively
    val properties = DialogProperties(
        dismissOnBackPress = alert.cancelable,
        dismissOnClickOutside = alert.cancelable
    )

    PureComposeOverlayDialog(
        cancelable = alert.cancelable,
        modifier = Modifier.padding(24.dp),
        overlayColor = AppColors.NeonAquaBlue20,
        onDismissRequest = {
            if (alert.cancelable) {
                closeDialog()
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.White)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Icon ---
            if (alert.showIcon) {
                val iconPainter = if (alert.iconRes != null) {
                    painterResource(id = alert.iconRes!!)
                } else {
                    rememberVectorPainter(image = alert.type.icon)
                }

                Image(
                    painter = iconPainter,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Title ---
            Text(
                text = alert.title.asString(),
                style = AppTheme.typography.medium.copy(
                    fontSize = 24.sp,
                    color = AppColors.DarkCharcoal,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- Description ---
            val textValue = alert.text.asString()
            if (textValue.isNotBlank()) {
                Text(
                    text = textValue,
                    style = AppTheme.typography.medium.copy(
                        color = AppColors.FashionGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Buttons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (alert.dismissText != null) {
                    AppButton(
                        title = alert.dismissText?.asString() ?: "",
                        onClick = {
                            alert.onDismiss?.invoke()
                            closeDialog()
                        },
                        backgroundColor = AppColors.HintGray,
                        contentColor = AppColors.Error,
                        style = AppButtonStyle.NONE,
                        modifier = Modifier.weight(1f)
                    )
                }

                AppButton(
                    title = alert.confirmText.asString(),
                    onClick = {
                        println("DEBUG: Confirm clicked")
                        alert.onConfirm?.invoke()
                        println("DEBUG: onConfirm finished successfully")
                        closeDialog()
                        println("DEBUG: closeDialog triggered")
                    },
                    style = AppButtonStyle.FILLED_GRADIENT,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Full Dialog", showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun AppActionDialogPreview_Full() {
    PaceAppTheme {
        AppActionDialog(
            alert = AlertType.Dialog(
                title = UiText.DynamicString("Bluetooth disabled"),
                text = UiText.DynamicString("Please enable Bluetooth from control center to pair your watch."),
                confirmText = UiText.DynamicString("Enable"),
                dismissText = UiText.DynamicString("Skip"),
                showIcon = true,
                iconRes = R.drawable.ic_strava_logo,
                type = MessageType.Warning
            ),
            closeDialog = {}
        )
    }
}

@Preview(name = "Minimal Dialog", showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun AppActionDialogPreview_Minimal() {
    PaceAppTheme {
        AppActionDialog(
            alert = AlertType.Dialog(
                title = UiText.DynamicString("Pairing Successful"),
                text = UiText.DynamicString(""),
                confirmText = UiText.DynamicString("Continue"),
                showIcon = false,
                type = MessageType.Success
            ),
            closeDialog = {}
        )
    }
}

@Preview(name = "Info Dialog", showBackground = true, backgroundColor = 0xFFCCCCCC)
@Composable
private fun AppActionDialogPreview_Info() {
    PaceAppTheme {
        AppActionDialog(
            alert = AlertType.Dialog(
                title = UiText.DynamicString("Location Required"),
                text = UiText.DynamicString("We need your location to calculate your accurate running pace."),
                confirmText = UiText.DynamicString("Allow"),
                showIcon = true,
                type = MessageType.Info
            ),
            closeDialog = {}
        )
    }
}