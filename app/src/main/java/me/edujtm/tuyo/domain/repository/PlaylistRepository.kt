package me.edujtm.tuyo.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.data.model.PrimaryPlaylist


interface PlaylistRepository {
    fun getPlaylist(playlistId: String): Flow<PagingData<PlaylistItem>>
    fun getPrimaryPlaylist(primaryPlaylist: PrimaryPlaylist): Flow<PagingData<PlaylistItem>>
}