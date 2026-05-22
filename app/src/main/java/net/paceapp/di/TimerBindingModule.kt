package net.paceapp.di

import net.paceapp.session.AppSessionManager
import com.wvelabs.core_network.session.TimerCache
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TimerBindingModule {
    @Binds
    @Singleton
    abstract fun bindTimerCache(impl: AppSessionManager): TimerCache
}