package net.paceapp.core.revenuecat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener

// Global paywall host — mount once at the MainActivity root (next to AppNavHost). Presents
// the dashboard-configured Pace Pro paywall (offering `default`, the current offering) when
// PaywallController.show() is called, and dismisses on a completed/restored purchase that
// activates pace_app_pro so the user can proceed.
//
// Gilroy branding: current RevenueCatUI (v2 paywalls) has no FontProvider API — the paywall
// renders the fonts chosen in the dashboard Paywall Editor, matched to a bundled res/font
// file by name. Gilroy is already bundled (R.font.gilroy_*); it renders in-brand as long as
// the Paywall Editor's font name matches those resource file names. Nothing to wire here.
@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
fun AppPaywallHost() {
	val isShowing by PaywallController.isShowing.collectAsState()
	if (!isShowing) return

	PaywallDialog(
		PaywallDialogOptions.Builder()
			.setRequiredEntitlementIdentifier(RevenueCatConst.entitlementPro)
			.setDismissRequest { PaywallController.dismiss() }
			.setListener(object : PaywallListener {
				override fun onPurchaseCompleted(
					customerInfo: CustomerInfo,
					storeTransaction: StoreTransaction,
				) {
					PaywallController.completeAndDismiss()
				}

				override fun onRestoreCompleted(customerInfo: CustomerInfo) {
					if (customerInfo.entitlements[RevenueCatConst.entitlementPro]?.isActive == true) {
						PaywallController.completeAndDismiss()
					}
				}
			})
			.build()
	)
}
