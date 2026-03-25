package com.example.paceapp.di

import com.wvelabs.core_network.di.DataStoreFileName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @DataStoreFileName // This tag perfectly matches the one we created in :core-network
    fun provideDataStoreFileName(): String {
        return "pace_app_secure_prefs" 
    }
}