package com.wvelabs.core_network.interceptors

import com.wvelabs.core_network.utils.AppLogger
import okhttp3.logging.HttpLoggingInterceptor

class ApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        AppLogger.i(message)
    }
}