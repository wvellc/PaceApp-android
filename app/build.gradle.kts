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
    // LOCAL MODULES (The Core Engine)
    // ---------------------------------------------------------
    implementation(project(":core-network"))
    implementation(project(":core-ui"))


}