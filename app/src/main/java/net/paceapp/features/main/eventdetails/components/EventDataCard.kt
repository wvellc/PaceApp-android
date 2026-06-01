package net.paceapp.features.main.eventdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wvelabs.core_ui.utils.AppDateFormat
import com.wvelabs.core_ui.utils.DateTimeHelper
import net.paceapp.R
import net.paceapp.core.extensions.displayValue
import net.paceapp.features.main.eventdetails.models.EventDetailsUiModel
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun EventDataCard(
    eventDetails: EventDetailsUiModel,
    isAnalyticsExpanded: Boolean = true,
    isIntervalsExpanded: Boolean = false,
    isSegmentsExpanded: Boolean = false,
    onToggleAnalytics: () -> Unit = {},
    onToggleIntervals: () -> Unit = {},
    onToggleSegments: () -> Unit = {},
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.White)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = eventDetails.title,
            style = AppTheme.typography.semiBold.copy(
                fontSize = 24.sp,
                color = AppColors.DarkCharcoal,
                lineHeight = 24.sp,
                letterSpacing = 0.54.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        TimeVarianceRow(
            varianceInSeconds = eventDetails.timeVarianceInSeconds,
            isAheadOfTime = eventDetails.isAheadOfTime,
            percentage = eventDetails.performancePercentage ?: 0,
        )

        Spacer(modifier = Modifier.height(16.dp))

        EventIconTextRow(
            iconRes = R.drawable.ic_calender,
            text = DateTimeHelper.formatDateTime(
                eventDetails.dateTime,
                AppDateFormat.DATE_FULL_MDY
            ) ?: ""
        )

        Spacer(modifier = Modifier.height(8.dp))

        EventIconTextRow(
            iconRes = R.drawable.ic_location,
            text = eventDetails.location
        )
        Spacer(modifier = Modifier.height(16.dp))

        DefaultDivider()

        //Analytics data
        EventExpandableDetails(
            title = stringResource(R.string.analysis),
            isExpanded = isAnalyticsExpanded,
            onToggle = onToggleAnalytics
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EventTitleValue(
                        title = stringResource(R.string.event_distance),
                        value = eventDetails.targetDistance.displayValue
                    )

                    EventTitleValue(
                        title = stringResource(R.string.finished_goal_time),
                        value = DateTimeHelper.formatDuration(eventDetails.finishTimeGoalInSeconds.seconds)
                    )

                    EventTitleValue(
                        title = stringResource(R.string.time_variance),
                        value = DateTimeHelper.formatDuration(eventDetails.timeVarianceInSeconds.seconds)
                    )
                    EventTitleValue(
                        title = stringResource(R.string.segments),
                        value = eventDetails.segments.size.toString()
                    )

                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EventTitleValue(
                        title = stringResource(R.string.completed_distance),
                        value = eventDetails.completedDistance.displayValue
                    )
                    EventTitleValue(
                        title = stringResource(R.string.total_time_taken),
                        value = DateTimeHelper.formatDuration(eventDetails.totalTimeTakenInSeconds.seconds)
                    )

                    EventTitleValue(
                        title = stringResource(R.string.look_back_intervals),
                        value = eventDetails.lookBackIntervals.toString()
                    )
                    EventTitleValue(
                        title = stringResource(R.string.average_heart_rate),
                        value = "${eventDetails.averageHeartRateBpm ?: 0} bpm"
                    )
                }
            }
        }

        DefaultDivider()

        EventExpandableDetails(
            title = stringResource(R.string.intervals),
            isExpanded = isIntervalsExpanded,
            onToggle = onToggleIntervals
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                eventDetails.intervals.chunked(4).forEachIndexed { rowIndex, rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEachIndexed { colIndex, item ->
                            val globalIndex = (rowIndex * 4) + colIndex
                            Box(modifier = Modifier.weight(1f)) {
                                EventTitleValue(
                                    title = "Interval ${globalIndex + 1}",
                                    value = DateTimeHelper.formatDuration(item.durationInSeconds.seconds)
                                )
                            }
                        }

                        // If the last row has less than 4 items, fill the empty space
                        // so the items don't stretch weirdly
                        repeat(4 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

        }
        DefaultDivider()

        EventExpandableDetails(
            title = stringResource(R.string.segments),
            isExpanded = isSegmentsExpanded,
            onToggle = onToggleSegments
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                eventDetails.segments.forEach { model ->
                    key(model.id) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.segment_title, model.id),
                                style = AppTheme.typography.semiBold.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.32.sp,
                                    color = AppColors.DarkCharcoal,
                                )
                            )
                            val formatDuration =
                                DateTimeHelper.formatDuration(model.durationInSeconds.seconds)
                            Text(
                                text = "$formatDuration / ${model.distance.displayValue}",
                                style = AppTheme.typography.semiBold.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.32.sp,
                                    color = AppColors.DarkCharcoal,
                                )
                            )
                        }
                    }
                }
            }

        }
    }
}


@Composable
private fun DefaultDivider() {
    HorizontalDivider(
        modifier = Modifier,
        color = AppColors.HintGray,
        thickness = 1.dp
    )
}

