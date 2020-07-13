package me.edujtm.tuyo.di.components

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.di.modules.ViewModelModule
import me.edujtm.tuyo.di.modules.YoutubeApiModule
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [YoutubeApiModule::class, ViewModelModule::class])
interface AppComponent {
    val mainViewModel: MainViewModel
    val likedVideosViewModel: LikedVideosViewModel
    val homeViewModel: HomeViewModel
    val searchViewModel: SearchViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance applicationContext: Context
        ): AppComponent
    }
}