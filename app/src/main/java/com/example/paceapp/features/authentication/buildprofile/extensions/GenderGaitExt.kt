package com.example.paceapp.features.authentication.buildprofile.extensions

import com.example.paceapp.core.domain.enums.GenderTypes

private const val DEFAULT_MALE_WALK = 2.5f
private const val DEFAULT_MALE_RUN = 4.0f
private const val DEFAULT_FEMALE_WALK = 2.2f
private const val DEFAULT_FEMALE_RUN = 3.5f


private const val DEFAULT_OTHER_WALK = 1f
private const val DEFAULT_OTHER_RUN = 1f

/**
 * Extension to resolve the default Gait PACE based on the user's gender.
 * @return A Pair containing (WalkingGait, RunningGait)
 */
fun GenderTypes.getDefaultGaits(): Pair<com.example.paceapp.features.authentication.buildprofile.domain.GaitPace, com.example.paceapp.features.authentication.buildprofile.domain.GaitPace> {
    return when (this) {
        GenderTypes.MALE -> Pair(
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_MALE_WALK,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            ),
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_MALE_RUN,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            )
        )
        GenderTypes.FEMALE -> Pair(
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_FEMALE_WALK,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            ),
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_FEMALE_RUN,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            )
        )
        else -> Pair(
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_OTHER_WALK,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            ),
            _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitPace(
                value = DEFAULT_OTHER_RUN,
                unit = _root_ide_package_.com.example.paceapp.features.authentication.buildprofile.domain.GaitUnit.FEET
            )
        )
    }
}