package com.wvelabs.core_network.interceptors

import com.wvelabs.core_network.utils.AppLogger
import okhttp3.logging.HttpLoggingInterceptor

class ApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        // We use .i (Info) for network traffic so it's easy to distinguish
        // from general .d (Debug) logic logs in your Logcat.
        AppLogger.i(message)
    }
}