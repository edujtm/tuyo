package me.edujtm.tuyo.domain.domainmodel

sealed class Result<out T> {
    data class Success<T>(val data: T): Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()

    companion object {
        fun <T> build(action: () -> T) : Result<T> {
            return try {
                Success(action())
            } catch (e: Exception) {
                Failure(e)
            }
        }
    }
}
