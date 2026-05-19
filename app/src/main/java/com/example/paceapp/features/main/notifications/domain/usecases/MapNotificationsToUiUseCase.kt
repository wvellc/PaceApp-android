package com.example.paceapp.features.main.notifications.domain.usecases

import com.example.paceapp.features.main.notifications.domain.mappers.toUI
import com.example.paceapp.features.main.notifications.domain.models.NotificationModel
import com.example.paceapp.features.main.notifications.models.NotificationUI
import javax.inject.Inject

class MapNotificationsToUiUseCase @Inject constructor() {
    operator fun invoke(notifications: List<NotificationModel>): List<NotificationUI> {
        return notifications.map { it.toUI() }
    }
}