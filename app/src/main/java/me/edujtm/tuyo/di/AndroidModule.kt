package me.edujtm.tuyo.di

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.auth.Auth
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.repository.http.PlaylistHttpApi
import me.edujtm.tuyo.repository.http.VideoHttpApi
import me.edujtm.tuyo.repository.http.YoutubePlaylistApi
import me.edujtm.tuyo.repository.http.YoutubeVideoHttpApi
import me.edujtm.tuyo.ui.likedvideos.LikedVideosViewModel
import me.edujtm.tuyo.ui.home.HomeViewModel
import me.edujtm.tuyo.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val SCOPES = listOf(YouTubeScopes.YOUTUBE_READONLY)

val androidModule = module {
    single<Auth> {
        AuthManager(context = androidContext())
    }

    single<VideoHttpApi> {
        YoutubeVideoHttpApi()
    }

    single<PlaylistHttpApi> {
        YoutubePlaylistApi()
    }

    factory {
        val accountManager = get<Auth>()
        GoogleAccountCredential.usingOAuth2(androidContext(), SCOPES)
            .apply {
                backOff = ExponentialBackOff()
                selectedAccountName = accountManager.getUserAccount()?.email
            }
    }

    factory {
        val credentials = get<GoogleAccountCredential>()
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        YouTube.Builder(transport, jsonFactory, credentials)
            .build()
    }

    viewModel {
        MainViewModel(authManager = get())
    }

    viewModel {
        LikedVideosViewModel(playlistApi = get())
    }

    viewModel {
        HomeViewModel()
    }

    viewModel {
        SearchViewModel()
    }
}