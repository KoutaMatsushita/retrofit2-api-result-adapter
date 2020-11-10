package dev.mk2481.retrofit2.adapter.apiresult

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = ApiResultCallAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (Flow::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Deferred return type must be parameterized as Flow<ApiResult<Foo>> or Flow<ApiResult<out Foo>>")
        }
        val responseType = getParameterUpperBound(0, returnType)
        if (responseType !is ParameterizedType) {
            throw IllegalStateException(
                "Deferred return type must be parameterized as Flow<ApiResult<Foo>> or Flow<ApiResult<out Foo>>")
        }
        val successType = getParameterUpperBound(0, responseType)

        val rawDeferredType = getRawType(successType)
        return if (rawDeferredType == Response::class.java) {
            if (successType !is ParameterizedType) {
                throw IllegalStateException(
                    "Response must be parameterized as Response<Foo> or Response<out Foo>")
            }
            ResponseCallAdapter<Any>(getParameterUpperBound(0, successType))
        } else {
            BodyCallAdapter<Any>(successType)
        }

    }
}