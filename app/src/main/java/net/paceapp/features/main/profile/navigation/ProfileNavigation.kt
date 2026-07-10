package net.paceapp.features.main.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import net.paceapp.features.main.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSetGait: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToManageWatch: () -> Unit,
    onNavigateToStrava: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileScreen(
            onBack = onBack,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSetGait = onNavigateToSetGait,
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToManageWatch = onNavigateToManageWatch,
            onNavigateToStrava = onNavigateToStrava,
        )
    }
}
