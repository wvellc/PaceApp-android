package net.paceapp.di

import android.content.Context
import com.garmin.android.connectiq.ConnectIQ
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GarminModule {

    @Provides
    @Singleton
    fun provideConnectIQ(@ApplicationContext context: Context): ConnectIQ {
        // Using the constructor: getInstance(Context context, IQConnectType connectType)
        // We pass the ApplicationContext to prevent memory leaks since this is a Singleton
        return ConnectIQ.getInstance(context, ConnectIQ.IQConnectType.WIRELESS)
    }
}