package net.paceapp.features.main.profile.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.paceapp.R

enum class ProfileOptions(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int,
    val showSwitch: Boolean = false,
) {
    MANAGE_YOUR_WATCH(
        titleRes = R.string.manage_your_watch,
        iconRes = R.drawable.ic_manage_your_watch
    ),

    INTERVAL_VIBRATE(
        titleRes = R.string.interval_vibrate,
        iconRes = R.drawable.ic_interval_vibrate,
        showSwitch = true
    ),

    INTERVAL_BEEP(
        titleRes = R.string.interval_beep,
        iconRes = R.drawable.ic_interval_beep,
        showSwitch = true
    ),

    SET_GAIT(
        titleRes = R.string.profile_step_set_gait_title,
        iconRes = R.drawable.ic_set_gait
    ),

    STRAVA(
        titleRes = R.string.strava_connect,
        iconRes = R.drawable.ic_sync
    );
}