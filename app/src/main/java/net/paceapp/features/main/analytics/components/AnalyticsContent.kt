package net.paceapp.features.main.analytics.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.features.main.analytics.AnalyticsContract.Event
import net.paceapp.features.main.analytics.AnalyticsContract.State
import net.paceapp.theme.AppTheme

@Composable
internal fun AnalyticsContent(
    state: State, onEvent: (Event) -> Unit
) {
    val analyticsListState = rememberLazyListState()

    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
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
            )
        }) { innerPaddings ->
        LazyColumn(
            state = analyticsListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPaddings.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = 24.dp,
                start = AppTheme.screenPadding,
                end = AppTheme.screenPadding,
                bottom = AppTheme.screenPadding + AppTheme.bottomNavBarPadding
            )
        ) {
            items(state.analyticsList, key = { it.id }) { analyticsData ->
                AnalyticsListItem(modifier = Modifier.fillMaxWidth(), analytics = analyticsData) {
                    onEvent(Event.OnAnalyticsClick(analyticsData))
                }
            }
        }
    }
}
