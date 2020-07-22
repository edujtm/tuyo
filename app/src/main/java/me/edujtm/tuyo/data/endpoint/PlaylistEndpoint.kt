package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.model.PlaylistItemListResponse

interface PlaylistEndpoint {
    suspend fun getPlaylistById(
        id: String,
        token: String? = null,
        pageSize: Long = 40
    ) : PlaylistItemListResponse
}