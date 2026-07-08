package net.paceapp.core.data.firestore

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.maps.android.PolyUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
        val id = connectIQId(payload["id"]) ?: (System.currentTimeMillis() / 1000).toInt()
        val now = Timestamp.now()
        val scheduledAt = parseConnectIQDate(payload["date"] as? String) ?: Date()
        val goalTimeSeconds = parseTimeString(payload["goal"] as? String ?: "00:00:00")
        val distanceValue = parseDouble(payload["distance"]) ?: 0.0
        val measure = payload["measure"] as? String ?: "Miles"
        val actualTimeStr = payload["actualTime"] as? String ?: ""
        val hasCompletion = isCompleted || actualTimeStr.isNotEmpty() || payload["actualDist"] != null
        val status = if (hasCompletion) EventStatusValue.COMPLETED else EventStatusValue.ACTIVE
        val actualTimeSeconds = if (actualTimeStr.isEmpty()) null else parseTimeString(actualTimeStr)
        val actualDistance = parseDouble(payload["actualDist"])
        val timeVarianceSeconds = parseSignedTimeVariance(payload["timeVar"] as? String ?: "")
        val avgHeartRate = parseInt(payload["avgHeartRate"])
        val avgPaceSeconds = parseInt(payload["avgPace"]) // watch/Firebase value only — never computed locally
        val effortPercentage = computeEffortPercentage(goalTimeSeconds, actualTimeSeconds)

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
            completedAt = if (hasCompletion) now else null,
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
            paces = arrayOfInts(payload["paces"]).map { it.toLong() }.ifEmpty { null },
            completedSegments = arrayOfDicts(payload["completedSegments"]).ifEmpty { null },
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
        return payload
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

    fun computeEffortPercentage(goalTimeSeconds: Int, actualTimeSeconds: Int?): Double? {
        if (actualTimeSeconds == null || goalTimeSeconds <= 0) return null
        val ratio = min(goalTimeSeconds, actualTimeSeconds).toDouble() /
            max(goalTimeSeconds, actualTimeSeconds).toDouble()
        return min(100.0, max(0.0, ratio * 100.0))
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
