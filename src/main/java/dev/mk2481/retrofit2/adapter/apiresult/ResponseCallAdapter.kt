package dev.mk2481.retrofit2.adapter.apiresult

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

internal class ResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, ApiResult<Response<T>>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): ApiResult<Response<T>> {
        val result = ApiResult<Response<T>>(InProgress)

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                result.toFailure(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                result.toSuccess(response)
            }
        })

        return result
    }
}