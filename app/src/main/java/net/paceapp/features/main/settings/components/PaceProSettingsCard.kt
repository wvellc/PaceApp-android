package net.paceapp.features.main.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Inline Pace Pro card for the Settings screen — mirrors iOS SettingScreen.subscriptionSection.
// Shows the Pace Pro title, a status line (You're on Pace Pro / Unlock unlimited events), and a
// brand-gradient pill: "Manage Subscription" (subscribers → Customer Center) or "Go Pro" (free
// users → paywall).
@Composable
internal fun PaceProSettingsCard(
	isPro: Boolean,
	onGoPro: () -> Unit,
	onManageSubscription: () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.background(AppColors.White)
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp),
	) {
		Column(
			modifier = Modifier.fillMaxWidth(),
			verticalArrangement = Arrangement.spacedBy(2.dp),
		) {
			Text(
				text = stringResource(R.string.pace_pro_title),
				style = AppTheme.typography.semiBold.copy(
					color = AppColors.DarkCharcoal,
					fontSize = 16.sp,
				),
			)
			Text(
				text = stringResource(
					if (isPro) R.string.pace_pro_status_active else R.string.pace_pro_status_inactive
				),
				style = AppTheme.typography.medium.copy(
					color = if (isPro) AppColors.FluorescentMint else AppColors.FashionGray,
					fontSize = 13.sp,
				),
			)
		}

		// Brand-gradient pill — matches the Strava Resync button (and iOS's gradient button).
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clip(CircleShape)
				.background(Brush.verticalGradient(AppColors.buttonGradient))
				.clickable(onClick = if (isPro) onManageSubscription else onGoPro)
				.padding(vertical = 10.dp),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = stringResource(
					if (isPro) R.string.pace_pro_manage else R.string.pace_pro_go_pro
				),
				style = AppTheme.typography.semiBold.copy(
					color = AppColors.White,
					fontSize = 14.sp,
				),
			)
		}
	}
}
