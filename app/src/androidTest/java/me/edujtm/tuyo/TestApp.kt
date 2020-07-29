package me.edujtm.tuyo

import android.app.Application
import me.edujtm.tuyo.di.component.DaggerTestAppComponent
import me.edujtm.tuyo.di.components.AppComponent
import me.edujtm.tuyo.di.components.ComponentProvider


class TestApp : Application(), ComponentProvider {
    override val component: AppComponent by lazy {
        DaggerTestAppComponent.factory()
            .create(applicationContext = this)
    }
}