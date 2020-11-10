package dev.mk2481.retrofit2.adapter.apiresult.sample.domain

import dev.mk2481.retrofit2.adapter.apiresult.ApiResult
import kotlinx.coroutines.flow.Flow

interface StatusRepository {
    fun get(status: Int, sleep: Int): Flow<ApiResult<Status>>
    fun post(status: Int, sleep: Int): Flow<ApiResult<Status>>
    fun put(status: Int, sleep: Int): Flow<ApiResult<Status>>
    fun delete(status: Int, sleep: Int): Flow<ApiResult<Status>>
}