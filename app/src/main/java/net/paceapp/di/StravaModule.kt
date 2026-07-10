package net.paceapp.di

import kotlinx.serialization.json.Json
import net.paceapp.core.strava.StravaApi
import net.paceapp.core.strava.StravaConst
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// Strava talks to the app's Firebase HTTPS functions with a per-call Firebase ID token,
// so it needs its OWN client — deliberately NOT the shared core-network OkHttp blueprint,
// whose AuthInterceptor injects a session Bearer token that would collide with ours.
@Module
@InstallIn(SingletonComponent::class)
object StravaModule {

    @Provides
    @Singleton
    @Named("strava")
    fun provideStravaOkHttp(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideStravaApi(@Named("strava") client: OkHttpClient): StravaApi {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(StravaConst.FUNCTIONS_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(StravaApi::class.java)
    }
}
