package me.edujtm.tuyo.di

import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {

    viewModel {
        MainViewModel()
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