package net.paceapp.core.revenuecat

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitLogOut
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Single source of truth for Pace Pro entitlement state — mirrors iOS SubscriptionManager.
// Backed by RevenueCat's CustomerInfo: seeded from the cache, refreshed on launch, and
// kept live via the updatedCustomerInfoListener. Identity (logIn/logOut) is driven from
// AuthManager so RevenueCat always tracks the Firebase uid, never an anonymous id.
@Singleton
class ProManager @Inject constructor(
	@param:ApplicationScope private val appScope: CoroutineScope,
) {
	private val _isPro = MutableStateFlow(false)
	val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

	init {
		// Purchases is configured in PaceApplication.onCreate, which always runs before any
		// injection point reaches this singleton — but guard anyway so a mis-order can't crash.
		if (Purchases.isConfigured) {
			Purchases.sharedInstance.updatedCustomerInfoListener =
				UpdatedCustomerInfoListener { info -> _isPro.value = info.isProActive() }
			// Seed from cache, then network, via awaitCustomerInfo's default fetch policy.
			refresh()
		}
	}

	// Pulls the latest CustomerInfo from RevenueCat (network with cache fallback).
	private fun refresh() {
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitCustomerInfo() }
				.onSuccess { _isPro.value = it.isProActive() }
				.onFailure { AppLogger.w("[RevenueCat] customerInfo refresh failed: ${it.message}") }
		}
	}

	// Identify the purchaser as the signed-in Firebase user. Called on every sign-in and
	// on cold-start session restore (from AuthManager's auth-state listener).
	fun logIn(appUserId: String) {
		if (!Purchases.isConfigured) return
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitLogIn(appUserId) }
				.onSuccess { _isPro.value = it.customerInfo.isProActive() }
				.onFailure { AppLogger.w("[RevenueCat] logIn failed: ${it.message}") }
		}
	}

	// Drop the RevenueCat identity on sign-out / account deletion. Skip when already
	// anonymous — awaitLogOut throws LogOutWithAnonymousUserError in that case.
	fun logOut() {
		_isPro.value = false
		if (!Purchases.isConfigured || Purchases.sharedInstance.isAnonymous) return
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitLogOut() }
				.onSuccess { _isPro.value = it.isProActive() }
				.onFailure { AppLogger.w("[RevenueCat] logOut failed: ${it.message}") }
		}
	}

	// The gate: a free user may create an event only below the free limit; Pro is unlimited.
	// Mirrors iOS SubscriptionManager.canCreateEvent(currentLiveEventCount:).
	fun canCreateEvent(currentLiveEventCount: Int): Boolean =
		isPro.value || currentLiveEventCount < RevenueCatConst.freeEventLimit

	private fun CustomerInfo.isProActive(): Boolean =
		entitlements[RevenueCatConst.entitlementPro]?.isActive == true
}
