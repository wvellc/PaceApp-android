package com.example.paceapp.config

import com.example.paceapp.BuildConfig
import com.wvelabs.core_network.config.NetworkEnvironment
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppEnvironmentImpl @Inject constructor() : NetworkEnvironment {


    // Define the constants (Your Flutter Environments class)
    companion object {
        private const val PROD = "prod"
        private const val DEV = "dev"
        private const val LOCAL = "local"

        // The Logic (Replaces kDebugMode switch)
        private val currentEnv = if (BuildConfig.DEBUG) LOCAL else PROD
    }

    // 4. The available configurations
    private val configs = listOf(
        EnvConfig(
            env = LOCAL,
            scheme = "http",
            host = "",
            webHost = ""
        ),
        EnvConfig(
            env = DEV,
            scheme = "http",
            host = "",
            webHost = ""
        ),
        EnvConfig(
            env = PROD,
            scheme = "https",
            host = "",
            webHost = ""
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