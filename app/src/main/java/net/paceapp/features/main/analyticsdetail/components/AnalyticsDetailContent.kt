package net.paceapp.features.main.analyticsdetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppSegmentedButtons
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.enums.AnalyticsPeriod
import net.paceapp.core.extensions.g2Continuity
import net.paceapp.core.extensions.titleRes
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.Event
import net.paceapp.features.main.analyticsdetail.AnalyticsDetailContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

@Composable
internal fun AnalyticsDetailContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    val summaryListState = rememberLazyListState()
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = false,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(state.metricType.titleRes),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
            )
        },
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPaddings.calculateTopPadding())
                .padding(AppTheme.screenPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Analytics periods
            AppSegmentedButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                    .background(AppColors.White.copy(alpha = 0.1f))
                    .padding(4.dp),
                segments = AnalyticsPeriod.entries,
                selectedSegment = state.selectedPeriod,
                itemTitle = { stringResource(it.titleRes) },
                onSegmentSelected = { onEvent(Event.OnAnalyticPeriodSelected(it)) },
            )

            //Summary list
            LazyColumn(
                state = summaryListState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    bottom = AppTheme.screenPadding + innerPaddings.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.summaryList, key = { it.id }) { summary ->
                    AnalyticDetailItem(
                        modifier = Modifier.fillMaxWidth(),
                        summaryData = summary
                    )
                }
            }
        }
    }
}
