package net.paceapp.features.main.profile.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.paceapp.R
import net.paceapp.core.components.AppBaseScreen
import net.paceapp.core.components.CommonAppBar
import net.paceapp.core.extensions.defaultClickable
import net.paceapp.features.main.profile.ProfileContract.Event
import net.paceapp.features.main.profile.ProfileContract.State
import net.paceapp.features.main.profile.enums.ProfileOptions
import net.paceapp.theme.AppColors
import net.paceapp.theme.AppTheme
import com.wvelabs.core_ui.components.InitialAvatarView

@Composable
internal fun ProfileContent(
    state: State,
    onEvent: (Event) -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infinite rotation")

    // Animate a float from 0 to 360 degrees
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20 * 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation animation"
    )
    AppBaseScreen(
        modifier = Modifier.fillMaxSize(),
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
                        //Settings
                        Image(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier
                                .clip(CircleShape)
                                .graphicsLayer {
                                    rotationZ = rotation
                                }
                                .defaultClickable {
                                    onEvent(Event.OnSettingClick)
                                }

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
                .padding(horizontal = AppTheme.screenPadding)
                // Clear the floating bottom tab bar so the last rows are reachable and
                // the content has room to scroll (matches Home/History).
                .padding(bottom = AppTheme.bottomNavBarPadding + AppTheme.screenPadding),
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
                textStyle = AppTheme.typography.bold.copy(
                    color = AppColors.RadiantBlue,
                    fontSize = 46.sp,
                )
            )
            //User info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                //Name
                Text(
                    text = state.userUiModel?.fullNameAndGender ?: "",
                    style = AppTheme.typography.bold.copy(
                        fontSize = 24.sp,
                        color = AppColors.White
                    )
                )
                Spacer(Modifier.height(2.dp))
                //Email or phone number
                Text(
                    text = state.userUiModel?.contactInfo ?: "",
                    style = AppTheme.typography.medium.copy(
                        fontSize = 13.sp,
                        color = AppColors.White.copy(alpha = 0.5f)
                    )
                )

            }
            //Edit profile
            EditProfileButton(onClick = { onEvent(Event.OnEditProfileClick) })

            ProfileOptions.entries.filter { it != ProfileOptions.STRAVA }.forEachIndexed { index, option ->

                key("${option.name}_$index") {
                    val isSwitchChecked = when (option) {
                        ProfileOptions.INTERVAL_VIBRATE -> state.isIntervalVibrateEnabled
                        ProfileOptions.INTERVAL_BEEP -> state.isIntervalBeepEnabled
                        else -> false
                    }
                    ProfileOptionItem(
                        option = option,
                        isChecked = isSwitchChecked,
                        onToggle = { isEnabled ->
                            onEvent(Event.OnToggleSwitch(option, isEnabled))
                        },
                    ) {
                        onEvent(Event.OnProfileOptionClick(option))
                    }
                }
            }
        }
    }
}
