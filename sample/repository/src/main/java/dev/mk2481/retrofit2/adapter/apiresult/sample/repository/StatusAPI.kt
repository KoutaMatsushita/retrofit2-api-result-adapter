package dev.mk2481.retrofit2.adapter.apiresult.sample.repository

import dev.mk2481.retrofit2.adapter.apiresult.ApiResult
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

interface StatusAPI {
    @Headers("Accept: application/json")
    @GET("/{status}")
    fun get(
        @Path("status") status: Int,
        @Query("sleep") sleep: Int
    ): Flow<ApiResult<StatusJSON>>

    @Headers("Accept: application/json")
    @POST("/{status}")
    fun post(
        @Path("status") status: Int,
        @Query("sleep") sleep: Int
    ): Flow<ApiResult<StatusJSON>>

    @Headers("Accept: application/json")
    @PUT("/{status}")
    fun put(
        @Path("status") status: Int,
        @Query("sleep") sleep: Int
    ): Flow<ApiResult<StatusJSON>>

    @Headers("Accept: application/json")
    @DELETE("/{status}")
    fun delete(
        @Path("status") status: Int,
        @Query("sleep") sleep: Int
    ): Flow<ApiResult<StatusJSON>>
}