package net.paceapp.features.main.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.core.revenuecat.PlanPeriod
import net.paceapp.core.revenuecat.ProPlanInfo
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Locale

// Plan-details dialog for subscribers: a light card. Pace Pro title, the plan facts, then Close.
@Composable
internal fun PlanDetailsDialog(
	planInfo: ProPlanInfo,
	onClose: () -> Unit,
) {
	Dialog(onDismissRequest = onClose) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(AppColors.White)
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			// Header
			Text(
				text = stringResource(R.string.pace_pro_title),
				style = AppTheme.typography.bold.copy(color = AppColors.DarkCharcoal, fontSize = 24.sp),
			)

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(1.dp)
					.background(AppColors.DarkCharcoal.copy(alpha = 0.12f)),
			)

			// Facts
			PlanRow(
				label = stringResource(R.string.plan_events_label),
				value = stringResource(R.string.plan_unlimited),
			)
			planInfo.period?.let { period ->
				PlanRow(
					label = stringResource(R.string.plan_billing_label),
					value = stringResource(
						if (period == PlanPeriod.YEARLY) R.string.plan_yearly else R.string.plan_monthly
					),
				)
			}
			planInfo.renewalDate?.let { date ->
				PlanRow(
					label = stringResource(
						if (planInfo.willRenew) R.string.plan_next_renewal else R.string.plan_expires_on
					),
					value = dateFormatter.format(date),
				)
			}

			// Close (bordered).
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.clip(CircleShape)
					.border(1.dp, AppColors.DarkCharcoal.copy(alpha = 0.3f), CircleShape)
					.defaultClickable(onClick = onClose)
					.padding(vertical = 12.dp),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = stringResource(R.string.plan_close),
					style = AppTheme.typography.semiBold.copy(color = AppColors.DarkCharcoal, fontSize = 14.sp),
				)
			}
		}
	}
}

@Composable
private fun PlanRow(label: String, value: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Text(
			modifier = Modifier.weight(1f),
			text = label,
			style = AppTheme.typography.medium.copy(color = AppColors.DarkCharcoal.copy(alpha = 0.6f), fontSize = 14.sp),
		)
		Text(
			text = value,
			textAlign = TextAlign.End,
			style = AppTheme.typography.semiBold.copy(color = AppColors.DarkCharcoal, fontSize = 14.sp),
		)
	}
}

private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
