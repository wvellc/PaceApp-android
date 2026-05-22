package net.paceapp.features.main.notifications.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationModel(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null,
    val updatedAt: String? = null,
)