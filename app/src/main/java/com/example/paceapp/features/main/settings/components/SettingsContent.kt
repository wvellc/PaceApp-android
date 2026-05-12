package com.example.paceapp.features.main.settings.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.AppButton
import com.example.paceapp.core.components.AppSegmentedButtons
import com.example.paceapp.core.components.AppTextButton
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.domain.enums.DistanceUnits
import com.example.paceapp.core.extensions.g2Continuity
import com.example.paceapp.features.main.settings.SettingsContract.Event
import com.example.paceapp.features.main.settings.SettingsContract.State
import com.example.paceapp.features.main.settings.enums.SettingOptions
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.capsule.ContinuousRoundedRectangle

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

                // LIST OPTIONS
                items(
                    items = SettingOptions.entries,
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
                style = AppTheme.typography.size14.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.FashionGray,
                ),
                onClick = { onEvent(Event.OnDeleteAccountClick) },
            )
        }
    }
}
