package net.paceapp.features.main.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.capsule.ContinuousRoundedRectangle
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.AppButton
import net.paceapp.core.components.AppSegmentedButtons
import net.paceapp.core.components.AppTextButton
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.enums.DistanceUnits
import net.paceapp.core.extensions.g2Continuity
import net.paceapp.features.main.settings.SettingsContract.Event
import net.paceapp.features.main.settings.SettingsContract.State
import net.paceapp.features.main.settings.enums.SettingOptions
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme

@Composable
internal fun SettingsContent(
    state: State,
    onEvent: (Event) -> Unit
) {
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            //App bar
            CommonAppBar(
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                }
            )
        }
    ) { innerPaddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddings)
                .padding(horizontal = AppTheme.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp)
            ) {

                // HEADER
                item(key = "setting_distance_tabs") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = AppColors.White)
                            .padding(8.dp)
                    ) {
                        AppSegmentedButtons(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ContinuousRoundedRectangle(12.dp, continuity = g2Continuity))
                                .background(AppColors.HintGray)
                                .padding(4.dp),
                            segments = DistanceUnits.entries,
                            unselectedTextColor = AppColors.DarkCharcoal,
                            selectedTextColor = AppColors.White,
                            selectedSegment = state.selectedDistanceUnits,
                            itemTitle = { stringResource(it.titleRes) },
                            onSegmentSelected = { onEvent(Event.OnDistanceUnitSelected(it)) },
                        )
                    }
                }

                // LIST OPTIONS — Notifications is hidden for now (parity with iOS,
                // whose notifications entry is commented out).
                items(
                    items = SettingOptions.entries.filter { it != SettingOptions.NOTIFICATIONS },
                    key = { option -> option.name }
                ) { option ->
                    SettingOptionItem(
                        option = option,
                        onClick = { onEvent(Event.OnSettingOptionClick(option)) },
                        onDevButtonTap = { onEvent(Event.OnDeveloperWebsiteClick) },
                        isDevOptionExpanded = state.isDevOptionExpanded,
                    )
                }
            }

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.logout),
            ) { onEvent(Event.OnLogoutClick) }

            AppTextButton(
                text = stringResource(R.string.delete_account),
                style = AppTheme.typography.semiBold.copy(
                    fontSize = 14.sp,
                    color = AppColors.FashionGray,
                ),
                onClick = { onEvent(Event.OnDeleteAccountClick) },
            )
        }
    }
}
