package com.example.paceapp.config

// Define the structure (Replaces the Map<String, String>)
data class EnvConfig(
    val env: String,
    val scheme: String,
    val host: String,
    val webHost: String,
    val subPath: String = "api"
) {
    val apiBaseUrl: String get() = "$scheme://$host/$subPath/"
    val webBaseUrl: String get() = "$scheme://$webHost/"
}