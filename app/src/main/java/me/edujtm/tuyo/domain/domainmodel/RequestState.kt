package me.edujtm.tuyo.domain.domainmodel

sealed class RequestState<out T> {
    object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T): RequestState<T>()
    data class Failure(val error: Throwable) : RequestState<Nothing>() {
        companion object {
            fun withMessage(message: String) : Failure {
                val error = Throwable(message)
                return Failure(
                    error
                )
            }
        }
    }

    fun <R> onSuccessMap(transform: (T) -> R) : RequestState<R> {
        return when (this) {
            is Success -> Success(transform(this.data))
            is Failure -> Failure(this.error)
            is Loading -> Loading
        }
    }
}