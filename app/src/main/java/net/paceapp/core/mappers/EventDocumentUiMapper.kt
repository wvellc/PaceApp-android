package net.paceapp.core.mappers

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import net.paceapp.core.data.firestore.EventDocument
import net.paceapp.core.data.firestore.EventDocumentMapper
import net.paceapp.core.domain.models.DistanceModel
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.models.ActivityModel
import java.util.Locale

// Bridges a Firestore EventDocument to the app's list UI model (ActivityModel),
// which ActivityToUiModelMapper then formats into ActivityUiModel. Shared by
// Home, History, Favorites — the single place documents become list rows.
object EventDocumentUiMapper {

    fun toActivityModel(doc: EventDocument): ActivityModel {
        val variance = doc.timeVarianceSeconds
        return ActivityModel(
            id = doc.id.toString(),
            title = doc.name,
            location = doc.location,
            date = toLocalDateTime(doc),
            distance = DistanceModel(
                value = doc.distanceValue.toFloat(),
                unit = if (doc.measure == "Miles") DistanceUnits.MILES else DistanceUnits.KMS,
            ),
            goalTime = doc.goalTimeSeconds.toLong(),
            avgPace = formatPace(doc.avgPaceSeconds),
            paceDifference = variance?.let { EventDocumentMapper.formatSignedVariance(it) }.orEmpty(),
            // Negative variance = finished ahead of goal (matches iOS delta colouring).
            isAheadOfTime = (variance ?: 0) < 0,
        )
    }

    private fun toLocalDateTime(doc: EventDocument) =
        Instant.fromEpochMilliseconds(doc.scheduledAt?.toDate()?.time ?: 0L)
            .toLocalDateTime(TimeZone.currentSystemDefault())

    // Pace seconds → "m:ss"; "00:00" when unknown (mirrors iOS avgPaceFormatted, which
    // returns "00:00" for avgPace == 0 — e.g. a pre-run favorite/upcoming event).
    private fun formatPace(seconds: Int?): String {
        if (seconds == null || seconds <= 0) return "00:00"
        return String.format(Locale.US, "%d:%02d", seconds / 60, seconds % 60)
    }
}
