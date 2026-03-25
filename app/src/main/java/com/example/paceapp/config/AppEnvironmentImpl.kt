package com.example.paceapp.config

import com.example.paceapp.BuildConfig
import com.wvelabs.core_network.config.NetworkEnvironment
import javax.inject.Inject
import javax.inject.Singleton

// 1. Define the structure (Replaces the Map<String, String>)
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

@Singleton
class AppEnvironmentImpl @Inject constructor() : NetworkEnvironment {

    // 2. Define the constants (Your Flutter Environments class)
    companion object {
        private const val PROD = "prod"
        private const val DEV = "dev"
        private const val LOCAL = "local"

        // 3. The Logic (Replaces kDebugMode switch)
        private val currentEnv = if (BuildConfig.DEBUG) LOCAL else PROD
    }

    // 4. The available configurations
    private val configs = listOf(
        EnvConfig(
            env = LOCAL,
            scheme = "http",
            host = "192.168.1.21:3008",
            webHost = "192.168.1.21"
        ),
        EnvConfig(
            env = DEV,
            scheme = "http",
            host = "54.166.214.6:3000",
            webHost = "54.166.214.6"
        ),
        EnvConfig(
            env = PROD,
            scheme = "https",
            host = "app.dinklinks.com",
            webHost = "admin.dinklinks.com"
        )
    )

    // 5. Find the active config
    private val activeConfig: EnvConfig = configs.find { it.env == currentEnv } 
        ?: configs.last() // Fallback to Prod

    // 6. Expose the clean values to the app
    override val baseUrl: String = activeConfig.apiBaseUrl
    override val webBaseUrl: String = activeConfig.webBaseUrl
    override val envName: String = activeConfig.env
}