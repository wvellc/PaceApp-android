package com.example.paceapp.features.main.home.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AnimatedBellIcon
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.core.garmin.enums.WatchConnectionState
import com.example.paceapp.features.main.home.HomeContract.Event
import com.example.paceapp.features.main.home.HomeContract.State
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.capsule.ContinuousRoundedRectangle
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun HomeContent(
    state: State,
    onEvent: (Event) -> Unit,
) {
    val isWatchConnected = state.watchModel?.status == WatchConnectionState.CONNECTED
    val metricsBackdrop = rememberLayerBackdrop()
    val animatedColor = rememberPulsingColor(
        initialColor = AppColors.FluorescentMint,
        targetColor = AppColors.InfernoRed,
        holdDuration = 5.minutes,
        transitionDuration = 600.milliseconds
    )

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        backgroundModifier = Modifier.layerBackdrop(metricsBackdrop),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                showBackButton = false,
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    //Notifications
                    AnimatedBellIcon {
                        onEvent(Event.OnNotificationClick)
                    }
                }
            )
        }
    ) { innerPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(innerPaddings)
                .padding(AppTheme.screenPadding)
                .padding(bottom = AppTheme.bottomNavBarPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                //Greeting message
                Text(
                    text = stringResource(
                        R.string.greetings_user,
                        state.userUiModel?.firstName ?: ""
                    ),
                    style = AppTheme.typography.size24.copy(
                        color = AppColors.White,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )
                //Watch status
                Text(
                    text = when {
                        isWatchConnected -> state.lastSyncDate
                        else -> stringResource(R.string.not_synced_yet)
                    },
                    style = AppTheme.typography.size12.copy(
                        fontSize = 13.sp,
                        color = AppColors.White.copy(alpha = 0.5f),
                        lineHeight = 13.sp,
                        fontWeight = FontWeight.Medium,
                    )
                )
            }

            //Watch metrics buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                state.metrics.forEach { item ->
                    WatchMetricsButton(
                        backdrop = metricsBackdrop,
                        metric = item,
                        tintColor = animatedColor.value,
                        onClick = {},
                    )
                }
            }
            //Event and favorite buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                //New Event
                HomeSquareButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_new_event,
                    titleRes = R.string.new_event,
                    onClick = {}
                )
                //Favorites
                HomeSquareButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_favorites,
                    titleRes = R.string.favorites,
                    onClick = {}
                )
            }
        }


    }
}

@Composable
fun HomeSquareButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.ic_new_event,
    @StringRes titleRes: Int = R.string.new_event,
    iconSize: Dp = 70.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(ContinuousRoundedRectangle(16.dp))
            .background(AppColors.White)
            .defaultClickable(
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(R.drawable.ic_run_faded),
            contentDescription = null,
            modifier = Modifier
                .heightIn(105.dp)
                .wrapContentWidth()
                .align(Alignment.BottomEnd)
                .offset(y = 12.dp, x = (-3).dp)
                .alpha(0.8f)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Image(
                painter = painterResource(iconRes),
                contentDescription = stringResource(titleRes),
                modifier = Modifier.size(iconSize)
            )

            Text(
                text = stringResource(titleRes),
                style = AppTheme.typography.size12.copy(
                    fontSize = 17.sp,
                    color = AppColors.DarkCharcoal,
                    lineHeight = 17.sp,
                    letterSpacing = 0.34.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            )
        }

    }
}



