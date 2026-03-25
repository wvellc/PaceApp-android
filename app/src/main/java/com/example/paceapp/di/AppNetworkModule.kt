package com.example.paceapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

//Building Retrofit for the App
@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient // Hilt passes in your secure client from :core-network!
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json { 
            ignoreUnknownKeys = true // Prevents crashes if the backend adds new fields
            coerceInputValues = true // Safely handles nulls for default values
        }

        return Retrofit.Builder()
            // We will eventually move this URL into your AppEnvironment setup
            .baseUrl("https://api.your-staging-server.com/") 
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
}