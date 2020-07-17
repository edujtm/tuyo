package me.edujtm.tuyo.di.components

import dagger.BindsInstance
import dagger.Module
import dagger.Subcomponent
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.di.modules.YoutubeApiModule
import me.edujtm.tuyo.di.qualifier.UserEmail
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel

@Subcomponent(modules = [
    YoutubeApiModule::class
])
interface MainActivityComponent {
    val searchViewModel: SearchViewModel
    val homeViewModel: HomeViewModel
    val likedVideosViewModel: LikedVideosViewModel
    val mainViewModel: MainViewModel

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance
            @UserEmail
            userEmail: String
        ) : MainActivityComponent
    }

    @Module(subcomponents = [MainActivityComponent::class])
    interface InstallModule
}