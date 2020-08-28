package me.edujtm.tuyo.di.component

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import me.edujtm.tuyo.di.components.AppComponent
import me.edujtm.tuyo.di.modules.FakeAuthModule
import me.edujtm.tuyo.di.modules.FakeConcurrencyModule
import me.edujtm.tuyo.di.modules.FakePersistenceModule
import me.edujtm.tuyo.di.qualifier.AppContext
import javax.inject.Singleton


@Singleton
@Component(modules = [
    FakeAuthModule::class,
    FakePersistenceModule::class,
    FakeConcurrencyModule::class,
    TestMainActivityComponent.InstallModule::class
])
interface TestAppComponent : AppComponent {

    override val mainActivityInjector: TestMainActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @AppContext
            applicationContext: Context
        ) : TestAppComponent
    }
}