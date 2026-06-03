package net.paceapp.navigation

import androidx.navigation.NavHostController
import net.paceapp.core.domain.enums.LoginTypes
import net.paceapp.core.enums.AnalyticsMetricType
import net.paceapp.features.authentication.buildprofile.navigation.BuildProfileRoute
import net.paceapp.features.authentication.login.navigation.LoginRoute
import net.paceapp.features.authentication.profilecreated.navigation.ProfileCreatedRoute
import net.paceapp.features.authentication.verifyotp.navigation.VerifyOtpRoute
import net.paceapp.features.common.webview.navigation.WebviewRoute
import net.paceapp.features.main.analyticsdetail.navigation.AnalyticsDetailRoute
import net.paceapp.features.main.createevent.navigation.CreateEventRoute
import net.paceapp.features.main.duplicateevent.navigation.DuplicateEventRoute
import net.paceapp.features.main.editevent.navigation.EditEventRoute
import net.paceapp.features.main.editprofile.navigation.EditProfileRoute
import net.paceapp.features.main.eventdetails.navigation.EventDetailsRoute
import net.paceapp.features.main.eventmap.navigation.EventMapRoute
import net.paceapp.features.main.favoriteactivities.navigation.FavoriteActivitiesRoute
import net.paceapp.features.main.managewatch.navigation.ManageWatchRoute
import net.paceapp.features.main.notifications.navigation.NotificationsRoute
import net.paceapp.features.main.settings.navigation.SettingsRoute
import net.paceapp.features.main.tabhost.navigation.TabHostRoute
import net.paceapp.features.main.updategait.navigation.UpdateGaitRoute

class AppNavActions(
    private val navController: NavHostController,
    private val onRootExit: () -> Unit
) {

    fun goBack() {
        when {
            navController.previousBackStackEntry != null -> navController.popBackStack()
            else -> onRootExit()
        }
    }

    // BACK WITH RESULT
    fun <T> goBackWithResult(key: String, result: T) {
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(key, result)

        goBack()
    }

    fun toLogin() {
        navController.navigate(LoginRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }


    fun toWebView(url: String, title: String? = null, isZoomEnabled: Boolean = false) {
        navController.navigate(
            WebviewRoute(
                url = url,
                title = title,
                isZoomEnabled = isZoomEnabled
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toVerifyOtp(loginType: LoginTypes, value: String, countryCode: String? = null) {
        navController.navigate(
            VerifyOtpRoute(
                loginType = loginType,
                emailPhoneValue = value,
                countryCode = countryCode
            )
        ) {
            launchSingleTop = true
        }
    }

    /** Feature removed */
//     fun toOtpSuccess(loginType: LoginTypes) {
//        navController.navigate(OtpSuccessRoute(loginType = loginType)) {
//            popUpTo(navController.graph.id) { inclusive = true }
//            launchSingleTop = true
//        }
//    }

    fun toBuildProfile() {
        navController.navigate(BuildProfileRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toProfileCreated() {
        navController.navigate(ProfileCreatedRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toTabHost() {
        navController.navigate(TabHostRoute) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun toSettings() {
        navController.navigate(SettingsRoute) {
            launchSingleTop = true
        }
    }

    fun toAnalyticsDetails(type: AnalyticsMetricType) {
        navController.navigate(AnalyticsDetailRoute(type = type)) {
            launchSingleTop = true
        }
    }

    fun toUpdateGait() {
        navController.navigate(UpdateGaitRoute) {
            launchSingleTop = true
        }
    }

    fun toEditProfile() {
        navController.navigate(EditProfileRoute) {
            launchSingleTop = true
        }
    }

    fun toManageWatch() {
        navController.navigate(ManageWatchRoute) {
            launchSingleTop = true
        }
    }

    fun toNotifications() {
        navController.navigate(NotificationsRoute) {
            launchSingleTop = true
        }
    }

    fun toCreateEvent() {
        navController.navigate(CreateEventRoute) {
            launchSingleTop = true
        }
    }

    fun toEventDetails(
        id: String,
        eventName: String,
        location: String,
        date: String,
    ) {
        navController.navigate(
            EventDetailsRoute(
                id = id,
                eventName = eventName,
                location = location,
                date = date
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toEditEvent(id: String, eventName: String, location: String) {
        navController.navigate(
            EditEventRoute(
                id = id,
                eventName = eventName,
                location = location
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toDuplicateEvent(id: String, eventName: String, location: String, date: String) {
        navController.navigate(
            DuplicateEventRoute(
                id = id,
                eventName = eventName,
                location = location,
                date = date
            )
        ) {
            launchSingleTop = true
        }
    }

    fun toEventMap() {
        navController.navigate(EventMapRoute) {
            launchSingleTop = true
        }
    }

    fun toFavorites() {
        navController.navigate(FavoriteActivitiesRoute) {
            launchSingleTop = true
        }
    }
}