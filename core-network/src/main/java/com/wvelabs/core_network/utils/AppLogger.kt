package com.wvelabs.core_network.utils

import android.util.Log
import com.wvelabs.core_network.BuildConfig

object AppLogger {
    private const val TAG = "PaceApp_Debug"

    // 💡 DEBUG: General development logs
    fun d(message: String) {
        if (BuildConfig.DEBUG) Log.d(TAG, "💡 $message")
    }

    // ℹ️ INFO: Key milestones (App startup, Config loaded)
    fun i(message: String) {
//        if (BuildConfig.DEBUG) Log.i(TAG, "ℹ️ $message")
    }

    // ⚠️ WARNING: Non-fatal issues (Slow UI, missing optional data)
    fun w(message: String) {
//        if (BuildConfig.DEBUG) Log.w(TAG, "⚠️ $message")
    }

    // ❌ ERROR: Fatal crashes or failed API calls
    fun e(message: String, throwable: Throwable? = null) {
//        if (BuildConfig.DEBUG) Log.e(TAG, "❌ $message", throwable)
    }
}