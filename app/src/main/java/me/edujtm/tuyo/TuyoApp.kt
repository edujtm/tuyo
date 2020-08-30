package me.edujtm.tuyo

import android.app.Application
import me.edujtm.tuyo.di.components.ComponentProvider
import me.edujtm.tuyo.di.components.DaggerAppComponent
import timber.log.Timber

class TuyoApp : Application(), ComponentProvider {
    override val component by lazy {
        DaggerAppComponent
            .factory()
            .create(applicationContext = this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}