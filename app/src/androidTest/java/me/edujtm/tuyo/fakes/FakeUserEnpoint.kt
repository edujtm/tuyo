package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.di.scopes.PerUserSession
import me.edujtm.tuyo.domain.domainmodel.PagedData
import javax.inject.Inject

@PerUserSession
class FakeUserEnpoint
    @Inject constructor() : UserEndpoint {

    private val headers by lazy {
        Fake.Network.playlistHeader(nextPageToken = null).take(20).toList()
    }

    private val playlistIds by lazy {
        Fake.primaryPlaylistsIds().first()
    }

    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds {
        return playlistIds
    }

    // TODO: properly implement this function, so it returns a fixed set rather
    // than random ones
    override suspend fun getUserPlaylists(token: String?): PagedData<PlaylistHeaderJson> {
        return PagedData(
            data = headers,
            prevPageToken = null as String?,
            nextPageToken = null as String?
        )
    }
}