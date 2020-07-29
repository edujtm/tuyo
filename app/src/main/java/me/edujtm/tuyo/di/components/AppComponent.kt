package me.edujtm.tuyo.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import me.edujtm.tuyo.di.modules.AuthModule
import me.edujtm.tuyo.di.modules.PersistenceModule
import me.edujtm.tuyo.ui.login.LoginActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AuthModule::class,
    PersistenceModule::class,
    MainActivityComponent.InstallModule::class
])
interface AppComponent {

    val mainActivityInjector: MainActivityComponent.Factory

    fun inject(activity: LoginActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ): AppComponent
    }
}