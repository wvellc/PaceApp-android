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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
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
import java.util.Locale
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
        // Time-variance + effort badge only for completed events — an active/upcoming
        // event has no variance, so iOS shows no red "+00:00:00 / 0%" here.
        if (eventDetails.isCompleted) {
            Spacer(modifier = Modifier.height(8.dp))
            TimeVarianceRow(
                varianceInSeconds = eventDetails.timeVarianceInSeconds,
                isAheadOfTime = eventDetails.isAheadOfTime,
                percentage = eventDetails.performancePercentage ?: 0,
            )
        }

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
            // Build the visible analysis rows in reading order, then split them evenly
            // across two columns. This keeps the original layout when everything is
            // present, but when a row is hidden (e.g. Segments with 0) the remaining
            // items rebalance instead of leaving an empty slot in one column.
            val analysisItems = buildList {
                add(stringResource(R.string.event_distance) to eventDetails.targetDistance.displayValue)
                add(
                    stringResource(R.string.finished_goal_time) to
                        DateTimeHelper.formatDuration(eventDetails.finishTimeGoalInSeconds.seconds)
                )
                // Completed-only stats are hidden for an active/upcoming event
                // (mirrors iOS filtering out "—" values).
                if (eventDetails.isCompleted) {
                    add(
                        stringResource(R.string.time_variance) to
                            DateTimeHelper.formatDuration(eventDetails.timeVarianceInSeconds.seconds)
                    )
                }
                // Hide the Segments count in Analysis when the event has none
                // (no "Segments: 0" for a plain run).
                if (eventDetails.segments.isNotEmpty()) {
                    add(stringResource(R.string.segments) to eventDetails.segments.size.toString())
                }
                if (eventDetails.isCompleted) {
                    add(stringResource(R.string.completed_distance) to eventDetails.completedDistance.displayValue)
                    add(
                        stringResource(R.string.total_time_taken) to
                            DateTimeHelper.formatDuration(eventDetails.totalTimeTakenInSeconds.seconds)
                    )
                }
                add(stringResource(R.string.look_back_intervals) to eventDetails.lookBackIntervals.toString())
                if (eventDetails.isCompleted) {
                    add(
                        stringResource(R.string.average_heart_rate) to
                            "${eventDetails.averageHeartRateBpm ?: 0} bpm"
                    )
                    // Average Pace (watch-provided), shown as m:ss per the event's unit.
                    eventDetails.averagePaceSeconds?.takeIf { it > 0 }?.let { paceSeconds ->
                        val unit = stringResource(eventDetails.targetDistance.unit.unitNameRes)
                        add(
                            stringResource(R.string.average_pace) to
                                String.format(Locale.US, "%d:%02d min/%s", paceSeconds / 60, paceSeconds % 60, unit)
                        )
                    }
                }
            }
            val leftCount = (analysisItems.size + 1) / 2
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    analysisItems.take(leftCount).forEach { (title, value) ->
                        EventTitleValue(title = title, value = value)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    analysisItems.drop(leftCount).forEach { (title, value) ->
                        EventTitleValue(title = title, value = value)
                    }
                }
            }
        }

        //Intervals — only when the event actually has interval paces (mirrors iOS).
        if (eventDetails.intervals.isNotEmpty()) {
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
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
        } // end intervals guard
        //Segments — only when the event has segments (mirrors iOS).
        if (eventDetails.segments.isNotEmpty()) {
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Segment label — "S1", "S2"… (mirrors iOS, fixed 28dp lead).
                            Text(
                                text = stringResource(R.string.segment_title, model.id + 1),
                                modifier = Modifier.width(28.dp),
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
                                modifier = Modifier.weight(1f),
                                text = "$formatDuration / ${model.distance.displayValue}",
                                textAlign = TextAlign.Center,
                                style = AppTheme.typography.semiBold.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    letterSpacing = 0.32.sp,
                                    color = AppColors.DarkCharcoal,
                                )
                            )
                            // Completed / Awaiting status pill (mint vs hint-gray, iOS).
                            Box(
                                modifier = Modifier
                                    .width(84.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (model.isCompleted) AppColors.FluorescentMint
                                        else AppColors.HintGray
                                    )
                                    .padding(vertical = 4.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(
                                        if (model.isCompleted) R.string.segment_completed
                                        else R.string.segment_awaiting
                                    ),
                                    style = AppTheme.typography.semiBold.copy(
                                        fontSize = 11.sp,
                                        color = if (model.isCompleted) AppColors.DarkCharcoal
                                        else AppColors.FashionGray,
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
        } // end segments guard
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

