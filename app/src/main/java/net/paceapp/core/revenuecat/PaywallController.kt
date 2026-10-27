package net.paceapp.core.revenuecat

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Fire-and-forget request to present the Pace Pro paywall, decoupled from the UI layer —
// mirrors the app's AppAlerts pattern. Any ViewModel can call show(); the AppPaywallHost
// composable (mounted once in MainActivity) observes this and presents the RevenueCatUI
// paywall over the whole app.
object PaywallController {
	private val _isShowing = MutableStateFlow(false)
	val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

	// What to do once the user unlocks Pro from the paywall — typically proceed into the
	// flow they were gated out of. Held only while the paywall is showing; cleared on dismiss
	// so a VM-bound callback can't outlive its screen.
	@Volatile
	private var onProUnlocked: (() -> Unit)? = null

	fun show(onProUnlocked: (() -> Unit)? = null) {
		this.onProUnlocked = onProUnlocked
		_isShowing.value = true
	}

	// Pro unlocked (completed/restored purchase) — dismiss, then run the pending action so
	// the user proceeds without a second tap.
	fun completeAndDismiss() {
		val action = onProUnlocked
		onProUnlocked = null
		_isShowing.value = false
		action?.invoke()
	}

	// Dismissed without unlocking Pro — drop the pending action.
	fun dismiss() {
		onProUnlocked = null
		_isShowing.value = false
	}
}
