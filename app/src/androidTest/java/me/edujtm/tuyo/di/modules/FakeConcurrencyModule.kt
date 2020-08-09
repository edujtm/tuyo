package me.edujtm.tuyo.di.modules

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import me.edujtm.tuyo.domain.DispatcherProvider

@Module
object FakeConcurrencyModule {

    // Not sure which dispatcher to use on instrumentation tests
    @JvmStatic
    @Provides
    fun provideDispatcherProvider() : DispatcherProvider {
        return object : DispatcherProvider {
            override val computation = Dispatchers.Default
            override val io = Dispatchers.IO
            override val main = Dispatchers.Main
        }
    }
}