package dev.mk2481.retrofit2.adapter.apiresult

import retrofit2.*
import java.lang.reflect.Type

internal class BodyCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, ApiResult<T>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): ApiResult<T> {
        val result = ApiResult<T>(InProgress)

        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                result.toFailure(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    result.toSuccess(response.body()!!)
                } else {
                    result.toFailure(HttpException(response))
                }
            }
        })

        return result
    }
}