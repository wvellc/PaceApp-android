package net.paceapp.features.main.strava.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppTextButton
import net.paceapp.core.components.CommonAppBar
import net.paceapp.features.main.strava.StravaConnectContract.Event
import net.paceapp.features.main.strava.StravaConnectContract.State
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

// Connect / disconnect the user's Strava account + sync recent activities. Mirrors iOS
// StravaConnectScreen — the heavy lifting is server-side; this just drives StravaManager
// (via events) and reflects the connection state it observes from Firestore.
@Composable
internal fun StravaConnectContent(
    state: State,
    onEvent: (Event) -> Unit,
    context: Context,
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        // isWorking → block interaction + show the loading overlay (iOS working overlay).
        isLoading = state.isWorking,
        hasPattern = true,
        appBar = {
            CommonAppBar(
                title = stringResource(R.string.strava_title),
                showAppLogo = false,
                onBackClick = { onEvent(Event.OnBackClick) },
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(horizontal = AppTheme.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Header + connected-as card.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_strava_logo),
                        contentDescription = null,
                        modifier = Modifier.height(44.dp),
                    )
                    androidx.compose.material3.Text(
                        text = stringResource(
                            if (state.isConnected) R.string.strava_connected_title
                            else R.string.strava_disconnected_title
                        ),
                        style = AppTheme.typography.semiBold.copy(
                            color = AppColors.White,
                            fontSize = 20.sp,
                        ),
                    )
                    androidx.compose.material3.Text(
                        text = stringResource(
                            if (state.isConnected) R.string.strava_connected_subtitle
                            else R.string.strava_disconnected_subtitle
                        ),
                        style = AppTheme.typography.medium.copy(
                            color = AppColors.White.copy(alpha = 0.5f),
                            fontSize = 14.sp,
                        ),
                    )
                }

                val name = state.athleteName
                if (state.isConnected && !name.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppColors.White)
                            .padding(16.dp),
                    ) {
                        androidx.compose.material3.Text(
                            text = stringResource(R.string.strava_connected_as, name),
                            style = AppTheme.typography.semiBold.copy(
                                color = AppColors.DarkCharcoal,
                                fontSize = 16.sp,
                            ),
                        )
                    }
                }
            }

            // Footer actions.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (state.isConnected) {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.strava_sync_recent),
                    ) { onEvent(Event.OnSyncRecentClick) }

                    AppTextButton(
                        text = stringResource(R.string.strava_disconnect),
                        contentColor = AppColors.Error,
                        onClick = { onEvent(Event.OnDisconnectClick) },
                    )
                } else {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.strava_connect_button),
                    ) { onEvent(Event.OnConnectClick(context)) }
                }

                androidx.compose.material3.Text(
                    text = stringResource(R.string.strava_powered_by),
                    style = AppTheme.typography.medium.copy(
                        color = AppColors.White.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}
