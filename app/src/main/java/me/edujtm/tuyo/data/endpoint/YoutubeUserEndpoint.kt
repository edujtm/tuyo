package me.edujtm.tuyo.data.endpoint

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.data.PrimaryPlaylists
import javax.inject.Inject

class YoutubeUserEndpoint
    @Inject constructor(val credentials: CredentialFactory) : UserEndpoint {

    private val youtube: YouTube
        get() {
            val credential = credentials.currentUser()
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            return YouTube.Builder(transport, jsonFactory, credential)
                .build()
        }

    override fun getPrimaryPlaylistsIds(): PrimaryPlaylists = youtube.channels()
        .list("snippet,contentDetails,statistics")
        .apply {
            mine = true
        }.execute()
        .items
        .firstOrNull()?.let {
            val playlists = it.contentDetails.relatedPlaylists
            PrimaryPlaylists(
                likedVideos = playlists.likes,
                favorites = playlists.favorites,
                watchLater = playlists.watchLater,
                history = playlists.watchHistory
            )
        } ?: throw Exception("Couldn't retrieve primary user playlists")

}