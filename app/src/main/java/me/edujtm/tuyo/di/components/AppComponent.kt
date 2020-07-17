package me.edujtm.tuyo.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.di.modules.AuthModule
import me.edujtm.tuyo.di.modules.ViewModelModule
import me.edujtm.tuyo.di.modules.YoutubeApiModule
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.login.LoginActivity
import me.edujtm.tuyo.ui.search.SearchViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AuthModule::class,
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