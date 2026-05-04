package com.wvelabs.core_ui.alerts

import androidx.annotation.DrawableRes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


// ==========================================
// GLOBAL ALERT MANAGER
// ==========================================

/**
 * A singleton dispatcher used to trigger alerts from anywhere in the app,
 * completely decoupled from the UI layer.
 *
 * 📘 ARCHITECTURE NOTE:
 * This is the "Public API" of the alert system. When you call a method here,
 * it sends an event into a stream. That stream is collected and rendered by
 * the [AppAlertContainer] located at the root of your application.
 *
 * 🧩 REDIRECTION:
 * If your alerts are not showing up, check if [AppAlertContainer] is wrapping
 * your NavHost in MainActivity.kt.
 */
object AppAlerts {

    // A buffered channel ensures that if an alert is triggered (e.g., in an init block) 
    // before the UI is fully ready to collect it, the event is queued rather than dropped.
    private val channel = Channel<AlertType>(Channel.BUFFERED)

    /**
     * Internal stream observed by [AppAlertContainer].
     */
    internal val alerts = channel.receiveAsFlow()

    fun send(alert: AlertType) {
        channel.trySend(alert)
    }

    /**
     * Shows a brief message at the bottom of the screen.
     */
    fun showToast(
        text: String,
        gravity: ToastGravity = ToastGravity.Top,
        type: MessageType = MessageType.Info,
        durationMillis: Long = 3000L,
        @DrawableRes iconRes: Int? = null,
        button: ToastButton? = null,
    ) = send(
        AlertType.Toast(
            message = text,
            type = type,
            durationMillis = durationMillis,
            gravity = gravity,
            iconRes = iconRes,
            button = button
        )
    )

    /**
     * Shows a message with an optional action button (Snackbar).
     */
    fun showSnackbar(
        text: String,
        type: MessageType = MessageType.Info,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ) = send(
        AlertType.Snackbar(
            text = text,
            type = type,
            actionLabel = actionLabel,
            onAction = onAction
        )
    )

    /**
     * Shows a blocking modal dialog.
     */
    fun showDialog(
        title: String,
        text: String,
        type: MessageType = MessageType.Info,
        confirmText: String = "OK",
         cancelable: Boolean = true,
        dismissText: String? = null,
        onConfirm: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) = send(
        AlertType.Dialog(
            title = title,
            text = text,
            type = type,
            confirmText = confirmText,
            dismissText = dismissText,
            cancelable = cancelable,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    )
}
