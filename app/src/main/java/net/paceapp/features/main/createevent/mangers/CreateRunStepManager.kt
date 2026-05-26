package net.paceapp.features.main.createevent.mangers

import net.paceapp.features.main.createevent.enums.CreateRunStep
import net.paceapp.features.main.createevent.CreateEventContract.State
import javax.inject.Inject

class CreateRunStepManager @Inject constructor(
    private val createRunValidator: CreateRunValidator
) {
    private val stepList = CreateRunStep.entries.toTypedArray()

    fun validateStep(step: CreateRunStep, state: State): String? {
        return createRunValidator(step, state)
    }

    fun getNextStep(state: State): CreateRunStep? {
        val currentStep = state.currentStep

        // If we are on Segments and haven't reached the last one, stay on this step
        if (currentStep == CreateRunStep.SegmentDetails) {
            if (state.currentSegmentIndex < state.segmentList.lastIndex) {
                return CreateRunStep.SegmentDetails
            }
        }

        // Otherwise, go to the next enum index (or null if we are done)
        val nextIndex = currentStep.ordinal + 1
        return if (nextIndex <= stepList.lastIndex) stepList[nextIndex] else null
    }

    fun getPreviousStep(state: State): CreateRunStep? {
        val currentStep = state.currentStep

        // If we are on Segments and aren't at the first one, stay on this step
        if (currentStep == CreateRunStep.SegmentDetails) {
            if (state.currentSegmentIndex > 0) {
                return CreateRunStep.SegmentDetails 
            }
        }

        // Otherwise, go to the previous enum index (or null if we are at the start)
        val prevIndex = currentStep.ordinal - 1
        return if (prevIndex >= 0) stepList[prevIndex] else null
    }
}