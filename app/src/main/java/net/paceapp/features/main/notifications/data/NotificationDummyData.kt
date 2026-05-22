package net.paceapp.features.main.notifications.data

import net.paceapp.features.main.notifications.domain.models.NotificationModel
import java.util.UUID

object NotificationDummyData {

    fun getDummyNotifications(): List<NotificationModel> = listOf(
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Interval complete",
            message = "Check your split time and steps for this segment.",
            updatedAt = "2026-05-19T10:45:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Goal pace off target",
            message = "You're slightly off your target pace. Adjust in the next interval.",
            updatedAt = "2026-05-19T10:45:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Goal pace on track",
            message = "You're right on your target pace. Keep it up!",
            updatedAt = "2026-05-19T10:47:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Goal pace ahead",
            message = "You're exceeding your target pace. Maintain this energy!",
            updatedAt = "2026-05-19T10:49:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Goal pace below target",
            message = "You're falling behind your target pace. Push harder!",
            updatedAt = "2026-05-19T10:48:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "New Personal Best!",
            message = "You just beat your 5K record by 12 seconds. Incredible work!",
            updatedAt = "2026-05-19T10:35:00Z",
        ),
        NotificationModel(
            id = UUID.randomUUID().toString(),
            title = "Watch battery low",
            message = "Your Garmin is at 15%. Consider charging before your next run.",
            updatedAt = "2026-05-19T09:50:00Z",
        )
    )
}