package me.edujtm.tuyo.data.endpoint

import me.edujtm.tuyo.data.model.PlaylistItemJson
import me.edujtm.tuyo.domain.domainmodel.PagedData
import me.edujtm.tuyo.domain.domainmodel.Token

interface PlaylistEndpoint {
    suspend fun getPlaylistById(
        id: String,
        token: Token? = null,
        pageSize: Long = 40
    ) : PagedData<PlaylistItemJson>
}