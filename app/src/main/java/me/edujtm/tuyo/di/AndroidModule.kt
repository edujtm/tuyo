package me.edujtm.tuyo.di

import me.edujtm.tuyo.ui.dashboard.DashboardViewModel
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.notifications.NotificationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {

    viewModel {
        DashboardViewModel()
    }

    viewModel {
        HomeViewModel()
    }

    viewModel {
        NotificationsViewModel()
    }
}