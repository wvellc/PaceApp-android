package net.paceapp.core.revenuecat

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// Fire-and-forget request to present the Pace Pro paywall, decoupled from the UI layer —
// mirrors the app's AppAlerts pattern. Any ViewModel calls show(); MainActivity collects
// launchRequests and presents the dashboard paywall via RevenueCat's PaywallActivityLauncher
// (the official full-screen presentation —
// https://www.revenuecat.com/docs/tools/paywalls/displaying-paywalls).
object PaywallController {
	// One-shot launch events, not a retained flag: a replay of 0 means a MainActivity
	// recreation (e.g. rotation) while the paywall is open never relaunches it.
	private val _launchRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
	val launchRequests: SharedFlow<Unit> = _launchRequests.asSharedFlow()

	// What to do once the user unlocks Pro from the paywall — typically proceed into the
	// flow they were gated out of. Held until the paywall closes; cleared on dismiss so a
	// VM-bound callback can't outlive its screen.
	@Volatile
	private var onProUnlocked: (() -> Unit)? = null

	fun show(onProUnlocked: (() -> Unit)? = null) {
		this.onProUnlocked = onProUnlocked
		_launchRequests.tryEmit(Unit)
	}

	// Pro unlocked (completed/restored purchase) — run the pending action so the user
	// proceeds without a second tap.
	fun completeAndDismiss() {
		val action = onProUnlocked
		onProUnlocked = null
		action?.invoke()
	}

	// Closed/cancelled without unlocking Pro — drop the pending action.
	fun dismiss() {
		onProUnlocked = null
	}
}
