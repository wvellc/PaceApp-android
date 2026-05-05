package com.example.paceapp.features.main.tabhost.domain

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.paceapp.R
import com.example.paceapp.features.main.home.navigation.HomeRoute
import com.example.paceapp.features.main.tabhost.navigation.*
import kotlin.reflect.KClass

sealed class BottomTab<T : Any>(
    val route: T,
    val routeClass: KClass<T>,
//    @param:StringRes val titleResId: Int,
//    @param:DrawableRes val iconResId: Int
) {
    data object Home : BottomTab<HomeRoute>(
        route = HomeRoute,
        routeClass = HomeRoute::class,
//        titleResId = R.string.tab_home,
//        iconResId = R.drawable.ic_home // Replace with your actual drawable
    )

    data object History : BottomTab<HistoryRoute>(
        route = HistoryRoute,
        routeClass = HistoryRoute::class,
//        titleResId = R.string.tab_history,
//        iconResId = R.drawable.ic_history
    )

    data object Analytics : BottomTab<AnalyticsRoute>(
        route = AnalyticsRoute,
        routeClass = AnalyticsRoute::class,
//        titleResId = R.string.tab_analytics,
//        iconResId = R.drawable.ic_analytics
    )

    data object Profile : BottomTab<ProfileRoute>(
        route = ProfileRoute,
        routeClass = ProfileRoute::class,
//        titleResId = R.string.tab_profile,
//        iconResId = R.drawable.ic_profile
    )
}