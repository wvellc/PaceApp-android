package com.example.paceapp.features.main.createevent.extensions

import androidx.annotation.StringRes
import com.example.paceapp.R
import com.example.paceapp.features.main.createevent.enums.CreateRunStep

val CreateRunStep.titleRes: Int?
    @StringRes
    get() = when (this) {
        CreateRunStep.EventDetails -> null
        CreateRunStep.Distance -> R.string.step_distance_title
        CreateRunStep.GoalTime -> R.string.step_goal_time_title
        CreateRunStep.SegmentChoice -> R.string.step_segment_choice_title
        CreateRunStep.SegmentCount -> R.string.step_segment_count_title
        CreateRunStep.SegmentDetails -> R.string.step_segment_details_title
        CreateRunStep.LookBackIntervals -> R.string.step_look_back_title
    }

val CreateRunStep.nextButtonRes: Int
    @StringRes
    get() = when (this) {
        CreateRunStep.LookBackIntervals -> R.string.create
        else -> R.string.next
    }