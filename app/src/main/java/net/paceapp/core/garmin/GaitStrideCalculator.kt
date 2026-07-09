package net.paceapp.core.garmin

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// Derives gait step lengths from the user's height, and converts between the
// app's Feet/Meters display units and the watch's millimeter values.
// Verbatim Kotlin port of iOS Model/GaitStrideCalculator.swift.
object GaitStrideCalculator {

    const val WALKING_FACTOR = 0.413
    const val RUNNING_FACTOR = 0.65
    private const val FEET_PER_METER = 3.280839895
    private const val MILLIMETERS_PER_METER = 1000.0

    // height cm → (walkingStepLength, runningStepLength) in the given unit words.
    data class DerivedGait(
        val walkingStepLength: Double,
        val walkingUnit: String,
        val runningStepLength: Double,
        val runningUnit: String,
    )

    fun gait(heightCm: Double, walkingUnit: String, runningUnit: String): DerivedGait = DerivedGait(
        walkingStepLength = stepLength(heightCm, WALKING_FACTOR, walkingUnit),
        walkingUnit = walkingUnit,
        runningStepLength = stepLength(heightCm, RUNNING_FACTOR, runningUnit),
        runningUnit = runningUnit,
    )

    // step length in a unit word, 2 dp. meters = (heightCm/100)*factor; feet = meters*3.2808…
    fun stepLength(heightCm: Double, factor: Double, unit: String): Double {
        val meters = (heightCm / 100.0) * factor
        val value = if (isMeters(unit)) meters else meters * FEET_PER_METER
        return (value * 100).roundToInt() / 100.0
    }

    // stored step length + unit → whole millimeters for the watch.
    fun millimeters(stepLength: Double, unit: String): Int {
        val meters = if (isMeters(unit)) stepLength else stepLength / FEET_PER_METER
        return (meters * MILLIMETERS_PER_METER).roundToInt()
    }

    // convert a displayed value between Feet/Meters, snapped to the picker's 1-dp step, clamped 0..9.9.
    fun convert(stepLength: Double, fromUnit: String, toUnit: String): Double {
        if (isMeters(fromUnit) == isMeters(toUnit)) return stepLength
        val meters = if (isMeters(fromUnit)) stepLength else stepLength / FEET_PER_METER
        val value = if (isMeters(toUnit)) meters else meters * FEET_PER_METER
        return min(9.9, max(0.0, (value * 10).roundToInt() / 10.0))
    }

    fun isMeters(unit: String): Boolean = unit.lowercase().startsWith("m")
}
