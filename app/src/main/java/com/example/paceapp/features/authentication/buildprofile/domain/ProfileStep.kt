package com.example.paceapp.features.authentication.buildprofile.domain

import androidx.annotation.StringRes
import com.example.paceapp.R

sealed class ProfileStep(
    @param:StringRes val titleRes: Int,
    @param:StringRes val buttonLabelRes: Int,
    val isSkippable: Boolean = false
) {
    object AccountSetup : ProfileStep(
        titleRes = R.string.profile_step_account_setup_title,
        buttonLabelRes = R.string.next
    )

    object PairWatchInit : ProfileStep(
        titleRes = R.string.profile_step_pair_watch_title,
        buttonLabelRes = R.string.profile_step_start_pairing,
        isSkippable = true
    )

    object SelectModel : ProfileStep(
        titleRes = R.string.profile_step_select_model_title,
        buttonLabelRes = R.string.profile_step_pair,
    )

    object PairSelectedWatch : ProfileStep(
        titleRes = R.string.profile_step_pair_watch_title,
        buttonLabelRes = R.string.next
    )

    object SetGait : ProfileStep(
        titleRes = R.string.profile_step_set_gait_title,
        buttonLabelRes = R.string.next
    )

    object ConnectStrava : ProfileStep(
        titleRes = R.string.profile_step_connect_strava_title,
        buttonLabelRes = R.string.profile_step_connect,
        isSkippable = true
    )
}

fun ProfileStep.getNextStep(){
//    when(this){
//
//    }
}