package me.edujtm.tuyo.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem


interface PlaylistRepository {
    fun getPlaylist(playlistId: String): Flow<PagingData<PlaylistItem>>
    fun getPrimaryPlaylist(primaryPlaylists: Int): Flow<PagingData<PlaylistItem>>
}