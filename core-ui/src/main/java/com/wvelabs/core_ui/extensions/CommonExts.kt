package com.wvelabs.core_ui.extensions

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.util.Locale
import java.util.concurrent.TimeUnit


fun Context.showToast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    toast.show()

}

fun Long.convertMillisToMinSec(): String = String.format(
    locale = Locale.getDefault(),
    format = "%02d:%02d",
    TimeUnit.MILLISECONDS.toMinutes(this),
    TimeUnit.MILLISECONDS.toSeconds(this) -
            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
)

fun Int.convertSecondsToMinSec() = (this * 1000L).convertMillisToMinSec()

fun <T> MutableList<T>.assignAll(elements: Collection<T>) {
    clear()
    addAll(elements)
}

fun Context.openNotificationSettings() {
    val intent = Intent()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    } else {
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", packageName)
    }
    startActivity(intent)
}


