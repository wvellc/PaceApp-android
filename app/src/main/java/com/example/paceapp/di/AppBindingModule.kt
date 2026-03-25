package com.example.paceapp.di

import com.example.paceapp.config.AppEnvironmentImpl
import com.example.paceapp.session.AppSessionManager
import com.wvelabs.core_network.config.NetworkEnvironment
import com.wvelabs.core_network.session.SessionCache
import com.wvelabs.core_network.session.SessionListener
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//Binding the Interfaces
@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindingModule {

    @Binds
    @Singleton
    abstract fun bindSessionCache(impl: AppSessionManager): SessionCache

    @Binds
    @Singleton
    abstract fun bindSessionListener(impl: AppSessionManager): SessionListener

    @Binds
    @Singleton
    abstract fun bindNetworkEnvironment(impl: AppEnvironmentImpl): NetworkEnvironment
}

