package net.paceapp.features.authentication.buildprofile.models

import androidx.annotation.StringRes
import net.paceapp.R

sealed class ProfileStep(
    @param:StringRes val titleRes: Int,
    @param:StringRes val buttonLabelRes: Int,
    val isSkippable: Boolean = false,
    val stepOrder: Int
) {
    abstract val nextStep: ProfileStep?
    abstract val previousStep: ProfileStep?
    open val skipStep: ProfileStep? = null

    data object AccountSetup : ProfileStep(
        titleRes = R.string.profile_step_account_setup_title,
        buttonLabelRes = R.string.next,
        stepOrder = 1
    ) {
        override val nextStep get() = PairWatchInit
        override val previousStep get() = null
    }

    data object PairWatchInit : ProfileStep(
        titleRes = R.string.profile_step_pair_watch_title,
        buttonLabelRes = R.string.profile_step_start_pairing,
        isSkippable = true,
        stepOrder = 2
    ) {
        override val nextStep get() = SelectModel
        override val previousStep get() = AccountSetup
        override val skipStep get() = SetGait
    }

    data object SelectModel : ProfileStep(
        titleRes = R.string.profile_step_select_model_title,
        buttonLabelRes = R.string.profile_step_pair,
        stepOrder = 3
    ) {
        override val nextStep get() = PairWatchSuccess
        override val previousStep get() = PairWatchInit
    }

    data object PairWatchSuccess : ProfileStep(
        titleRes = R.string.profile_step_pair_watch_title,
        buttonLabelRes = R.string.next,
        stepOrder = 4
    ) {
        override val nextStep get() = SetGait
        override val previousStep get() = PairWatchInit
    }

    data object SetGait : ProfileStep(
        titleRes = R.string.profile_step_set_gait_title,
        buttonLabelRes = R.string.next,
        stepOrder = 5
    ) {
        // Set Gait → Connect Strava → finish (ConnectStrava.nextStep=null finishes onboarding).
        override val nextStep get() = ConnectStrava
        override val previousStep get() = PairWatchInit // The UX jump backwards
    }

    data object ConnectStrava : ProfileStep(
        titleRes = R.string.profile_step_connect_strava_title,
        buttonLabelRes = R.string.profile_step_connect,
        isSkippable = true,
        stepOrder = 6
    ) {
        override val nextStep get() = null
        override val previousStep get() = SetGait
    }
}