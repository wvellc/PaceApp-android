package net.paceapp.di

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
}