package me.edujtm.tuyo.di.modules

import dagger.Binds
import dagger.Module
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.fakes.FakeAuthManager

@Module
abstract class FakeAuthModule {

    @Binds
    abstract fun provideAuthManager(authManager: FakeAuthManager): AuthManager
}