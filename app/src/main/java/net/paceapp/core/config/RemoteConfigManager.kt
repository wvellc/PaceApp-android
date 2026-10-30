package net.paceapp.core.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.paceapp.BuildConfig
import net.paceapp.core.revenuecat.RevenueCatConst
import javax.inject.Inject
import javax.inject.Singleton

// Firebase Remote Config — single source for remotely-tunable values, synced to iOS via the
// shared thepaceapp project. Defaults are applied immediately, so every getter is safe before
// the first fetch; fetchAndActivate then refreshes the live values. Mirrors iOS RemoteConfigManager.
@Singleton
class RemoteConfigManager @Inject constructor() {

	private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
		setConfigSettingsAsync(
			FirebaseRemoteConfigSettings.Builder()
				// Debug fetches every launch; release honors a 1h cache so we don't hammer the backend.
				.setMinimumFetchIntervalInSeconds(if (BuildConfig.DEBUG) 0 else MIN_FETCH_INTERVAL_SECONDS)
				.build()
		)
		// Fallback = the compiled constant, so the gate works offline / before the first fetch.
		setDefaultsAsync(mapOf<String, Any>(KEY_FREE_EVENT_LIMIT to RevenueCatConst.freeEventLimit.toLong()))
	}

	// Free-tier event limit, live. Seeded from the defaults, updated after each activate.
	private val _freeEventLimit = MutableStateFlow(readFreeEventLimit())
	val freeEventLimit: StateFlow<Int> = _freeEventLimit.asStateFlow()

	// Snapshot read for one-off gate checks (create/duplicate) that don't need the flow.
	val currentFreeEventLimit: Int get() = _freeEventLimit.value

	init {
		refresh()
	}

	// Fetch + activate the latest values, then publish them. Failures keep the last good value.
	fun refresh() {
		remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
			if (task.isSuccessful) {
				_freeEventLimit.value = readFreeEventLimit()
			} else {
				AppLogger.w("[RemoteConfig] fetchAndActivate failed: ${task.exception?.message}")
			}
		}
	}

	// A non-positive remote value is treated as unset — fall back to the compiled constant.
	private fun readFreeEventLimit(): Int {
		val value = remoteConfig.getLong(KEY_FREE_EVENT_LIMIT).toInt()
		return if (value > 0) value else RevenueCatConst.freeEventLimit
	}

	companion object {
		// Shared key with iOS — must match the Firebase console parameter name.
		private const val KEY_FREE_EVENT_LIMIT = "free_event_limit"
		private const val MIN_FETCH_INTERVAL_SECONDS = 3600L
	}
}
