package me.edujtm.tuyo.data.endpoint

import me.edujtm.tuyo.data.PlaylistItem


interface PlaylistEndpoint {
    fun getPlaylistById(id: String) : List<PlaylistItem>
}