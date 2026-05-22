package net.paceapp.features.main.notifications.domain.usecases

import net.paceapp.features.main.notifications.domain.mappers.toUI
import net.paceapp.features.main.notifications.domain.models.NotificationModel
import net.paceapp.features.main.notifications.models.NotificationUI
import javax.inject.Inject

class MapNotificationsToUiUseCase @Inject constructor() {
    operator fun invoke(notifications: List<NotificationModel>): List<NotificationUI> {
        return notifications.map { it.toUI() }
    }
}