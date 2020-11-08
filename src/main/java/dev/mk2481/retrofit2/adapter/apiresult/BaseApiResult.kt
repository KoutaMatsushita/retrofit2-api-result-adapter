package dev.mk2481.retrofit2.adapter.apiresult

class BaseApiResult<SUCCESS, ERROR : Throwable>(
    defaultState: ApiResultState<SUCCESS, ERROR> = Initialize
) {
    val isLoading: Boolean
        get() = state is InProgress

    private var state: ApiResultState<SUCCESS, ERROR> = defaultState

    /**
     * 結果を取得します
     *
     * @param success 成功時のコールバック
     * @param failure 失敗時のコールバック
     */
    fun fold(
        success: ((SUCCESS) -> Unit)? = null,
        failure: ((ERROR) -> Unit)? = null,
    ) {
        when (val s = state) {
            is Success -> success?.invoke(s.result)
            is Failure -> failure?.invoke(s.error)
        }
    }

    /**
     * レスポンスを変換します
     *
     * @param R 変換後のレスポンスクラス
     * @param func 変換関数
     * @return 変換後の [BaseApiResult]
     */
    fun <R> transformSuccess(func: (SUCCESS) -> R): BaseApiResult<R, ERROR> {
        return when (val s = state) {
            Initialize -> BaseApiResult(Initialize)
            InProgress -> BaseApiResult(InProgress)
            is Success -> BaseApiResult(Success(func(s.result)))
            is Failure -> BaseApiResult(s)
        }
    }

    /**
     * エラーを変換します
     *
     * @param R 変換後のエラークラス
     * @param func 変換関数
     * @return 変換後の [BaseApiResult]
     */
    fun <R: Throwable> transformFailure(func: (ERROR) -> R): BaseApiResult<SUCCESS, R> {
        return when (val s = state) {
            Initialize -> BaseApiResult(Initialize)
            InProgress -> BaseApiResult(InProgress)
            is Success -> BaseApiResult((s))
            is Failure -> BaseApiResult(Failure(func(s.error)))
        }
    }

    internal fun toInitialize() {
        state = Initialize
    }

    internal fun toInProgress() {
        state = InProgress
    }

    internal fun toSuccess(response: SUCCESS) {
        state = Success(response)
    }

    internal fun toFailure(error: ERROR) {
        state = Failure(error)
    }
}

typealias ApiResult<SUCCESS> = BaseApiResult<SUCCESS, Throwable>

sealed class ApiResultState<out SUCCESS, out ERROR : Throwable>
object Initialize : ApiResultState<Nothing, Nothing>()
object InProgress : ApiResultState<Nothing, Nothing>()
data class Success<SUCCESS>(val result: SUCCESS) : ApiResultState<SUCCESS, Nothing>()
data class Failure<ERROR : Throwable>(val error: ERROR) : ApiResultState<Nothing, ERROR>()
