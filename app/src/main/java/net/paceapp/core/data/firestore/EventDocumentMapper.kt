package net.paceapp.core.data.firestore

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.maps.android.PolyUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

// Single translation layer between raw ConnectIQ wire payloads (Map<String, Any?>)
// and the Firestore EventDocument. Direct port of iOS EventDocumentMapper — all
// event-dict parsing lives here; nothing else parses raw payloads. Keep field
// names/formats identical to iOS so both phones interoperate over one backend.
object EventDocumentMapper {

    // MARK: - ConnectIQ wire payload → EventDocument

    fun document(
        payload: Map<String, Any?>,
        userId: String,
        isCompleted: Boolean,
        syncStatus: String,
        source: String,
    ): EventDocument {
        // Parse the same canonical two-decimal distances we send, so the stored value matches the watch.
        val payload = normalizingWatchDistances(payload)
        val id = connectIQId(payload["id"]) ?: (System.currentTimeMillis() / 1000).toInt()
        val now = Timestamp.now()
        val scheduledAt = parseConnectIQDate(payload["date"] as? String) ?: Date()
        val goalTimeSeconds = parseTimeString(payload["goal"] as? String ?: "00:00:00")
        val distanceValue = parseDouble(payload["distance"]) ?: 0.0
        val measure = payload["measure"] as? String ?: "Miles"
        // Status comes from HOW the event arrived (finish_event / completedEvents list), never from its
        // fields — an older watch "Duplicate" copies the original run's results onto a brand-new upcoming
        // event, so inferring completion from actualTime/actualDist wrongly filed it under History.
        val status = if (isCompleted) EventStatusValue.COMPLETED else EventStatusValue.ACTIVE
        // Result fields only belong to a completed event; ignore any that ride along on an active one.
        val results: Map<String, Any?> = if (isCompleted) payload else emptyMap()
        val actualTimeStr = results["actualTime"] as? String ?: ""
        val actualTimeSeconds = if (actualTimeStr.isEmpty()) null else parseTimeString(actualTimeStr)
        val actualDistance = parseDouble(results["actualDist"])
        val timeVarianceSeconds = parseSignedTimeVariance(results["timeVar"] as? String ?: "")
        val avgHeartRate = parseInt(results["avgHeartRate"])
        val avgPaceSeconds = parseInt(results["avgPace"]) // watch/Firebase value only — never computed locally
        val completedSegmentPayloads = arrayOfDicts(results["completedSegments"])
        val effortPercentage = pacePercentage(
            goalTimeSeconds = goalTimeSeconds,
            plannedDistance = distanceValue,
            actualTimeSeconds = actualTimeSeconds,
            coveredDistance = coveredDistance(actualDistance, completedSegmentPayloads),
        )

        // Build embedded segments from the wire "segments" array: { distance, eta:"HH:MM:SS" }.
        val segments = arrayOfDicts(payload["segments"]).mapIndexed { index, seg ->
            SegmentDocument(
                index = index,
                distance = parseDouble(seg["distance"]) ?: 0.0,
                goalTimeSeconds = parseTimeString(seg["eta"] as? String ?: "00:00:00"),
            )
        }

        val routePolyline = encodeCoordinates(payload["coordinates"])

        return EventDocument(
            id = id,
            userId = userId,
            status = status,
            name = payload["name"] as? String ?: "",
            location = payload["location"] as? String ?: "",
            scheduledAt = Timestamp(scheduledAt),
            completedAt = if (isCompleted) now else null,
            activityType = ActivityTypeWire.canonical(payload["activity"] as? String),
            distanceValue = distanceValue,
            measure = measure,
            goalTimeSeconds = goalTimeSeconds,
            lookBackIntervals = parseInt(payload["intervals"]) ?: 1,
            avgPaceSeconds = avgPaceSeconds,
            avgHeartRate = avgHeartRate,
            elevationGain = 0.0,
            effortPercentage = effortPercentage,
            actualTimeSeconds = actualTimeSeconds,
            actualDistance = actualDistance,
            timeVarianceSeconds = timeVarianceSeconds,
            paces = arrayOfInts(results["paces"]).map { it.toLong() }.ifEmpty { null },
            completedSegments = completedSegmentPayloads.ifEmpty { null },
            syncStatus = syncStatus,
            source = source,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
            segments = segments.ifEmpty { null },
            routePolyline = routePolyline,
        )
    }

    // MARK: - EventDocument → ConnectIQ wire payload
    // Inverse of document(...) — rebuilds the dict the watch expects.

