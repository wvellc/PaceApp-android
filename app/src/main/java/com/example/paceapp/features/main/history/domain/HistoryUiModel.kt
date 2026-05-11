package com.example.paceapp.features.main.history.domain

data class HistoryUiModel(
    val id: String,
    val title: String,
    val date: String,
    val distance: String,
    val time: String,
    val avgPace: String,
    val paceDifference: String,
    val isPaceImproved: Boolean
)