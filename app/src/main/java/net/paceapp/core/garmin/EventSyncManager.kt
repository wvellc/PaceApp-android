package net.paceapp.core.garmin

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.paceapp.core.auth.AuthManager
import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.data.firestore.GaitDocument
import net.paceapp.core.data.firestore.UserProfileRepository
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

// ============================================================================
// EventSyncManager — Event synchronization between Android phone and Garmin watch
// ============================================================================
//
// This class handles all event sync logic for PaceApp Android:
//   - Receiving and dispatching sync commands from the watch
//   - Merging remote events without duplicates
//   - Persisting events to SharedPreferences
//   - Sending sync commands to the watch via GarminDeviceManager
//   - Tracking deleted event IDs to prevent re-creation
//   - Syncing settings between phone and watch
//
// Supported commands:
//   "sync_request"  — Watch asks phone to send all data (phone responds with sync_all)
//   "sync_all"      — Watch sends all its data (phone merges, does NOT echo back)
//   "delete_event"  — Watch deleted an event
//   "create_event"  — Watch created a new active event
//   "finish_event"  — Watch finished an event (move active → completed)
//   "sync_settings" — Watch sends updated settings (alerts, gait)
//
// Sync Flow (two-way handshake):
//   sync_request (with data) → sync_all (with data) → STOP
//   The sync_all receiver NEVER echoes back (prevents infinite loop).
//
// ============================================================================

