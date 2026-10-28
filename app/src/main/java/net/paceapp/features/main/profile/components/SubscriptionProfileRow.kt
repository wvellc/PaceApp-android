package net.paceapp.features.main.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Profile "Subscription" row (mirrors iOS, placed right under Manage your watch). Free users
// see "Subscription" → paywall; subscribers see "Subscribed" with a mint seal → plan dialog.
// Matches the other Profile rows: white card, 48dp neon-aqua icon badge, same type style.
@Composable
internal fun SubscriptionProfileRow(
	isPro: Boolean,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(8.dp))
			.background(color = AppColors.White)
			.defaultClickable(onClick = onClick)
			.padding(8.dp),
		horizontalArrangement = Arrangement.spacedBy(16.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			modifier = Modifier.size(48.dp),
			contentDescription = null,
			painter = painterResource(R.drawable.ic_subscription),
		)
		Text(
			modifier = Modifier.weight(1f),
			text = stringResource(
				if (isPro) R.string.subscription_subscribed else R.string.subscription_title
			),
			style = AppTheme.typography.semiBold.copy(
				color = AppColors.DarkCharcoal,
				fontSize = 16.sp,
			),
		)
		if (isPro) {
			Image(
				modifier = Modifier.size(20.dp),
				contentDescription = null,
				painter = painterResource(R.drawable.ic_pro_seal),
			)
		}
	}
}
