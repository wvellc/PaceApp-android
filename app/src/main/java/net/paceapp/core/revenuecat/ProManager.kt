package net.paceapp.core.revenuecat

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.awaitCustomerInfo
import com.revenuecat.purchases.awaitLogIn
import com.revenuecat.purchases.awaitLogOut
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.wvelabs.core_network.di.ApplicationScope
import com.wvelabs.core_network.utils.AppLogger
import net.paceapp.core.config.RemoteConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

// Billing cadence of the active Pace Pro plan, derived from the store product id.
enum class PlanPeriod { MONTHLY, YEARLY }

// Active-subscription details for the plan-details dialog (mirrors iOS). Null when not Pro.
data class ProPlanInfo(
	val period: PlanPeriod?,
	val renewalDate: Date?,
	val willRenew: Boolean,
)

// Single source of truth for Pace Pro entitlement state — mirrors iOS SubscriptionManager.
// Backed by RevenueCat's CustomerInfo: seeded from the cache, refreshed on launch, and
// kept live via the updatedCustomerInfoListener. Identity (logIn/logOut) is driven from
// AuthManager so RevenueCat always tracks the Firebase uid, never an anonymous id.
@Singleton
class ProManager @Inject constructor(
	@param:ApplicationScope private val appScope: CoroutineScope,
	private val remoteConfigManager: RemoteConfigManager,
) {
	private val _isPro = MutableStateFlow(false)
	val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

	// Active-plan details for the Profile plan-details dialog. Null while not subscribed.
	private val _planInfo = MutableStateFlow<ProPlanInfo?>(null)
	val planInfo: StateFlow<ProPlanInfo?> = _planInfo.asStateFlow()

	init {
		// Purchases is configured in PaceApplication.onCreate, which always runs before any
		// injection point reaches this singleton — but guard anyway so a mis-order can't crash.
		if (Purchases.isConfigured) {
			Purchases.sharedInstance.updatedCustomerInfoListener =
				UpdatedCustomerInfoListener { info -> apply(info) }
			// Seed from cache, then network, via awaitCustomerInfo's default fetch policy.
			refresh()
		}
	}

	// Push the latest entitlement state into both flows from one CustomerInfo.
	private fun apply(info: CustomerInfo) {
		_isPro.value = info.isProActive()
		_planInfo.value = info.proPlanInfo()
	}

	// Pulls the latest CustomerInfo from RevenueCat (network with cache fallback).
	private fun refresh() {
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitCustomerInfo() }
				.onSuccess { apply(it) }
				.onFailure { AppLogger.w("[RevenueCat] customerInfo refresh failed: ${it.message}") }
		}
	}

	// Identify the purchaser as the signed-in Firebase user. Called on every sign-in and
	// on cold-start session restore (from AuthManager's auth-state listener).
	fun logIn(appUserId: String) {
		if (!Purchases.isConfigured) return
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitLogIn(appUserId) }
				.onSuccess { apply(it.customerInfo) }
				.onFailure { AppLogger.w("[RevenueCat] logIn failed: ${it.message}") }
		}
	}

	// Drop the RevenueCat identity on sign-out / account deletion. Skip when already
	// anonymous — awaitLogOut throws LogOutWithAnonymousUserError in that case.
	fun logOut() {
		_isPro.value = false
		_planInfo.value = null
		if (!Purchases.isConfigured || Purchases.sharedInstance.isAnonymous) return
		appScope.launch {
			runCatching { Purchases.sharedInstance.awaitLogOut() }
				.onSuccess { apply(it) }
				.onFailure { AppLogger.w("[RevenueCat] logOut failed: ${it.message}") }
		}
	}

	// The gate: a free user may create an event only below the free limit; Pro is unlimited.
	// The limit is remote-configurable (Firebase Remote Config), falling back to the compiled
	// constant. Mirrors iOS SubscriptionManager.canCreateEvent(currentLiveEventCount:).
	fun canCreateEvent(currentLiveEventCount: Int): Boolean =
		isPro.value || currentLiveEventCount < remoteConfigManager.currentFreeEventLimit

	private fun CustomerInfo.isProActive(): Boolean =
		entitlements[RevenueCatConst.entitlementPro]?.isActive == true

	// Derive the plan-details view model from the active Pace Pro entitlement. Period comes
	// from the store product id (…:yearly-auto / pro_yearly → Yearly, …month… → Monthly);
	// renewalDate + willRenew come straight off the entitlement (mirrors iOS).
	private fun CustomerInfo.proPlanInfo(): ProPlanInfo? {
		val entitlement = entitlements[RevenueCatConst.entitlementPro] ?: return null
		if (!entitlement.isActive) return null
		val id = entitlement.productIdentifier
		val period = when {
			id.contains("year", ignoreCase = true) || id.contains("annual", ignoreCase = true) -> PlanPeriod.YEARLY
			id.contains("month", ignoreCase = true) -> PlanPeriod.MONTHLY
			else -> null
		}
		return ProPlanInfo(
			period = period,
			renewalDate = entitlement.expirationDate,
			willRenew = entitlement.willRenew,
		)
	}
}
