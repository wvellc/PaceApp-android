package net.paceapp.features.main.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppButton
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Inline Strava card for the Settings screen — mirrors iOS SettingScreen.stravaSection.
// Shows the Strava logo, a status line (Connected as {name} / Connected / Not connected),
// and inline Resync/Disconnect pills when connected, or a Connect button when not.
@Composable
internal fun StravaSettingsCard(
    isConnected: Boolean,
    athleteName: String?,
    isWorking: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onResync: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppColors.White)
            // Dim + block actions while a Strava request is in flight (iOS opacity 0.6).
            .alpha(if (isWorking) 0.6f else 1f)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_strava_logo),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(R.string.strava_title),
                    style = AppTheme.typography.semiBold.copy(
                        color = AppColors.DarkCharcoal,
                        fontSize = 16.sp,
                    ),
                )
                Text(
                    text = when {
                        isConnected && !athleteName.isNullOrEmpty() ->
                            stringResource(R.string.strava_connected_as, athleteName)

                        isConnected -> stringResource(R.string.strava_status_connected)
                        else -> stringResource(R.string.strava_status_not_connected)
                    },
                    style = AppTheme.typography.medium.copy(
                        color = if (isConnected) AppColors.FluorescentMint else AppColors.FashionGray,
                        fontSize = 13.sp,
                    ),
                )
            }
        }

        if (isConnected) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StravaPill(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.strava_resync),
                    tint = AppColors.RadiantBlue,
                    enabled = !isWorking,
                    onClick = onResync,
                )
                StravaPill(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.strava_disconnect),
                    tint = AppColors.Error,
                    enabled = !isWorking,
                    onClick = onDisconnect,
                )
            }
        } else {
            AppButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.strava_connect),
                enabled = !isWorking,
                onClick = onConnect,
            )
        }
    }
}

// A capsule pill button: tint-tinted fill at 12% with the label in the full tint
// (mirrors iOS stravaActionButton).
@Composable
private fun StravaPill(
    title: String,
    tint: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.12f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            style = AppTheme.typography.semiBold.copy(color = tint, fontSize = 14.sp),
        )
    }
}
