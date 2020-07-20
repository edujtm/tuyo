package me.edujtm.tuyo.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import me.edujtm.tuyo.data.model.PlaylistItem
import me.edujtm.tuyo.domain.domainmodel.RequestState


interface PlaylistRepository {
    fun getLikedVideos(): Flow<PagingData<PlaylistItem>>
}