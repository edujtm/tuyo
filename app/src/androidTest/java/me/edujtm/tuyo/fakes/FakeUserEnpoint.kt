package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.UserEndpoint
import me.edujtm.tuyo.data.model.PlaylistHeaderJson
import me.edujtm.tuyo.data.model.PrimaryPlaylistsIds
import me.edujtm.tuyo.domain.domainmodel.PagedData
import javax.inject.Inject

class FakeUserEnpoint
    @Inject constructor() : UserEndpoint {

    private val playlistsIds by lazy {
        Fake.primaryPlaylistsIds().first()
    }

    override suspend fun getPrimaryPlaylistsIds(): PrimaryPlaylistsIds {
        return playlistsIds
    }

    // TODO: properly implement this function, so it returns a fixed set rather
    // than random ones
    override suspend fun getUserPlaylists(token: String?): PagedData<PlaylistHeaderJson> {
        val items = Fake.Network.playlistHeader(nextPageToken = null).take(20).toList()

        return PagedData(
            items,
            prevPageToken = null as String?,
            nextPageToken = null as String?
        )
    }
}