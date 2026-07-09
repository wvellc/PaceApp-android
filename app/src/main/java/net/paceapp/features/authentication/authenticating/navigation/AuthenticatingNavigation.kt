package net.paceapp.features.authentication.authenticating.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import net.paceapp.features.authentication.authenticating.AuthenticatingScreen

@Serializable
data object AuthenticatingRoute

fun NavGraphBuilder.authenticatingScreen() {
    composable<AuthenticatingRoute> {
        AuthenticatingScreen()
    }
}
