package com.example.paceapp.features.main.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AnimatedBellIcon
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun HomeContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val isWatchConnected = state.watchModel?.status == WatchConnectionState.CONNECTED
    val metricsBackdrop = rememberLayerBackdrop()
    val lazyListState = rememberLazyListState()
    // Null index means the dialog is closed.
    var selectedMetricInfoIndex by remember { mutableStateOf<Int?>(null) }

    val animatedColor = rememberPulsingColor(
        initialColor = AppColors.FluorescentMint,
        targetColor = AppColors.InfernoRed,
        holdDuration = 5.minutes,
        transitionDuration = 600.milliseconds
    )

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        backgroundModifier = Modifier.layerBackdrop(metricsBackdrop),
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
            //Greeting message
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(
                            R.string.greetings_user,
                            state.userUiModel?.firstName ?: ""
                        ),
                        style = AppTheme.typography.bold.copy(
                            color = AppColors.White,
                            lineHeight = 24.sp,
                            fontSize = 24.sp,
                        )
                    )
                    //Watch status
                    Text(
                        text = when {
                            isWatchConnected -> state.lastSyncDate
                            else -> stringResource(R.string.not_synced_yet)
                        },
                        style = AppTheme.typography.medium.copy(
                            fontSize = 13.sp,
                            color = AppColors.White.copy(alpha = 0.5f),
                            lineHeight = 13.sp,
                        )
                    )
                }
            }
            //Watch metrics buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        }
                    )
                    //Favorites
                    HomeSquareButton(
                        modifier = Modifier.weight(1f),
                        iconRes = R.drawable.ic_favorites,
                        titleRes = R.string.favorites,
                        onClick = {}
                    )
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
                key = { index, item -> item.id }
            ) { index, activity ->
                UpcomingActivityItem(
                    modifier = Modifier.fillMaxWidth(),
                    model = activity,
                    onClick = {
                        onEvent(Event.OnActivityClick(activity))
                    }
                )
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
                    onDismissDialog = {
                        selectedMetricInfoIndex = null
                    }

                )
        }

    }
}


