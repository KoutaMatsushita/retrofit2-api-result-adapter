package dev.mk2481.retrofit2.adapter.apiresult

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.*
import java.lang.reflect.Type

internal class BodyCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<ApiResult<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<ApiResult<T>> {
        val result = MutableStateFlow<ApiResult<T>>(InProgress)

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                result.value = Failure(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    result.value = Success(response.body()!!)
                } else {
                    result.value = Failure(HttpException(response))
                }
            }
        })

        return result.asStateFlow()
    }
}