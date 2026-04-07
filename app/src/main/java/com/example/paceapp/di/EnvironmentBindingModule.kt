package com.example.paceapp.di

import com.example.paceapp.config.AppEnvironmentImpl
import com.wvelabs.core_network.config.NetworkEnvironment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EnvironmentBindingModule {

    @Binds
    @Singleton
    abstract fun bindNetworkEnvironment(
        impl: AppEnvironmentImpl
    ): NetworkEnvironment
}