@Singleton
class EventSyncManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val deviceManager: GarminDeviceManager,
    @param:ApplicationScope private val appScope: CoroutineScope,
    // Firestore write-through (shared thepaceapp backend, parity with iOS).
    private val eventRepository: EventRepository,
    private val userProfileRepository: UserProfileRepository,
    private val authManager: AuthManager,
) {

    companion object {
        private const val TAG = "EventSync"
        private const val PREFS_NAME = "event_sync_prefs"
        private const val KEY_ACTIVE_EVENTS = "active_events"
        private const val KEY_COMPLETED_EVENTS = "completed_events"
        private const val KEY_DELETED_IDS = "deleted_event_ids"
        private const val KEY_SETTINGS = "synced_settings"

        // Settings keys — must match watch's Application.Storage keys exactly
        val SETTINGS_KEYS = listOf(
            "vibrate_alert",
            "beep_alert",
            "walking_gait",
            "walking_gait_measure",
            "running_gait",
            "running_gait_measure"
        )
    }

    // --- Compose-observable state ---
    // UI can read these directly in @Composable functions.
    val activeEvents = mutableStateListOf<Map<String, Any?>>()
    val completedEvents = mutableStateListOf<Map<String, Any?>>()

    // --- Internal storage ---
    private var activeEventPayloads = mutableListOf<MutableMap<String, Any?>>()
    private var completedEventPayloads = mutableListOf<MutableMap<String, Any?>>()
    private var deletedEventIds = mutableListOf<Int>()

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // =====================================================================
    // INITIALIZATION
    // =====================================================================

    init {
        // Load persisted state
        activeEventPayloads = loadEventPayloads(KEY_ACTIVE_EVENTS)
        completedEventPayloads = loadEventPayloads(KEY_COMPLETED_EVENTS)
        deletedEventIds = loadDeletedEventIds()
        pruneActivePayloadsAlreadyCompleted()
        rebuildObservableLists()

        // Listen for incoming Garmin messages
        deviceManager.incomingMessages
            .onEach { messageData -> handleIncomingMessage(messageData) }
            .launchIn(appScope)

        // Auto-trigger full sync when watch connects, and ask the watch for its
        // settings (height/weight/gait) so the profile stays in sync.
        deviceManager.onAppReady = {
            AppLogger.d("[$TAG] Watch connected — triggering full sync")
            requestFullSync()
            requestWatchSettings()
        }
    }

    // =====================================================================
    // SECTION 1: Message Dispatch
    // =====================================================================

    /**
     * Handles raw incoming messages from the Garmin watch.
     * The ConnectIQ Android SDK delivers messages as `List<Any>`.
     * Typically the list contains a single Map element.
     */
    private fun handleIncomingMessage(messageData: List<Any>) {
        for (item in messageData) {
            if (item is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val dict = item as Map<String, Any?>
                AppLogger.d("[$TAG] Received: $dict")

                // Try to handle as a sync command
                if (handleSyncMessage(dict)) {
                    continue
                }

                // Legacy fallback: treat unrecognized dict as a raw event record
                val eventPayload = extractEventRecord(dict)
                if (eventPayload != null) {
                    upsertEventPayload(eventPayload, isCompleted = false, syncStatus = "synced")
                }
            }
        }
    }

    // =====================================================================
    // SECTION 2: Sync Message Handler
    // Dispatches incoming sync commands from the watch.
    // =====================================================================

    /**
     * Dispatches incoming sync commands from the watch.
     * Returns true if the message was handled as a sync command.
     *
     * Supported commands:
     *   - `sync_request`: Watch asks phone to send all data (phone responds with sync_all)
     *   - `sync_all`: Watch sends all its data (phone merges, does NOT echo back)
     *   - `delete_event`: Watch deleted an event
     *   - `create_event`: Watch created a new active event
     *   - `finish_event`: Watch finished an event (active → completed)
     *   - `sync_settings`: Watch sends updated settings
     */
    private fun handleSyncMessage(dict: Map<String, Any?>): Boolean {
        val command = dict["command"] as? String ?: return false
        val isForce = dict["is_force_update"] as? Boolean ?: false

        AppLogger.d("[$TAG] Command: $command (force=$isForce)")

        when (command) {

            // --- SYNC REQUEST: Watch asks phone to send all data ---
            "sync_request" -> {
                if (isForce) {
                    activeEventPayloads.clear()
                    completedEventPayloads.clear()
                    deletedEventIds.clear()
                }
                applyDeletedEventIds(extractIntList(dict["deletedEventIds"]))
                mergeEventPayloads(extractPayloadList(dict["completedEvents"]), isCompleted = true)
                mergeEventPayloads(extractPayloadList(dict["activeEvents"]), isCompleted = false)
                applyRemoteSettings(dict["settings"])
                persistSyncState()
                // Respond with our full data so the watch gets our events too
                sendFullSync(command = "sync_all", isForceUpdate = isForce)
                return true
            }

            // --- SYNC ALL: Watch sends all its data (response to our sync_request) ---
            // We merge but do NOT echo sync_all back — prevents infinite loop.
            "sync_all" -> {
                if (isForce) {
                    activeEventPayloads.clear()
                    completedEventPayloads.clear()
                    deletedEventIds.clear()
                }
                applyDeletedEventIds(extractIntList(dict["deletedEventIds"]))
                mergeEventPayloads(extractPayloadList(dict["completedEvents"]), isCompleted = true)
                mergeEventPayloads(extractPayloadList(dict["activeEvents"]), isCompleted = false)
                applyRemoteSettings(dict["settings"])
                persistSyncState()
                return true
            }

            // --- DELETE EVENT: Watch deleted a specific event ---
            "delete_event" -> {
                val id = extractEventId(dict)
                if (id != null) {
                    applyDeletedEventId(id)
                    persistSyncState()
                }
                return true
            }

            // --- CREATE EVENT: Watch created a new active event ---
            "create_event" -> {
                val eventPayload = extractEventRecord(dict)
                if (eventPayload != null) {
                    upsertEventPayload(eventPayload, isCompleted = false, syncStatus = "synced")
                }
                return true
            }

            // --- FINISH EVENT: Watch finished an event (active → completed) ---
            "finish_event" -> {
                val eventPayload = extractEventRecord(dict)
                if (eventPayload != null) {
                    upsertEventPayload(eventPayload, isCompleted = true, syncStatus = "synced")
                }
                return true
            }

            // --- SYNC SETTINGS: Watch sends updated settings ---
            "sync_settings" -> {
                applyRemoteSettings(dict["settings"])
                return true
            }

            else -> return false
        }
    }

    // =====================================================================
    // SECTION 3: Public API — Send commands to watch
    // =====================================================================

    /**
     * Requests a full sync from the watch. Called when the phone connects
     * or when the user manually triggers a sync.
     */
    fun requestFullSync() {
        sendFullSync(command = "sync_request")
    }

    /**
     * Forces a complete resync — clears local cached sync state and
     * re-sends everything with `is_force_update: true`.
     * The watch will also resend all its data, ignoring previous sync state.
     */
    fun forceResync() {
        sendFullSync(command = "sync_request", isForceUpdate = true)
    }

    /**
     * Saves a new event locally and sends `create_event` to the watch.
     * Call this when the user creates a new event on the phone.
     */
    fun createEvent(eventPayload: Map<String, Any?>) {
        upsertEventPayload(eventPayload, isCompleted = false, syncStatus = "pending", source = "phone")
        deviceManager.sendMessageToWatch(
            mapOf(
                "command" to "create_event",
                "source" to "phone",
                "event" to eventPayload
            )
        )
    }

    /**
     * Deletes an event locally and sends `delete_event` to the watch.
     * @param id The event's sync ID
     */
    fun deleteEvent(id: Int) {
        applyDeletedEventId(id)
        persistSyncState()
        deviceManager.sendMessageToWatch(
            mapOf(
                "command" to "delete_event",
                "source" to "phone",
                "id" to id
            )
        )
    }

    /**
     * Finishes an event locally (moves active → completed) and sends
     * `finish_event` to the watch.
     * Call this if the phone needs to mark an event as completed.
     */
    fun finishEvent(eventPayload: Map<String, Any?>) {
        upsertEventPayload(eventPayload, isCompleted = true, syncStatus = "pending", source = "phone")
        deviceManager.sendMessageToWatch(
            mapOf(
                "command" to "finish_event",
                "source" to "phone",
                "event" to eventPayload
            )
        )
    }

    /**
     * Sends all current settings to the watch as a sync_settings command.
     * Call this when the user changes any setting on the phone.
     */
    fun sendSettings() {
        deviceManager.sendMessageToWatch(
            mapOf(
                "command" to "sync_settings",
                "source" to "phone",
                "settings" to getSettingsPayload()
            )
        )
    }

    // =====================================================================
    // SECTION 4: Full Sync Transmit
    // =====================================================================

    /**
     * Sends a full sync payload to the watch.
     * @param command "sync_request" (asking watch to respond) or "sync_all" (sending our data)
     * @param isForceUpdate when true, tells the watch to ignore previous sync state
     */
    private fun sendFullSync(command: String, isForceUpdate: Boolean = false) {
        deviceManager.sendMessageToWatch(
            mapOf(
                "command" to command,
                "source" to "phone",
                "is_force_update" to isForceUpdate,
                "activeEvents" to activeEventPayloads.toList(),
                "completedEvents" to completedEventPayloads.toList(),
                "deletedEventIds" to deletedEventIds.toList(),
                "settings" to getSettingsPayload()
            )
        )
    }

    // =====================================================================
    // SECTION 5: Event Merge & Upsert Logic
    // =====================================================================

    private fun mergeEventPayloads(payloads: List<Map<String, Any?>>, isCompleted: Boolean) {
        for (payload in payloads) {
            upsertEventPayload(payload, isCompleted = isCompleted, syncStatus = "synced")
        }
    }

    private fun upsertEventPayload(
        payload: Map<String, Any?>,
        isCompleted: Boolean,
        syncStatus: String,
        source: String = "watch",
    ) {
        val normalized = payload.toMutableMap()
        val id = extractEventId(normalized) ?: (System.currentTimeMillis() / 1000).toInt()

        // Skip events that were locally deleted
        if (deletedEventIds.contains(id)) return

        normalized["id"] = id
        normalized["syncStatus"] = syncStatus
        // Remove legacy syncType if present
        normalized.remove("syncType")

        if (isCompleted) {
            // Remove from active (it's finished)
            activeEventPayloads.removeAll { extractEventId(it) == id }
            upsertInList(normalized, completedEventPayloads)
        } else {
            // Only add as active if not already completed
            if (completedEventPayloads.any { extractEventId(it) == id }) {
                persistSyncState()
                return
            }
            upsertInList(normalized, activeEventPayloads)
        }

        // Mirror to Firestore so the event reaches the cloud + the other phone.
        // The repo preserves write-once id/source/createdAt, so an echoed
        // phone-created event stays source="phone" even when re-synced as "watch".
        writeEventToFirestore(normalized, isCompleted, syncStatus, source)
        persistSyncState()
    }

    private fun upsertInList(
        payload: MutableMap<String, Any?>,
        list: MutableList<MutableMap<String, Any?>>
    ) {
        val id = extractEventId(payload)
        if (id != null) {
            val index = list.indexOfFirst { extractEventId(it) == id }
            if (index >= 0) {
                list[index] = payload
            } else {
                list.add(0, payload)
            }
        } else {
            list.add(0, payload)
        }
    }

    // =====================================================================
    // SECTION 6: Deleted Event ID Tracking
    // =====================================================================

    private fun applyDeletedEventIds(ids: List<Int>) {
        for (id in ids) {
            applyDeletedEventId(id)
        }
    }

    private fun applyDeletedEventId(id: Int) {
        if (!deletedEventIds.contains(id)) {
            deletedEventIds.add(id)
        }
        activeEventPayloads.removeAll { extractEventId(it) == id }
        completedEventPayloads.removeAll { extractEventId(it) == id }
        rebuildObservableLists()
        deleteEventInFirestore(id)
    }

    // =====================================================================
    // SECTION 7: Pruning & Cleanup
    // =====================================================================

    /** Removes active payloads that already exist in completed. */
    private fun pruneActivePayloadsAlreadyCompleted() {
        val completedIds = completedEventPayloads.mapNotNull { extractEventId(it) }.toSet()
        if (completedIds.isEmpty()) return
        activeEventPayloads.removeAll { payload ->
            val id = extractEventId(payload)
            id != null && completedIds.contains(id)
        }
    }

    /** Removes deleted IDs that no longer exist in any event list. */
    private fun pruneDeletedEventIds() {
        val activeIds = activeEventPayloads.mapNotNull { extractEventId(it) }.toSet()
        val completedIds = completedEventPayloads.mapNotNull { extractEventId(it) }.toSet()
        deletedEventIds.removeAll { id ->
            !activeIds.contains(id) && !completedIds.contains(id)
        }
    }

    // =====================================================================
    // SECTION 8: Settings Sync
    // =====================================================================

    /** Returns a map of all synced settings from SharedPreferences, plus computed
     * millimeter step lengths for the watch (mirrors iOS getSettingsPayload). */
    fun getSettingsPayload(): Map<String, Any?> {
        val stored = prefs.getString(KEY_SETTINGS, null)
        val settingsMap = if (stored != null) jsonToMap(JSONObject(stored)) else emptyMap()
        val payload = SETTINGS_KEYS.associateWith { settingsMap[it] }.toMutableMap()

        settingDouble(settingsMap["walking_gait"])?.let {
            payload["walking_step_length"] =
                GaitStrideCalculator.millimeters(it, fullGaitUnit(settingsMap["walking_gait_measure"]))
        }
        settingDouble(settingsMap["running_gait"])?.let {
            payload["running_step_length"] =
                GaitStrideCalculator.millimeters(it, fullGaitUnit(settingsMap["running_gait_measure"]))
        }
        return payload
    }

    /** Asks the watch to send its current settings (incl. height) via a sync_settings
     * reply. Called on watch-app-ready and on Profile entry (mirrors iOS requestSettings). */
    fun requestWatchSettings() {
        deviceManager.sendMessageToWatch(
            mapOf("command" to "request_settings", "source" to "phone")
        )
    }

    /** Updates a single setting and persists it. */
    fun updateSetting(key: String, value: Any?) {
        val stored = prefs.getString(KEY_SETTINGS, null)
        val settingsMap = if (stored != null) jsonToMap(JSONObject(stored)).toMutableMap() else mutableMapOf()
        settingsMap[key] = value
        prefs.edit().putString(KEY_SETTINGS, JSONObject(settingsMap).toString()).apply()
    }

    /**
     * Applies settings received from the watch.
     * Only updates keys that are present and non-null in the remote payload.
     */
    private fun applyRemoteSettings(settings: Any?) {
        val settingsMap = when (settings) {
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                settings as Map<String, Any?>
            }
            else -> return
        }

        val stored = prefs.getString(KEY_SETTINGS, null)
        val localSettings = if (stored != null) jsonToMap(JSONObject(stored)).toMutableMap() else mutableMapOf()

        for (key in SETTINGS_KEYS) {
            val value = settingsMap[key]
            if (value != null) {
                localSettings[key] = value
            }
        }

        prefs.edit().putString(KEY_SETTINGS, JSONObject(localSettings).toString()).apply()
        AppLogger.d("[$TAG] Settings applied: $localSettings")

        // Mirror watch settings into the Firestore user doc (parity with iOS): alerts,
        // body metrics, and gait re-derived from the watch's height when present.
        applyRemoteSettingsToProfile(settingsMap)
    }

    // =====================================================================
    // SECTION 8B: Firestore Write-Through (shared thepaceapp backend)
    // Every BLE mutation is mirrored to Firestore so events/settings reach the
    // cloud and the other phone. All writes are no-ops until a user is signed in.
    // =====================================================================

    private fun writeEventToFirestore(
        payload: Map<String, Any?>,
        isCompleted: Boolean,
        syncStatus: String,
        source: String,
    ) {
        val userId = authManager.currentUid ?: return
        appScope.launch {
            runCatching {
                eventRepository.upsert(payload, isCompleted, syncStatus, source, userId)
            }.onFailure { AppLogger.e("[$TAG] Firestore event upsert failed", it) }
        }
    }

    private fun deleteEventInFirestore(id: Int) {
        val userId = authManager.currentUid ?: return
        appScope.launch {
            runCatching { eventRepository.softDelete(id, userId) }
                .onFailure { AppLogger.e("[$TAG] Firestore soft delete failed", it) }
        }
    }

    // Mirrors iOS applyRemoteSettings profile write: alerts + body metrics, and gait
    // RE-DERIVED from the watch's height when present (overwrites manual gait — accepted
    // parity trade-off; iOS has no manual-override flag). `settings` is the raw watch map.
    // Gait unit boundary: watch "ft"/"m" ↔ Firestore/app "Feet"/"Meters".
    private fun applyRemoteSettingsToProfile(settings: Map<String, Any?>) {
        val userId = authManager.currentUid ?: return
        val heightCm = settingDouble(settings["user_height"])
        val weightKg = settingDouble(settings["user_weight"])?.let { it / 1000.0 } // grams → kg

        appScope.launch {
            (settings["vibrate_alert"] as? Boolean)?.let {
                runCatching { userProfileRepository.updateIntervalVibrate(userId, it) }
            }
            (settings["beep_alert"] as? Boolean)?.let {
                runCatching { userProfileRepository.updateIntervalBeep(userId, it) }
            }
            if (heightCm != null || weightKg != null) {
                runCatching { userProfileRepository.updateBodyMetrics(userId, heightCm, weightKg) }
            }

            if (heightCm != null && heightCm > 0) {
                // Derive gait from height, save it, and push the computed mm back to the watch.
                val derived = GaitStrideCalculator.gait(
                    heightCm = heightCm,
                    walkingUnit = fullGaitUnit(settings["walking_gait_measure"]),
                    runningUnit = fullGaitUnit(settings["running_gait_measure"]),
                )
                val gaitDoc = GaitDocument(
                    walkingStepLength = derived.walkingStepLength,
                    walkingUnit = derived.walkingUnit,
                    runningStepLength = derived.runningStepLength,
                    runningUnit = derived.runningUnit,
                )
                runCatching { userProfileRepository.updateGait(userId, gaitDoc) }
                    .onFailure { AppLogger.e("[$TAG] Firestore gait update failed", it) }
                persistGaitToPrefs(derived)
                sendSettings()
            } else {
                // Legacy fallback: use the watch's reported gait values directly.
                val walking = settingDouble(settings["walking_gait"])
                val running = settingDouble(settings["running_gait"])
                if (walking != null || running != null) {
                    val gait = GaitDocument(
                        walkingStepLength = walking ?: 0.0,
                        walkingUnit = fullGaitUnit(settings["walking_gait_measure"]),
                        runningStepLength = running ?: 0.0,
                        runningUnit = fullGaitUnit(settings["running_gait_measure"]),
                    )
                    runCatching { userProfileRepository.updateGait(userId, gait) }
                        .onFailure { AppLogger.e("[$TAG] Firestore gait update failed", it) }
                }
            }
        }
    }

    // Writes the height-derived gait into local prefs (watch "ft"/"m" units) so the
    // next getSettingsPayload sends the updated gait + computed mm to the watch.
    private fun persistGaitToPrefs(derived: GaitStrideCalculator.DerivedGait) {
        updateSetting("walking_gait", derived.walkingStepLength)
        updateSetting("walking_gait_measure", watchGaitUnit(derived.walkingUnit))
        updateSetting("running_gait", derived.runningStepLength)
        updateSetting("running_gait_measure", watchGaitUnit(derived.runningUnit))
    }

    // Firestore/app "Feet"/"Meters" → watch "ft"/"m".
    private fun watchGaitUnit(unit: String): String = if (unit.lowercase().startsWith("m")) "m" else "ft"

    // Lenient number parse for gait values arriving as Double/Number/String.
    private fun settingDouble(value: Any?): Double? = when (value) {
        is Double -> value
        is Number -> value.toDouble()
        is String -> value.toDoubleOrNull()
        else -> null
    }

    // Watch "ft"/"m" → app/Firestore full word "Feet"/"Meters".
    private fun fullGaitUnit(value: Any?): String =
        if ((value as? String)?.lowercase()?.startsWith("m") == true) "Meters" else "Feet"

    // =====================================================================
    // SECTION 9: Persistence
    // =====================================================================

    /** Persists all sync state to SharedPreferences and updates Compose-observable lists. */
    private fun persistSyncState() {
        pruneActivePayloadsAlreadyCompleted()
        pruneDeletedEventIds()
        rebuildObservableLists()

        prefs.edit()
            .putString(KEY_ACTIVE_EVENTS, payloadsToJson(activeEventPayloads))
            .putString(KEY_COMPLETED_EVENTS, payloadsToJson(completedEventPayloads))
            .putString(KEY_DELETED_IDS, JSONArray(deletedEventIds).toString())
            .apply()
    }

    private fun rebuildObservableLists() {
        activeEvents.clear()
        activeEvents.addAll(activeEventPayloads)
        completedEvents.clear()
        completedEvents.addAll(completedEventPayloads)
    }

    private fun loadEventPayloads(key: String): MutableList<MutableMap<String, Any?>> {
        val json = prefs.getString(key, null) ?: return mutableListOf()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                jsonToMap(array.getJSONObject(i)).toMutableMap()
            }.toMutableList()
        } catch (e: Exception) {
            AppLogger.e("[$TAG] Failed to load $key", e)
            mutableListOf()
        }
    }

    private fun loadDeletedEventIds(): MutableList<Int> {
        val json = prefs.getString(KEY_DELETED_IDS, null) ?: return mutableListOf()
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { array.getInt(it) }.toMutableList()
        } catch (e: Exception) {
            AppLogger.e("[$TAG] Failed to load deleted IDs", e)
            mutableListOf()
        }
    }

    // =====================================================================
    // SECTION 10: Payload Extraction Helpers
    // =====================================================================

    /**
     * Extracts an event record from various message formats.
     * Mirrors the watch's `getEventRecordFromPayload()` function.
     *
     * Supports:
     *   - `{ "event": { ... } }` — event nested under "event" key
     *   - `{ "payload": { ... } }` — event nested under "payload" key
     *   - `{ "name": ..., "date": ... }` — event fields directly in dict
     */
    private fun extractEventRecord(dict: Map<String, Any?>): Map<String, Any?>? {
        val event = dict["event"]
        if (event is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return event as Map<String, Any?>
        }

        val payload = dict["payload"]
        if (payload is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            return payload as Map<String, Any?>
        }

        if (dict.containsKey("name") || dict.containsKey("date") || dict.containsKey("distance")) {
            return dict
        }

        return null
    }

    /** Extracts an event ID from a payload, handling Int, Long, Double, and String types. */
    private fun extractEventId(payload: Map<String, Any?>): Int? {
        return when (val id = payload["id"]) {
            is Int -> id
            is Long -> id.toInt()
            is Double -> id.toInt()
            is String -> id.toIntOrNull()
            else -> null
        }
    }

    /** Extracts a list of Ints from various input types (List, etc.). */
    private fun extractIntList(value: Any?): List<Int> {
        if (value is List<*>) {
            return value.mapNotNull { item ->
                when (item) {
                    is Int -> item
                    is Long -> item.toInt()
                    is Double -> item.toInt()
                    is String -> item.toIntOrNull()
                    else -> null
                }
            }
        }
        return emptyList()
    }

    /** Extracts a list of event payloads from various input types. */
    private fun extractPayloadList(value: Any?): List<Map<String, Any?>> {
        if (value is List<*>) {
            return value.mapNotNull { item ->
                if (item is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    item as Map<String, Any?>
                } else null
            }
        }
        return emptyList()
    }

    // =====================================================================
    // SECTION 11: JSON Serialization Helpers
    // =====================================================================

    private fun payloadsToJson(payloads: List<Map<String, Any?>>): String {
        val array = JSONArray()
        for (payload in payloads) {
            array.put(JSONObject(payload))
        }
        return array.toString()
    }

    private fun jsonToMap(json: JSONObject): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = json.opt(key)
            map[key] = when (value) {
                JSONObject.NULL -> null
                is JSONObject -> jsonToMap(value)
                is JSONArray -> jsonArrayToList(value)
                else -> value
            }
        }
        return map
    }

    private fun jsonArrayToList(array: JSONArray): List<Any?> {
        return (0 until array.length()).map { i ->
            when (val value = array.opt(i)) {
                JSONObject.NULL -> null
                is JSONObject -> jsonToMap(value)
                is JSONArray -> jsonArrayToList(value)
                else -> value
            }
        }
    }
}
