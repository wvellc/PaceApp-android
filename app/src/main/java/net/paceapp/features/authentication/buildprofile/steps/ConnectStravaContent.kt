package net.paceapp.features.authentication.buildprofile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import net.paceapp.core.components.CustomProfileImage
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Final onboarding step. Mirrors iOS ConnectStravaStepView: purely presentational — it
// reflects the Strava connection state; the footer "Connect" button (BuildProfileContent)
// triggers OAuth, and once connected it shows "Connected as {name}".
@Composable
fun ConnectStravaContent(
    isConnected: Boolean,
    athleteName: String?,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))
        // Description
        Text(
            text = stringResource(R.string.connect_strava_step_message),
            style = AppTheme.typography.medium.copy(
                color = AppColors.White,
                fontSize = 20.sp,
                lineHeight = 32.sp,
            ),
        )
        Spacer(Modifier.height(64.dp))
        // Strava logo
        CustomProfileImage(
            imageUrl = null,
            backgroundColor = AppColors.Transparent,
            shape = RoundedCornerShape(
                topStart = 72.dp,
                topEnd = 72.dp,
                bottomEnd = 16.dp,
                bottomStart = 16.dp,
            ),
            placeholder = painterResource(id = R.drawable.ic_strava_logo),
            isClickable = false,
            showLabel = false,
        )

        if (isConnected && !athleteName.isNullOrEmpty()) {
            Spacer(Modifier.height(42.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppColors.White)
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.strava_connected_as, athleteName),
                    style = AppTheme.typography.semiBold.copy(
                        color = AppColors.DarkCharcoal,
                        fontSize = 16.sp,
                    ),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
