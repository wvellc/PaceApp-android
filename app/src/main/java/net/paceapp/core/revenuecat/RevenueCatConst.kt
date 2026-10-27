package net.paceapp.core.revenuecat

// RevenueCat configuration — mirrors iOS RevenueCatConst / SubscriptionManager.
object RevenueCatConst {
	// Google Play public SDK key. It is public (safe to commit), but the Play Store
	// subscriptions and the RevenueCat Play Store app connection are still being set up,
	// so the real key does not exist yet.
	// TODO: paste the goog_ key from RevenueCat → Play Store app
	const val apiKey = "goog_YOUR_KEY_HERE"

	// Entitlement that unlocks Pace Pro (matches the iOS RevenueCat project).
	const val entitlementPro = "pace_app_pro"

	// A free user may keep at most this many live events (active + completed) before the
	// paywall gates further creation. Mirrors iOS SubscriptionManager.freeEventLimit.
	const val freeEventLimit = 15
}
