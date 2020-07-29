package me.edujtm.tuyo.di.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import me.edujtm.tuyo.di.components.AppComponent
import me.edujtm.tuyo.di.modules.FakeAuthModule
import me.edujtm.tuyo.di.modules.FakePersistenceModule


@Component(modules = [
    FakeAuthModule::class,
    FakePersistenceModule::class,
    TestMainActivityComponent.InstallModule::class
])
interface TestAppComponent : AppComponent {
    val activityInjectorFactory: TestMainActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ) : TestAppComponent
    }
}