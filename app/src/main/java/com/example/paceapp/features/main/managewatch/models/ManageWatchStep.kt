package com.example.paceapp.features.main.managewatch.models

import androidx.annotation.StringRes
import com.example.paceapp.R

sealed class ManageWatchStep(
    @param:StringRes val titleRes: Int,
    @param:StringRes val buttonLabelRes: Int
) {
    abstract val nextStep: ManageWatchStep?
    abstract val previousStep: ManageWatchStep?

    data object PairWatchInit : ManageWatchStep(
        titleRes = R.string.manage_your_watch,
        buttonLabelRes = R.string.profile_step_start_pairing
    ) {
        override val nextStep get() = SelectModel
        override val previousStep get() = null
    }

    data object SelectModel : ManageWatchStep(
        titleRes = R.string.profile_step_select_model_title,
        buttonLabelRes = R.string.profile_step_pair
    ) {
        override val nextStep get() = PairWatchSuccess
        override val previousStep get() = PairWatchInit
    }

    data object PairWatchSuccess : ManageWatchStep(
        titleRes = R.string.profile_step_pair_watch_title,
        buttonLabelRes = R.string.manage_watch_disconnect
    ) {
        // Note: Moving from Success -> Init (Disconnecting) is not a "next/previous" step.
        override val nextStep get() = null
        override val previousStep get() = null
    }
}