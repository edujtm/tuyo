package me.edujtm.tuyo.domain.repository

import me.edujtm.tuyo.data.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.RequestState


interface PlaylistRepository {
    suspend fun getLikedVideos(): RequestState<List<PlaylistItem>>
}