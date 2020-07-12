package me.edujtm.tuyo.repository.http

import me.edujtm.tuyo.data.PlaylistItem


interface PlaylistHttpApi {
    suspend fun getLikedVideos(): RequestState<List<PlaylistItem>>
}