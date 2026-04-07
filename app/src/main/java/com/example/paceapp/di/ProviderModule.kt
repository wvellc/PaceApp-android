package com.example.paceapp.di

import com.example.paceapp.core.providers.AppResourceProvider
import com.wvelabs.core_ui.resources.ResourceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProviderModule {

    @Binds
    @Singleton
    abstract fun bindResourceProvider(
        appResourceProvider: AppResourceProvider
    ): ResourceProvider
}