    fun connectIQPayload(document: EventDocument): Map<String, Any?> {
        val payload = mutableMapOf<String, Any?>(
            "id" to document.id,
            "name" to document.name,
            "location" to document.location,
            "date" to connectIQDateString(document.scheduledAt?.toDate() ?: Date()),
            "distance" to document.distanceValue,
            "measure" to document.measure,
            "goal" to formatTime(document.goalTimeSeconds),
            "intervals" to document.lookBackIntervals,
            "activity" to ActivityTypeWire.watchString(document.activityType),
            "syncStatus" to document.syncStatus,
            "source" to document.source,
        )

        document.actualTimeSeconds?.let { payload["actualTime"] = formatTime(it) }
        document.actualDistance?.let { payload["actualDist"] = it }
        document.timeVarianceSeconds?.let { payload["timeVar"] = formatSignedVariance(it) }
        document.avgHeartRate?.let { if (it > 0) payload["avgHeartRate"] = it }
        document.paces?.let { if (it.isNotEmpty()) payload["paces"] = it.map { p -> p.toInt() } }
        document.completedSegments?.let { if (it.isNotEmpty()) payload["completedSegments"] = it }
        document.segments?.let { segs ->
            if (segs.isNotEmpty()) {
                payload["segments"] = segs.map { seg ->
                    mapOf("distance" to seg.distance, "eta" to formatTime(seg.goalTimeSeconds))
                }
            }
        }
        document.routePolyline?.let { encoded ->
            val coords = PolyUtil.decode(encoded)
            if (coords.isNotEmpty()) {
                payload["coordinates"] = coords.map { mapOf("lat" to it.latitude, "lng" to it.longitude) }
            }
        }
        // Canonical two-decimal distance strings ("14.00"); segments still sum to the total.
        return normalizingWatchDistances(payload)
    }

    // MARK: - Helpers (direct ports of iOS EventDocumentMapper)

