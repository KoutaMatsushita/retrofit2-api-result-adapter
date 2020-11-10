package dev.mk2481.retrofit2.adapter.apiresult

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

internal class ResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<ApiResult<Response<T>>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<ApiResult<Response<T>>> {
        val result = MutableStateFlow<ApiResult<Response<T>>>(InProgress)

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                result.value = Failure(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                result.value = Success(response)
            }
        })

        return result
    }
}