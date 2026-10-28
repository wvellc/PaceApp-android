package net.paceapp.core.revenuecat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Fire-and-forget controller for the RevenueCat Customer Center — mirrors PaywallController.
// Call show() from anywhere (e.g. Settings "Manage Subscription") and AppCustomerCenterHost,
// mounted once at the MainActivity root, presents the full-screen Customer Center.
object CustomerCenterController {
	private val _isShowing = MutableStateFlow(false)
	val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

	fun show() {
		_isShowing.value = true
	}

	fun dismiss() {
		_isShowing.value = false
	}
}
