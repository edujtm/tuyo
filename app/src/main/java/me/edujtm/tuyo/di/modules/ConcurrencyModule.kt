package me.edujtm.tuyo.di.modules

import dagger.Binds
import dagger.Module
import me.edujtm.tuyo.domain.DefaultDispatcherProvider
import me.edujtm.tuyo.domain.DispatcherProvider

@Module
abstract class ConcurrencyModule {
    @Binds
    abstract fun provideDispatcherProvider(provider: DefaultDispatcherProvider): DispatcherProvider
}