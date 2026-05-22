package net.paceapp.navigation

import androidx.navigation.NavController

fun NavController.popBackSafely(
    onExit: (() -> Unit)? = null,   // Optional callback for exiting the app
) {
    if (this.previousBackStackEntry != null) {
        // If not at the root of the navigation stack, pop back
        this.popBackStack()
    } else {
        // If at the root, invoke the onExit callback if provided
        onExit?.invoke()
    }
}