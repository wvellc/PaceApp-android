package net.paceapp.core.revenuecat

// RevenueCat configuration — mirrors iOS RevenueCatConst / SubscriptionManager.
object RevenueCatConst {
	// Google Play public SDK key (RevenueCat → Pace App (Play Store), app61491fe922).
	// Public and safe to commit — it only identifies the app to RevenueCat.
	const val apiKey = "goog_UtiYwJQFzyvihGSbtLEqEUZlPtM"

	// Entitlement that unlocks Pace Pro (matches the iOS RevenueCat project).
	const val entitlementPro = "pace_app_pro"

	// Fallback free-tier limit: a free user may keep at most this many live events (active +
	// completed) before the paywall gates further creation. The live value is remote-configurable
	// via Firebase Remote Config ("free_event_limit"); this constant is the offline/default.
	// Mirrors iOS SubscriptionManager.freeEventLimit.
	const val freeEventLimit = 15
}
