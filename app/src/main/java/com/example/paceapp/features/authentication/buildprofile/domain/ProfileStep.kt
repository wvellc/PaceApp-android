package com.example.paceapp.features.authentication.buildprofile.domain

import androidx.annotation.StringRes
import com.example.paceapp.R

sealed class ProfileStep(
    @param:StringRes val titleRes: Int,
    @param:StringRes val buttonLabelRes: Int,
    val isSkippable: Boolean = false
) {

    data object AccountSetup : ProfileStep(
        R.string.profile_step_account_setup_title,
        R.string.next
    )

    data object PairWatchInit : ProfileStep(
        R.string.profile_step_pair_watch_title,
        R.string.profile_step_start_pairing,
        isSkippable = true
    )

    data object SelectModel : ProfileStep(
        R.string.profile_step_select_model_title,
        R.string.profile_step_pair,
    )

    data object PairSelectedWatch : ProfileStep(
        R.string.profile_step_pair_watch_title,
        R.string.next
    )

    data object SetGait : ProfileStep(
        R.string.profile_step_set_gait_title,
        R.string.next
    )

    data object ConnectStrava : ProfileStep(
        R.string.profile_step_connect_strava_title,
        R.string.profile_step_connect,
        isSkippable = true
    )

     val stepList = mutableListOf(
         AccountSetup,
         PairWatchInit,
         SelectModel,
         PairSelectedWatch,
         SetGait,
         ConnectStrava,
    )
    val ProfileStep.index
        get() = stepList.indexOf(this)

}


fun ProfileStep.getNextStep() {
//    when(this){
//
//    }
}