package dev.mk2481.retrofit2.adapter.apiresult

sealed class BaseApiResult<out SUCCESS, out ERROR : Throwable> {
    fun<R> transformSuccess(func: (SUCCESS) -> R): BaseApiResult<R, ERROR> {
        return when(this) {
            Initialize -> Initialize
            InProgress -> InProgress
            is Success -> Success(func(this.result))
            is Failure -> this
        }
    }

    fun<R: Throwable> transformFailure(func: (ERROR) -> R): BaseApiResult<SUCCESS, R> {
        return when(this) {
            Initialize -> Initialize
            InProgress -> InProgress
            is Success -> this
            is Failure -> Failure(func(this.error))
        }
    }

    override fun toString(): String {
        return "BaseApiResult()"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseApiResult<*, *>) return false
        return javaClass == other.javaClass
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}
object Initialize : BaseApiResult<Nothing, Nothing>() {
    override fun toString(): String {
        return "Initialize()"
    }
}
object InProgress : BaseApiResult<Nothing, Nothing>() {
    override fun toString(): String {
        return "InProgress()"
    }
}
data class Success<SUCCESS>(val result: SUCCESS) : BaseApiResult<SUCCESS, Nothing>()
data class Failure<ERROR : Throwable>(val error: ERROR) : BaseApiResult<Nothing, ERROR>()

typealias ApiResult<SUCCESS> = BaseApiResult<SUCCESS, Throwable>
