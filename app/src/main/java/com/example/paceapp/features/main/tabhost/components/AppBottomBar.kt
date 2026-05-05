package com.example.paceapp.features.main.tabhost.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.paceapp.features.main.tabhost.domain.BottomTab
import com.example.paceapp.theme.AppColors

@Composable
fun AppBottomBar(tabNavController: NavHostController) {
    val tabs = listOf(BottomTab.Home, BottomTab.History, BottomTab.Analytics, BottomTab.Profile)
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = AppColors.White.copy(alpha = 0.1f),
    ) {
        tabs.forEach { tab ->
            // Type-safe check to see if the current route matches the tab's KClass
            val isSelected = currentDestination?.hasRoute(tab.routeClass) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    tabNavController.navigate(tab.route) {
                        popUpTo(tabNavController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
//                    Icon(painterResource(id = tab.iconResId), contentDescription = null)
                },
                label = { Text( "TAB_ITEM"/*stringResource(id = tab.titleResId)*/) }
            )
        }
    }
}