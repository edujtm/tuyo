package me.edujtm.tuyo.data.endpoint

import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.PagedData

interface PlaylistEndpoint {
    fun getPlaylistById(
        id: String,
        token: String? = null,
        pageSize: Long = 40
    ) : PagedData<List<PlaylistItem>, String?>
}