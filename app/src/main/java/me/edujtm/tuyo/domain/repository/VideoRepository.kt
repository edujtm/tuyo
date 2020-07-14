package me.edujtm.tuyo.domain.repository

import me.edujtm.tuyo.domain.domainmodel.RequestState

interface VideoRepository {
    suspend fun getVideoInfo(): RequestState<List<String>>
}