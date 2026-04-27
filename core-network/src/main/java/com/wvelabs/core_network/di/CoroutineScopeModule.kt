package com.wvelabs.core_network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        // Use SupervisorJob so that a failure in one timer task 
        // doesn't kill the entire application's scope.
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}

/**
 * Qualifier for Hilt to distinguish between different scopes.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope