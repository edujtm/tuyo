package me.edujtm.tuyo.repository.http

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T): RequestState<T>()
    data class Failure(val e: Throwable) : RequestState<Nothing>()
}