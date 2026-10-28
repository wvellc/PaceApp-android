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
import androidx.compose.ui.graphics.Brush
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

// Plan-details dialog for subscribers (mirrors iOS): a dark card over a dimmed backdrop.
// Header (Active Plan / Pace Pro), the plan facts, then Manage Subscription + Close.
@Composable
internal fun PlanDetailsDialog(
	planInfo: ProPlanInfo,
	onManage: () -> Unit,
	onClose: () -> Unit,
) {
	Dialog(onDismissRequest = onClose) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.clip(RoundedCornerShape(20.dp))
				.background(Brush.verticalGradient(AppColors.backgroundGradient))
				.padding(20.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp),
		) {
			// Header
			Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
				Text(
					text = stringResource(R.string.plan_active_label),
					style = AppTheme.typography.medium.copy(color = AppColors.FashionGray, fontSize = 13.sp),
				)
				Text(
					text = stringResource(R.string.pace_pro_title),
					style = AppTheme.typography.bold.copy(color = AppColors.FluorescentMint, fontSize = 24.sp),
				)
			}

			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(1.dp)
					.background(AppColors.White20),
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

			// Primary — Manage Subscription (brand gradient).
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.clip(CircleShape)
					.background(Brush.verticalGradient(AppColors.buttonGradient))
					.defaultClickable(onClick = onManage)
					.padding(vertical = 12.dp),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = stringResource(R.string.pace_pro_manage),
					style = AppTheme.typography.semiBold.copy(color = AppColors.White, fontSize = 14.sp),
				)
			}

			// Secondary — Close (bordered).
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.clip(CircleShape)
					.border(1.dp, AppColors.White20, CircleShape)
					.defaultClickable(onClick = onClose)
					.padding(vertical = 12.dp),
				contentAlignment = Alignment.Center,
			) {
				Text(
					text = stringResource(R.string.plan_close),
					style = AppTheme.typography.semiBold.copy(color = AppColors.White, fontSize = 14.sp),
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
			style = AppTheme.typography.medium.copy(color = AppColors.White.copy(alpha = 0.6f), fontSize = 14.sp),
		)
		Text(
			text = value,
			textAlign = TextAlign.End,
			style = AppTheme.typography.semiBold.copy(color = AppColors.White, fontSize = 14.sp),
		)
	}
}

private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
