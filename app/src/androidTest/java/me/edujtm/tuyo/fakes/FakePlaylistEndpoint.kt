package me.edujtm.tuyo.fakes

import me.edujtm.tuyo.Fake
import me.edujtm.tuyo.data.endpoint.PlaylistEndpoint
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.PagedData
import javax.inject.Inject

// TODO: make a proper implementation of the FakePlaylistEndpoint
class FakePlaylistEndpoint
    @Inject constructor() : PlaylistEndpoint {

    override fun getPlaylistById(
        id: String,
        token: String?,
        pageSize: Long
    ): PagedData<List<PlaylistItem>, String?> {
        val items = Fake.playlistItem()
            .take(pageSize.toInt())
            .toList()

        return PagedData(
            data = items,
            nextPageToken = null as String?,
            prevPageToken = null as String?
        )
    }
}