package com.wvelabs.core_ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


// ==========================================
// DEFAULT UI COMPONENTS
// ==========================================

@Composable
fun MessageType.defaultColor(): Color {
    return when (this) {
        MessageType.Success -> Color(0xFF4CAF50)
        MessageType.Error -> Color(0xFFE53935)
        MessageType.Warning -> Color(0xFFFF9800)
        MessageType.Info -> Color(0xFF2196F3)
        MessageType.Loading -> MaterialTheme.colorScheme.inverseSurface
    }
}


val MessageType.icon: ImageVector
    get() = when (this) {
        MessageType.Info -> Icons.Default.Info
        MessageType.Success -> Icons.Default.CheckCircle
        MessageType.Error -> Icons.Default.Cancel
        MessageType.Warning -> Icons.Default.Warning
        MessageType.Loading -> Icons.Default.Notifications // iOS: "bell.fill"
    }

@Composable
fun DefaultToast(alert: AlertType.Toast) {
    Box(
        modifier = Modifier
            // This stops touches from passing through to buttons underneath!
            .pointerInput(Unit) {}
            .padding(bottom = 64.dp)
            .clip(RoundedCornerShape(50))
            .background(alert.type.defaultColor())
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = alert.message,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DefaultSnackbar(snackbarData: SnackbarData) {
    // Attempt to extract our custom visuals to retrieve the MessageType.
    // If it fails (e.g., standard snackbar triggered elsewhere), fallback to Loading.
    val customVisuals = snackbarData.visuals as? AlertSnackbarVisuals
    val type = customVisuals?.type ?: MessageType.Info

    Snackbar(
        snackbarData = snackbarData,
        containerColor = type.defaultColor(),
        contentColor = Color.White,
        actionColor = Color.White
    )
}

@Composable
fun DefaultDialog(alert: AlertType.Dialog, closeDialog: () -> Unit = {}) {
    AlertDialog(
        onDismissRequest = closeDialog,
        title = { Text(alert.title.asString(), color = alert.type.defaultColor()) },
        text = { Text(alert.text.asString()) },
        confirmButton = {
            TextButton(onClick = {
                alert.onConfirm?.invoke()
                closeDialog()
            }) {
                Text(alert.confirmText.asString())
            }
        },
        dismissButton = alert.dismissText?.let {
            {
                TextButton(onClick = {
                    alert.onDismiss?.invoke()
                    closeDialog()
                }) {
                    Text(it.asString())
                }
            }
        }
    )
}