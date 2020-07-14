package me.edujtm.tuyo.domain.domainmodel

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T): RequestState<T>()
    data class Failure(val e: Throwable) : RequestState<Nothing>() {
        companion object {
            fun withMessage(message: String) : Failure {
                val error = Throwable(message)
                return Failure(
                    error
                )
            }
        }
    }
}