    fun connectIQId(value: Any?): Int? = when (value) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        is String -> value.toIntOrNull()
        else -> null
    }

    fun parseConnectIQDate(value: String?): Date? {
        if (value == null) return null
        for (format in listOf("MMM/d/yyyy", "MMM/dd/yyyy", "yyyy-MM-dd")) {
            val f = SimpleDateFormat(format, Locale.US)
            f.isLenient = false
            runCatching { f.parse(value) }.getOrNull()?.let { return it }
        }
        return null
    }

    fun connectIQDateString(date: Date): String =
        SimpleDateFormat("MMM/d/yyyy", Locale.US).format(date)

    fun parseTimeString(value: String): Int {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return 0
        val sign = if (trimmed.startsWith("-")) -1 else 1
        val raw = trimmed.trim('+', '-')
        val parts = raw.split(":").mapNotNull { it.toIntOrNull() }
        return when (parts.size) {
            3 -> sign * ((parts[0] * 3600) + (parts[1] * 60) + parts[2])
            2 -> sign * ((parts[0] * 60) + parts[1])
            else -> 0
        }
    }

    fun parseSignedTimeVariance(value: String): Int? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null
        return parseTimeString(trimmed)
    }

    fun formatTime(seconds: Int): String {
        val abs = abs(seconds)
        return String.format(Locale.US, "%02d:%02d:%02d", abs / 3600, (abs % 3600) / 60, abs % 60)
    }

    fun formatSignedVariance(seconds: Int): String {
        val sign = if (seconds < 0) "-" else "+"
        return sign + formatTime(abs(seconds))
    }

    fun parseDouble(value: Any?): Double? = when (value) {
        is Double -> value
        is Float -> value.toDouble()
        is Int -> value.toDouble()
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }

    fun parseInt(value: Any?): Int? = when (value) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        is String -> value.toIntOrNull()
        else -> null
    }

    @Suppress("UNCHECKED_CAST")
    fun arrayOfDicts(value: Any?): List<Map<String, Any?>> {
        if (value is List<*>) {
            return value.mapNotNull { it as? Map<String, Any?> }
        }
        return emptyList()
    }

    fun arrayOfInts(value: Any?): List<Int> {
        if (value is List<*>) {
            return value.mapNotNull { parseInt(it) }
        }
        return emptyList()
    }

    // Pace % = goal pace ÷ actual pace × 100 — 100% is right on goal pace, above 100% is faster.
    // e.g. goal 10 km in 50:00, covered 10 km in 45:00 → 111%. Mirrors iOS EventDocumentMapper
    // and the shared cloud function. Uses distance actually covered, so a run ended early is fair.
    fun pacePercentage(
        goalTimeSeconds: Int,
        plannedDistance: Double,
        actualTimeSeconds: Int?,
        coveredDistance: Double,
    ): Double? {
        if (actualTimeSeconds == null || actualTimeSeconds <= 0 || goalTimeSeconds <= 0 ||
            plannedDistance <= 0.0 || coveredDistance <= 0.0
        ) {
            return null
        }
        val goalPace = goalTimeSeconds.toDouble() / plannedDistance
        val actualPace = actualTimeSeconds.toDouble() / coveredDistance
        return goalPace / actualPace * 100.0
    }

    // Recomputes pace % from a stored document, so events written before this formula match too.
    fun pacePercentage(document: EventDocument): Double? = pacePercentage(
        goalTimeSeconds = document.goalTimeSeconds,
        plannedDistance = document.distanceValue,
        actualTimeSeconds = document.actualTimeSeconds,
        coveredDistance = coveredDistance(document.actualDistance, document.completedSegments ?: emptyList()),
    )

    // Distance actually covered — actualDistance, else the sum of each completed segment's
    // "completed_distance" (mirrors the shared cloud function + iOS coveredDistance).
    fun coveredDistance(actualDistance: Double?, completedSegments: List<Map<String, Any?>>): Double {
        if (actualDistance != null && actualDistance > 0.0) return actualDistance
        return completedSegments.sumOf { parseDouble(it["completed_distance"]) ?: 0.0 }
    }

    // MARK: - Distance (watch wire format)
    // Every distance synced with the watch goes through here, e.g. 14 → "14.00", 14.0005 → "14.00".
    // String(format) is not localized (always "."), so a comma-decimal locale can't leak "14,00".

    // Decimal places for synced distances (the client's "tenths" would be 1).
    private const val DISTANCE_FRACTION_DIGITS = 2

    fun watchDistanceString(value: Double): String =
        String.format(Locale.US, "%.${DISTANCE_FRACTION_DIGITS}f", value)

    // The value both sides calculate with — parsed back from the wire string, so they can't disagree.
    fun canonicalDistance(value: Double): Double =
        watchDistanceString(value).toDoubleOrNull() ?: value

    // Rounds segment distances, giving the rounding remainder to the last one so they still sum to the
    // total. Drift larger than rounding (a genuinely different split) is left untouched.
    fun canonicalSegmentDistances(distances: List<Double>, total: Double): List<Double> {
        val rounded = distances.map { canonicalDistance(it) }.toMutableList()
        val lastIndex = rounded.indices.lastOrNull() ?: return rounded
        if (total <= 0.0) return rounded
        val drift = canonicalDistance(total) - rounded.sum()
        val maxRoundingDrift = rounded.size * 0.5 * Math.pow(10.0, -DISTANCE_FRACTION_DIGITS.toDouble()) + 1e-9
        val adjusted = canonicalDistance(rounded[lastIndex] + drift)
        if (abs(drift) <= maxRoundingDrift && adjusted > 0.0) {
            rounded[lastIndex] = adjusted
        }
        return rounded
    }

    // Rewrites "distance", "actualDist" and each segment "distance" in a watch payload as canonical strings.
    fun normalizingWatchDistances(payload: Map<String, Any?>): Map<String, Any?> {
        val result = payload.toMutableMap()
        val total = parseDouble(payload["distance"])
        if (total != null) result["distance"] = watchDistanceString(canonicalDistance(total))
        parseDouble(payload["actualDist"])?.let {
            result["actualDist"] = watchDistanceString(canonicalDistance(it))
        }
        val segments = arrayOfDicts(payload["segments"])
        val segmentDistances = segments.mapNotNull { parseDouble(it["distance"]) }
        // Only rewrite when every segment carries a distance, so the remainder lands on the right one.
        if (segments.isNotEmpty() && segmentDistances.size == segments.size) {
            val canonical = canonicalSegmentDistances(segmentDistances, total ?: 0.0)
            result["segments"] = segments.mapIndexed { i, seg ->
                seg.toMutableMap().apply { this["distance"] = watchDistanceString(canonical[i]) }
            }
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun encodeCoordinates(value: Any?): String? {
        val list = value as? List<*> ?: return null
        val coords = mutableListOf<LatLng>()
        for (item in list) {
            when (item) {
                is Map<*, *> -> {
                    val lat = parseDouble(item["lat"]) ?: parseDouble(item["latitude"])
                    val lng = parseDouble(item["lng"]) ?: parseDouble(item["longitude"])
                    if (lat != null && lng != null) coords.add(LatLng(lat, lng))
                }
                is List<*> -> if (item.size >= 2) {
                    val lat = parseDouble(item[0]); val lng = parseDouble(item[1])
                    if (lat != null && lng != null) coords.add(LatLng(lat, lng))
                }
            }
        }
        return if (coords.isEmpty()) null else PolyUtil.encode(coords)
    }
}

// Activity-type wire mapping — mirrors iOS ActivityType (from:/rawValue/watchString).
// Firestore `activityType` stores the canonical rawValue ("Run"/"Walking"/"Cycling"/"Other");
// the watch `activity` field uses watchString ("Run"/"Walk"/"Cycling"/"Other").
object ActivityTypeWire {
    private const val RUN = "Run"
    private const val WALKING = "Walking"
    private const val CYCLING = "Cycling"
    private const val OTHER = "Other"

    // Canonical Firestore value from any wire/label variant (case-insensitive).
    fun canonical(value: String?): String = when (value?.lowercase()) {
        "walk", "walking" -> WALKING
        "cycle", "cycling" -> CYCLING
        "other" -> OTHER
        "run", "running" -> RUN
        else -> RUN
    }

    // Value sent to the watch in the `activity` field.
    fun watchString(activityType: String): String = when (canonical(activityType)) {
        WALKING -> "Walk"
        CYCLING -> CYCLING
        OTHER -> OTHER
        else -> RUN
    }
}
