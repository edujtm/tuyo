package me.edujtm.tuyo.domain.repository

import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PrimaryPlaylist


interface PlaylistRepository<T> {
    fun getPlaylist(playlistId: String): Flow<T>
    fun getPrimaryPlaylist(primaryPlaylist: PrimaryPlaylist): Flow<T>
}