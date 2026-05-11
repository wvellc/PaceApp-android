package com.example.paceapp.features.main.history.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import javax.inject.Inject

/**
 * UseCase responsible for converting raw network models into presentation-ready UI models.
 * Later, a Repository can be injected here (e.g., class GetFormattedRunHistoryUseCase @Inject constructor(private val repo: RunRepository))
 */
class ConvertRunHistoryToUiUseCase @Inject constructor() {

    operator fun invoke(networkModel: RunHistoryModel): HistoryUiModel {
        return HistoryUiModel(
            id = networkModel.id,
            title = networkModel.title,
            
            // Format date timestamp
            date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(networkModel.timestamp)),
            
            // Format distance
            distance = String.format(Locale.getDefault(), "%.2f mi", networkModel.distance),
            
            // Format duration seconds
            time = formatSecondsToTime(networkModel.durationSeconds),
            
            // Format pace
            avgPace = "${formatSecondsToTime(networkModel.avgPaceSeconds)} /mi",
            
            // Format difference
            paceDifference = formatPaceDifference(networkModel.paceDifferenceSeconds),
            
            // Derive the boolean directly from the data
            isPaceImproved = networkModel.paceDifferenceSeconds < 0 
        )
    }

    // Helper functions for formatting
    private fun formatSecondsToTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    private fun formatPaceDifference(diffSeconds: Int): String {
        val sign = if (diffSeconds >= 0) "+" else "-"
        val absoluteSeconds = diffSeconds.absoluteValue
        val minutes = absoluteSeconds / 60
        val seconds = absoluteSeconds % 60
        return String.format(Locale.getDefault(), "%s%02d:%02d", sign, minutes, seconds)
    }
}