package dev.mk2481.retrofit2.adapter.apiresult

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

fun <SUCCESS, ERROR : Throwable, R> Flow<BaseApiResult<SUCCESS, ERROR>>.transformSuccess(func: (SUCCESS) -> R) =
    transform { value ->
        return@transform emit(value.transformSuccess(func))
    }

fun <SUCCESS, ERROR : Throwable, R: Throwable> Flow<BaseApiResult<SUCCESS, ERROR>>.transformFailure(func: (ERROR) -> R) =
    transform { value ->
        return@transform emit(value.transformFailure(func))
    }