package me.edujtm.tuyo.data.endpoint

import com.google.api.services.youtube.model.PlaylistItemListResponse

interface PlaylistEndpoint {
    fun getPlaylistById(id: String) : PlaylistItemListResponse
}