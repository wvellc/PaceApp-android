package net.paceapp.di

import net.paceapp.core.data.firestore.EventRepository
import net.paceapp.core.data.firestore.FirestoreEventRepository
import net.paceapp.core.data.firestore.FirestoreUserProfileRepository
import net.paceapp.core.data.firestore.UserProfileRepository
import net.paceapp.core.data.repository.UserRepositoryImpl
import net.paceapp.core.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the interface [UserRepository] to its implementation [UserRepositoryImpl].
     *
     * @Singleton ensures that the same instance of the repository is injected
     * everywhere throughout the app's lifecycle.
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    // Firestore event store (shared thepaceapp backend, parity with iOS).
    @Binds
    @Singleton
    abstract fun bindEventRepository(
        impl: FirestoreEventRepository
    ): EventRepository

    // Firestore user profile + settings store.
    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: FirestoreUserProfileRepository
    ): UserProfileRepository
}