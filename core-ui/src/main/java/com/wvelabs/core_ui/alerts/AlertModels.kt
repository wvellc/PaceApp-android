package com.wvelabs.core_ui.alerts

import androidx.annotation.DrawableRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.ui.graphics.Color
import java.util.UUID


// ==========================================
// MODELS & DATA STRUCTURES
// ==========================================

/**
 * Defines the semantic meaning of the alert, which can be used by
 * the UI components to dynamically assign colors and icons.
 */
enum class MessageType {
    Loading, Success, Error, Warning, Info


}


enum class ToastGravity { Top, Center, Bottom }
enum class IconPosition { START, END }

data class ToastButton(
    val title: String,
    val color: Color,
    val action: () -> Unit,
)

/**
 * Represents the different types of global alerts available in the app.
 */
sealed class AlertType {
    data class Toast(
        val message: String,
        val type: MessageType = MessageType.Info,
        val durationMillis: Long = 3000L,
        val gravity: ToastGravity = ToastGravity.Top,
        @param:DrawableRes val iconRes: Int? = null,
        val button: ToastButton? = null,
        // A unique ID is required so the system can distinguish between two consecutive toasts with the exact same text.
        val id: String = UUID.randomUUID().toString()
    ) : AlertType()

    data class Snackbar(
        val text: String,
        val type: MessageType = MessageType.Info,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : AlertType()

    data class Dialog(
        val title: UiText,
        val text: UiText,
        val showIcon: Boolean = false,
        @param:DrawableRes val iconRes: Int? = null,
        val type: MessageType = MessageType.Info,
        val cancelable: Boolean = true,
        val confirmText: UiText = UiText.DynamicString("OK"),
        val dismissText: UiText? = null,
        val onConfirm: (() -> Unit)? = null,
        val onDismiss: (() -> Unit)? = null
    ) : AlertType()
}

/**
 * A custom implementation of SnackbarVisuals.
 * Compose's default SnackbarHostState queue only accepts strings and basic actions.
 * We use this custom class to safely pass our [MessageType] through the queue
 * so the UI knows how to style the Snackbar when it finally appears.
 */
class AlertSnackbarVisuals(
    override val message: String,
    override val actionLabel: String?,
    override val withDismissAction: Boolean,
    override val duration: SnackbarDuration,
    val type: MessageType
) : SnackbarVisuals
