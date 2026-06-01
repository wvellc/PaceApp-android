plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Required for parsing API responses in the app
    alias(libs.plugins.kotlin.serialization)

    // Hilt DI Plugins
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("keyStore\\paceapp_key")
            storePassword = "PaceApp!@#246"
            keyPassword = "PaceApp!@#246"
            keyAlias = "PaceApp"
        }
    }
    namespace = "net.paceapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "net.paceapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // ---------------------------------------------------------
    // STANDARD ANDROID & COMPOSE (Your existing setup)
    // ---------------------------------------------------------
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling)

    //Kotlin Date time extension
    implementation(libs.kotlinx.datetime)


    // ---------------------------------------------------------
    // SPLASH SCREEN
    // ---------------------------------------------------------
    implementation(libs.androidx.core.splashscreen)

    // ---------------------------------------------------------
    // NAVIGATION (For AppNavHost)
    // ---------------------------------------------------------
    implementation(libs.androidx.navigation.compose)

    // ---------------------------------------------------------
    // DEPENDENCY INJECTION (Hilt)
    // ---------------------------------------------------------
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    // Fallback workaround if Hilt throws a metadata version exception
    implementation(libs.kotlin.metadata.jvm)

    // ---------------------------------------------------------
    // NETWORK & DATA (Your existing setup)
    // ---------------------------------------------------------
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)

    // DataStore (Needed for AppSessionManager)
    implementation(libs.androidx.datastore.preferences)

    // Coil - Image loading & Caching
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // ---------------------------------------------------------
    // GARMIN CONNECT
    // ---------------------------------------------------------
    implementation(libs.ciq.companion.app.sdk)

    // ---------------------------------------------------------
    // CAPSULE - For smooth rounded corner
    // ---------------------------------------------------------
    implementation(libs.capsule)

    // ---------------------------------------------------------
    // COUNTRY CODE PICKER
    // ---------------------------------------------------------
    implementation(libs.country.picker)

    // ---------------------------------------------------------
    // GLASS BACKDROP UI
    // ---------------------------------------------------------
    implementation(libs.backdrop)

    // ---------------------------------------------------------
    // Image cropper
    // ---------------------------------------------------------
    implementation(libs.ucrop)

    // ---------------------------------------------------------
    // Vico Charts
    // ---------------------------------------------------------
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    // ---------------------------------------------------------
    // Google Maps
    // ---------------------------------------------------------
    implementation(libs.google.maps.compose)
    implementation(libs.google.play.services.maps)
    implementation(libs.android.maps.utils)

    // ---------------------------------------------------------
    // LOCAL MODULES (The Core Engine)
    // ---------------------------------------------------------
    implementation(project(":core-network"))
    implementation(project(":core-ui"))
}
secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
}