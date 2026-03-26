package com.example.paceapp.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object Splash : Route()
}