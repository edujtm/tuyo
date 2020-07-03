package me.edujtm.tuyo

import android.app.Application
import me.edujtm.tuyo.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class TuyoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TuyoApp)
            modules(androidModule)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}