plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Required for parsing API responses in the app
    alias(libs.plugins.kotlin.serialization)

    // Hilt DI Plugins
    alias(libs.plugins.ksp) // ✅ ADD KSP
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.paceapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.paceapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    debugImplementation(libs.androidx.compose.ui.tooling)
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

    // This allows you to use `hiltViewModel()` inside your generated Compose screens!
    implementation(libs.androidx.hilt.navigation.compose)

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

    // ---------------------------------------------------------
    // LOCAL MODULES (The Core Engine)
    // ---------------------------------------------------------
    implementation(project(":core-network"))
    implementation(project(":core-ui"))

    // ---------------------------------------------------------
    // GARMIN CONNECT
    // ---------------------------------------------------------
    implementation(libs.ciq.companion.app.sdk)

}