package com.example.paceapp.features.main.notifications.domain.mappers

import com.example.paceapp.features.main.notifications.domain.models.NotificationModel
import com.example.paceapp.features.main.notifications.models.NotificationUI
import com.wvelabs.core_ui.utils.DateTimeHelper
import java.util.UUID

fun NotificationModel.toUI(): NotificationUI {
    val localDateTime = DateTimeHelper.getDateTime(updatedAt)

    return NotificationUI(
        id = this.id ?: UUID.randomUUID().toString(),
        title = this.title ?: "Notification",
        message = this.message ?: "",
        timeAgo = DateTimeHelper.getRelativeTime(localDateTime),
    )
}