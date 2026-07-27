package net.paceapp.core.extensions


import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.wvelabs.core_network.utils.AppLogger

/**
 * Extension to open any URL in the external system browser.
 */
fun Context.openBrowser(url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    } catch (e: Exception) {
        AppLogger.e("No web browser found : $e")
    }
}

/**
 * Opens a URL in an in-app Custom Tab (Android's analog of iOS SFSafariViewController) —
 * used for the FAQ/Help pages. Falls back to the external browser if no Custom Tabs
 * provider is available.
 */
fun Context.openCustomTab(url: String) {
    try {
        CustomTabsIntent.Builder().build().apply {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.launchUrl(this, url.toUri())
    } catch (e: Exception) {
        AppLogger.e("Custom Tab failed, falling back to browser : $e")
        openBrowser(url)
    }
}

/**
 * Extension to navigate directly to this App's specific
 * System Notification Settings.
 */
fun Context.openAppNotificationSettings() {
    val intent = Intent().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }

//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
//                action = "android.settings.APP_NOTIFICATION_SETTINGS"
//                putExtra("app_package", packageName)
//                putExtra("app_uid", applicationInfo.uid)
//            }

            else -> {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = "package:$packageName".toUri()
            }
        }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

/**
 * Extension to open general Application Details (Permissions, Storage, etc.)
 */
fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = "package:$packageName".toUri()
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}