package com.wvelabs.core_network.di

import com.wvelabs.core_network.timer.TimerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimerModule {
    @Provides
    @Singleton
    fun provideTimerFactory(): TimerFactory = TimerFactory()
}