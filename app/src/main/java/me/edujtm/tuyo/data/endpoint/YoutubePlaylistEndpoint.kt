package me.edujtm.tuyo.data.endpoint

import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import me.edujtm.tuyo.auth.CredentialFactory
import me.edujtm.tuyo.data.PlaylistItem
import javax.inject.Inject

class YoutubePlaylistEndpoint
    @Inject constructor(val credentials: CredentialFactory) : PlaylistEndpoint {

    private val youtube: YouTube
        get() {
            val credential = credentials.currentUser()
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            return YouTube.Builder(transport, jsonFactory, credential)
                .build()
        }


    override fun getPlaylistById(id: String): List<PlaylistItem> = youtube.playlistItems()
        .list("snippet,contentDetails")
        .apply {
            maxResults = 25
            playlistId = id
        }.execute()
        .items.map { PlaylistItem.fromJson(it) }
}