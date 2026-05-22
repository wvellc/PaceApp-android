package net.paceapp.core.extensions

import net.paceapp.core.domain.enums.GenderTypes
import net.paceapp.core.domain.models.GaitPace
import net.paceapp.core.domain.models.GaitUnit

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
fun GenderTypes.getDefaultGaits(): Pair<GaitPace, GaitPace> {
    return when (this) {
        GenderTypes.MALE -> Pair(

            GaitPace(
                value = DEFAULT_MALE_WALK,
                unit = GaitUnit.FEET
            ),
            GaitPace(
                value = DEFAULT_MALE_RUN,
                unit = GaitUnit.FEET
            )
        )

        GenderTypes.FEMALE -> Pair(
            GaitPace(
                value = DEFAULT_FEMALE_WALK,
                unit = GaitUnit.FEET
            ),
            GaitPace(
                value = DEFAULT_FEMALE_RUN,
                unit = GaitUnit.FEET
            )
        )

        else -> Pair(
            GaitPace(
                value = DEFAULT_OTHER_WALK,
                unit = GaitUnit.FEET
            ),
            GaitPace(
                value = DEFAULT_OTHER_RUN,
                unit = GaitUnit.FEET
            )
        )
    }
}