package me.edujtm.tuyo.repository.http

interface VideoRepository {
    suspend fun getVideoInfo(): RequestState<List<String>>
}