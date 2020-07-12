package me.edujtm.tuyo.repository.http

interface VideoHttpApi {
    suspend fun getVideoInfo(): RequestState<List<String>>
}