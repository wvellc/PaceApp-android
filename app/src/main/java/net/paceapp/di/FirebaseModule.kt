package net.paceapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.persistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Firebase provisioning for the shared `thepaceapp` backend (parity with iOS).
// iOS configures a large offline persistent cache in AppDelegate; we mirror that
// so Home/History read instantly and writes queue while offline.
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        val db = FirebaseFirestore.getInstance()
        // 100 MB persistent cache (iOS uses a 500 MB cache; Android SDK default
        // is 100 MB and is plenty for the event/user footprint).
        db.firestoreSettings = firestoreSettings {
            setLocalCacheSettings(persistentCacheSettings { })
        }
        return db
    }
}
