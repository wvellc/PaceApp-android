package net.paceapp.features.main.tabhost.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.paceapp.R
import net.paceapp.features.main.analytics.navigation.AnalyticsRoute
import net.paceapp.features.main.history.navigation.HistoryRoute
import net.paceapp.features.main.home.navigation.HomeRoute
import net.paceapp.features.main.profile.navigation.ProfileRoute
import kotlin.reflect.KClass

sealed class BottomTab<T : Any>(
    val route: T,
    val routeClass: KClass<T>,
    @param:StringRes val titleResId: Int,
    @param:DrawableRes val iconResId: Int,
    @param:DrawableRes val selectedIconResId: Int
) {
    data object Home : BottomTab<HomeRoute>(
        route = HomeRoute,
        routeClass = HomeRoute::class,
        titleResId = R.string.home,
        iconResId = R.drawable.tab_home,
        selectedIconResId = R.drawable.tab_home_selected
    )

    data object History : BottomTab<HistoryRoute>(
        route = HistoryRoute,
        routeClass = HistoryRoute::class,
        titleResId = R.string.history,
        iconResId = R.drawable.tab_history,
        selectedIconResId = R.drawable.tab_history_selected
    )

    data object Analytics : BottomTab<AnalyticsRoute>(
        route = AnalyticsRoute,
        routeClass = AnalyticsRoute::class,
        titleResId = R.string.analytics,
        iconResId = R.drawable.tab_analytics,
        selectedIconResId = R.drawable.tab_analytics_selected
    )

    data object Profile : BottomTab<ProfileRoute>(
        route = ProfileRoute,
        routeClass = ProfileRoute::class,
        titleResId = R.string.tab_profile,
        iconResId = R.drawable.tab_profile,
        selectedIconResId = R.drawable.tab_profile_selected
    )
}