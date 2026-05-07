package com.example.paceapp.features.main.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.paceapp.R
import com.example.paceapp.core.components.AppBaseScreen
import com.example.paceapp.core.components.CommonAppBar
import com.example.paceapp.core.extensions.defaultClickable
import com.example.paceapp.features.main.profile.ProfileContract.Event
import com.example.paceapp.features.main.profile.ProfileContract.State
import com.example.paceapp.features.main.profile.domain.ProfileOptions
import com.example.paceapp.theme.AppColors
import com.example.paceapp.theme.AppTheme
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.wvelabs.core_ui.components.InitialAvatarView
import com.wvelabs.core_ui.components.LiquidToggle
import com.wvelabs.core_ui.extensions.advancedShadow

@Composable
internal fun ProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    AppBaseScreen(
        modifier = Modifier
            .fillMaxSize(),
        isLoading = state.isLoading,
        hasPattern = true,
        appBar = {
            CommonAppBar(
                showBackButton = false,
                showAppLogo = true,
                onBackClick = {
                    onEvent(Event.OnBackClick)
                },

                actions = {
                    Image(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier
                            .clip(CircleShape)
                            .defaultClickable(onClick = {
                                onEvent(Event.OnSettingClick)
                            })
                    )
                }
            )
        }
    ) { innerPaddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPaddings)
                .padding(horizontal = AppTheme.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Avtar based on initials
            InitialAvatarView(
                size = DpSize(width = 100.dp, height = 108.dp),
                modifier = Modifier.padding(top = 30.dp),
                shape = RoundedCornerShape(
                    topStart = 72.dp,
                    topEnd = 72.dp,
                    bottomEnd = 10.dp,
                    bottomStart = 10.dp
                ),
                name = state.userUiModel?.fullName ?: "",
                backgroundColor = AppColors.White,
                textStyle = AppTheme.typography.size46.copy(
                    color = AppColors.RadiantBlue,
                    fontWeight = FontWeight.Bold,
                )
            )
            //User info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //Name
                Text(
                    text = state.userUiModel?.fullNameAndGender ?: "",
                    style = AppTheme.typography.size24.copy(
                        fontWeight = FontWeight.Bold,
                        color = AppColors.White
                    )
                )
                Spacer(Modifier.height(2.dp))
                //Email or phone number
                Text(
                    text = state.userUiModel?.contactInfo ?: "",
                    style = AppTheme.typography.size24.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.White.copy(alpha = 0.5f)
                    )
                )

            }
            //Edit profile
            EditProfileButton(onClick = { onEvent(Event.OnEditProfileClick) })

            val profileOptionState = rememberLazyListState()
            ProfileOptions.entries.forEachIndexed { index, option ->
                key("${option.name}_$index") { }
                ProfileOptionItem(option) {

                }
            }
        }
    }
}

@Composable
private fun ProfileOptionItem(
    option: ProfileOptions,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .advancedShadow(
                color = AppColors.Error,
                alpha = 0.25f,
                shadowBlurRadius = 0.25f,
                offsetY = 4f
            )
            .clip(shape)
            .background(color = AppColors.White)
            .defaultClickable(onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            contentDescription = null,
            painter = painterResource(option.iconRes),
        )

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(option.titleRes),
            style = AppTheme.typography.size16.copy(
                color = AppColors.DarkCharcoal,
                fontWeight = FontWeight.SemiBold,
            )
        )
        if (option.showSwitch) {
            val (selected, onSelect) = remember { mutableStateOf(false) }
            LiquidToggle(
                selected = { selected },
                switchColor = AppColors.NeonAquaBlue,
                trackColor = AppColors.ShipGray30,
                onSelect = onSelect,
                backdrop = rememberLayerBackdrop(),
            )
        }

    }
}