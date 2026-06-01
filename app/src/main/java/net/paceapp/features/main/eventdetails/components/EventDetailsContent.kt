package net.paceapp.features.main.eventdetails.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.wvelabs.core_ui.components.LiquidGlassButton
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppLoadingIndicator
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.components.NoDataView
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.core.utils.StaticMapHelper
import net.paceapp.features.main.eventdetails.EventDetailsContract.Event
import net.paceapp.features.main.eventdetails.EventDetailsContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun EventDetailsContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    val backdrop = rememberLayerBackdrop()

    val animatedTint by animateColorAsState(
        targetValue = when {
            state.isFavorite -> AppColors.FluorescentMint
            else -> AppColors.HintGray
        }
    )
    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                title = stringResource(R.string.new_event),
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },
                actions = {
                    LiquidGlassButton(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        onClick = { onEvent(Event.OnFavoriteToggle) },
                        backdrop = backdrop,
                        maxScale = 1.2f
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_favorites),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = animatedTint
                        )
                    }
                }
            )
        },
        appLoader = {
            AppLoadingIndicator(
                isFullScreen = false,
                backgroundColor = AppColors.Transparent
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPaddings.calculateTopPadding()),
        ) {
            if (state.isLoading) return@Column
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = innerPaddings.calculateBottomPadding())
                    .padding(AppTheme.screenPadding),
            ) {
                state.eventDetails?.let { eventDetails ->
                    val mapUrl = StaticMapHelper.buildPolylineMapUrl(
                        encodedPolyline = eventDetails.encodedPolyline,
                    )

                    //Static map
                    AsyncImage(
                        model = mapUrl,
                        contentDescription = "Event Route Map",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .height(185.dp) // The actual UI height
                            .clip(RoundedCornerShape(16.dp))
                            .defaultClickable(onClick = {})
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    //Event data
                    EventDataCard(
                        eventDetails = eventDetails,
                        isAnalyticsExpanded = state.isAnalyticsExpanded,
                        isIntervalsExpanded = state.isIntervalsExpanded,
                        isSegmentsExpanded = state.isSegmentsExpanded,
                        onToggleAnalytics = {
                            onEvent(Event.OnAnalyticsToggle)
                        },
                        onToggleIntervals = {
                            onEvent(Event.OnIntervalsToggle)
                        },
                        onToggleSegments = {
                            onEvent(Event.OnSegmentsToggle)
                        }
                    )
                } ?: NoDataView(title = "Unable to fetch event details. Contact support team.")
            }
        }
    }
}
