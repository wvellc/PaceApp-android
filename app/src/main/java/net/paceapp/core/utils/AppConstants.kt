package net.paceapp.core.utils

import com.wvelabs.core_ui.alerts.AppAlerts
import com.wvelabs.core_ui.alerts.MessageType

object AppConstants {
    fun showComingSoonDialog() {
        AppAlerts.showToast("Coming Soon", type = MessageType.Info)

    }

    val defaultDigitRange: ClosedRange<Double> = 0.1..999.0
    const val WATCH_APP_UUID = "7243fd4e-7a56-485b-8a27-7eb3e43638fc"
    const val WATCH_STORE_UUID = "7243fd4e-7a56-485b-8a27-7eb3e43638fc"
    const val OTP_LENGTH = 6
    const val DUMMY_TOKEN =
        "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoyMDI2MTU2MjIyfQ"
    const val SPLASH_DELAY = 1500L
}