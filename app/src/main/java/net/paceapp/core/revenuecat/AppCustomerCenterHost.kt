package net.paceapp.core.revenuecat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.customercenter.CustomerCenter

// Global Customer Center host — mount once at the MainActivity root (next to AppPaywallHost).
// Presents the RevenueCat Customer Center (manage / cancel / restore an active subscription)
// full-screen when CustomerCenterController.show() is called. Mirrors iOS's Customer Center
// opened from Settings → Manage Subscription.
@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
fun AppCustomerCenterHost() {
	val isShowing by CustomerCenterController.isShowing.collectAsState()
	if (!isShowing) return

	CustomerCenter(
		modifier = Modifier.fillMaxSize(),
		onDismiss = { CustomerCenterController.dismiss() },
	)
}
