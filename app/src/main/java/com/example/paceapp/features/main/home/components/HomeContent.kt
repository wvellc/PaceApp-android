package com.example.paceapp.features.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.paceapp.core.components.AnimatedBellIcon
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme

@Composable
internal fun HomeContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                showBackButton = false,
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    //Notifications
                    AnimatedBellIcon {
                        onEvent(Event.OnNotificationClick)
                    }
                }
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(AppTheme.screenPadding)
                .padding(bottom = AppTheme.bottomNavBarPadding)
                .background(AppColors.White),
        ) { }
    }
}

