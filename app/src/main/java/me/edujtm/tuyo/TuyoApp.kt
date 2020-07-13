package me.edujtm.tuyo

import android.app.Application
import me.edujtm.tuyo.di.components.ComponentProvider
import me.edujtm.tuyo.di.components.DaggerAppComponent

class TuyoApp : Application(), ComponentProvider {
    override val component by lazy {
        DaggerAppComponent
            .factory()
            .create(applicationContext = this)
    }
}