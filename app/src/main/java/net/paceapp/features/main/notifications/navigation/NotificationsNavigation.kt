package net.paceapp.features.main.notifications.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.main.notifications.NotificationsScreen

@Serializable
data object NotificationsRoute

fun NavGraphBuilder.notificationsScreen(
    onBack: () -> Unit
) {
    composable<NotificationsRoute> {
        NotificationsScreen(
            onBack = onBack
        )
    }
}
