package me.edujtm.tuyo.di

import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.auth.Auth
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    single<Auth> {
        AuthManager(context = androidContext())
    }

    viewModel {
        MainViewModel(authManager = get())
    }

    viewModel {
        LikedVideosViewModel()
    }

    viewModel {
        HomeViewModel()
    }

    viewModel {
        SearchViewModel()
    }
}