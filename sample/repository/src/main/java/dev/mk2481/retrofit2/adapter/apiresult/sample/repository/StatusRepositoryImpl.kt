package dev.mk2481.retrofit2.adapter.apiresult.sample.repository

import dev.mk2481.retrofit2.adapter.apiresult.ApiResult
import dev.mk2481.retrofit2.adapter.apiresult.sample.domain.Status
import dev.mk2481.retrofit2.adapter.apiresult.sample.domain.StatusRepository
import dev.mk2481.retrofit2.adapter.apiresult.transformSuccess
import kotlinx.coroutines.flow.Flow

class StatusRepositoryImpl constructor(
    private val api: StatusAPI
): StatusRepository {
    override fun get(status: Int, sleep: Int): Flow<ApiResult<Status>> {
        return api.get(status, sleep)
            .transformSuccess { it.toDomain() }
    }

    override fun post(status: Int, sleep: Int): Flow<ApiResult<Status>> {
        return api.post(status, sleep)
            .transformSuccess { it.toDomain() }
    }

    override fun put(status: Int, sleep: Int): Flow<ApiResult<Status>> {
        return api.put(status, sleep)
            .transformSuccess { it.toDomain() }
    }

    override fun delete(status: Int, sleep: Int): Flow<ApiResult<Status>> {
        return api.delete(status, sleep)
            .transformSuccess { it.toDomain() }
    }

    private fun StatusJSON.toDomain() = Status(code, description)
}