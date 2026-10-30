package net.paceapp.features.main.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Home event-list footer shown to free users once they hit the free event limit. Tapping it
// opens the Pace Pro paywall; it disappears the moment the user upgrades (state.showProUpsell).
// The limit is passed in (remote-configurable) so the subtitle always shows the live value.
@Composable
internal fun ProUpsellFooter(
	freeEventLimit: Int,
	onClick: () -> Unit,
) {
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.clip(RoundedCornerShape(16.dp))
			.background(Brush.verticalGradient(AppColors.backgroundGradient))
			.border(1.dp, AppColors.FluorescentMint.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
			.defaultClickable(onClick = onClick)
			.padding(16.dp),
		horizontalArrangement = Arrangement.spacedBy(14.dp),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Image(
			modifier = Modifier.size(32.dp),
			contentDescription = null,
			painter = painterResource(R.drawable.ic_pro_seal),
		)
		Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
			Text(
				text = stringResource(R.string.pro_upsell_footer_title),
				style = AppTheme.typography.semiBold.copy(
					color = AppColors.FluorescentMint,
					fontSize = 15.sp,
				),
			)
			Text(
				text = stringResource(R.string.pro_upsell_footer_subtitle, freeEventLimit),
				style = AppTheme.typography.medium.copy(
					color = AppColors.White.copy(alpha = 0.7f),
					fontSize = 13.sp,
				),
			)
		}
	}
}
