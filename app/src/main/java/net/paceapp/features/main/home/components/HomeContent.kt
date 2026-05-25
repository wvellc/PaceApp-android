package net.paceapp.features.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import net.paceapp.R
import net.paceapp.core.components.AnimatedBellIcon
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.profilesteps.PairWatchInitContent
import net.paceapp.core.garmin.enums.WatchConnectionState
import net.paceapp.core.garmin.state.GarminSdkState
import net.paceapp.features.main.home.HomeContract.Event
import net.paceapp.features.main.home.HomeContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun HomeContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val isSdkReady = state.garminSdkStatus is GarminSdkState.Ready
    val isWatchConnected = state.watchModel?.status == WatchConnectionState.CONNECTED
    val metricsBackdrop = rememberLayerBackdrop()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current
    // Null index means the dialog is closed.
    var selectedMetricInfoIndex by remember { mutableStateOf<Int?>(null) }

    val animatedColor = rememberPulsingColor(
        initialColor = AppColors.FluorescentMint,
        targetColor = AppColors.InfernoRed,
        holdDuration = 5.minutes,
        transitionDuration = 600.milliseconds
    )

    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        backgroundModifier = Modifier.layerBackdrop(metricsBackdrop),
        isLoading = state.isLoading || !isSdkReady,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                showBackButton = false, showAppLogo = true, onBackClick = {
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    //Notifications
                    AnimatedBellIcon {
                        onEvent(Event.OnNotificationClick)
                    }
                })
        }) { innerPaddings ->
        if (!isSdkReady) {
            return@AppBaseScreen
        }
        if (isWatchConnected) {
            // CONNECTED STATE
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    top = AppTheme.screenPadding,
                    start = AppTheme.screenPadding,
                    end = AppTheme.screenPadding,
                    bottom = AppTheme.screenPadding + AppTheme.bottomNavBarPadding
                )
            ) {
                // Greeting message
                item {
                    HomeGreetingHeader(
                        user = state.userUiModel,
                        subtitle = state.lastSyncDate
                    )
                }

                //Watch metrics buttons
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        state.metrics.forEachIndexed { index, item ->
                            WatchMetricsButton(
                                backdrop = metricsBackdrop,
                                metric = item,
                                tintColor = animatedColor.value,
                                onClick = {
                                    selectedMetricInfoIndex = index
                                },
                            )
                        }
                    }
                }
                //Event and favorite buttons
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        //New Event
                        HomeSquareButton(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_new_event,
                            titleRes = R.string.new_event,
                            onClick = {
                                onEvent(Event.OnNewEventClick)
                            })
                        //Favorites
                        HomeSquareButton(
                            modifier = Modifier.weight(1f),
                            iconRes = R.drawable.ic_favorites,
                            titleRes = R.string.favorites,
                            onClick = {
                                onEvent(Event.OnFavoriteClick)
                            })
                    }
                }
                //Upcoming activities label
                item {
                    Text(
                        text = stringResource(R.string.upcoming_activities),
                        style = AppTheme.typography.semiBold.copy(
                            color = AppColors.White,
                            fontSize = 16.sp,
                        )
                    )
                }
                //Upcoming Activity list
                itemsIndexed(
                    items = state.upcomingActivities,
                    key = { index, item -> item.id }) { index, activity ->
                    UpcomingActivityItem(
                        modifier = Modifier.fillMaxWidth(), model = activity, onClick = {
                            onEvent(Event.OnActivityClick(activity))
                        })
                }
            }

            selectedMetricInfoIndex?.let { currentIndex ->
                val metricInfo = state.metrics[currentIndex]

                WatchMetricsInfoDialog(
                    metricInfo = metricInfo,
                    currentIndex = currentIndex,
                    totalSteps = state.metrics.size,
                    cancelable = true,
                    onNextClick = {
                        selectedMetricInfoIndex = when {
                            currentIndex < state.metrics.size - 1 -> currentIndex + 1
                            else -> null
                        }
                    },
                    onPrevClick = {
                        if (currentIndex > 0) {
                            selectedMetricInfoIndex = currentIndex - 1
                        }
                    },
                    onCloseDialog = {
                        selectedMetricInfoIndex = null
                    }

                )
            }

        } else {
            // DISCONNECTED STATE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = AppTheme.screenPadding + innerPaddings.calculateTopPadding(),
                        bottom = AppTheme.bottomNavBarPadding
                    )
            ) {
                // Header
                HomeGreetingHeader(
                    modifier = Modifier
                        .padding(
                            start = AppTheme.screenPadding,
                            end = AppTheme.screenPadding,
                        ),
                    user = state.userUiModel,
                    subtitle = stringResource(R.string.not_synced_yet)
                )

                // Empty State
                PairWatchInitContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = AppTheme.screenPadding),
                    showButton = true,
                    onButtonClick = {
                        onEvent(Event.OnStartPairing(context))
                    }
                )
            }
        }
    }
}