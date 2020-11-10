package dev.mk2481.retrofit2.adapter.apiresult

import io.mockk.*
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.MediaType
import okhttp3.ResponseBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.TimeUnit
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@ExperimentalStdlibApi
class ApiResultCallAdapterFactoryTest {
    private lateinit var retrofit: Retrofit

    private val factory = ApiResultCallAdapterFactory()

    interface TestAPI {
        @GET("/test")
        fun test(): Flow<ApiResult<String>>

        @GET("/bad_request")
        fun badRequest(): Flow<ApiResult<String>>

        @GET("/not_found")
        fun notFound(): Flow<ApiResult<String>>
    }

    @Rule
    @JvmField
    val server = MockWebServer().apply {
        dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {
                    "/test" -> MockResponse()
                        .setBodyDelay(2, TimeUnit.SECONDS)
                        .setBody("hello!")
                        .setResponseCode(200)
                    "/bad_request" -> MockResponse()
                        .setBodyDelay(2, TimeUnit.SECONDS)
                        .setBody("hello!")
                        .setResponseCode(400)
                    else -> MockResponse()
                        .setBodyDelay(2, TimeUnit.SECONDS)
                        .setBody("hello!")
                        .setResponseCode(404)
                }
            }

        }
    }

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(StringConverterFactory())
            .addCallAdapterFactory(factory)
            .build()
    }

    @Test
    fun responseType() {
        assertThat(
            factory.get(
                typeOf<Flow<ApiResult<String>>>().javaType,
                emptyArray(),
                retrofit
            )!!.responseType()
        ).isEqualTo(String::class.java)

        assertThat(
            factory.get(
                typeOf<Flow<ApiResult<List<String>>>>().javaType,
                emptyArray(),
                retrofit
            )!!.responseType()
        ).isEqualTo(typeOf<List<String>>().javaType)

        assertThat(
            factory.get(
                typeOf<Flow<ApiResult<Response<String>>>>().javaType,
                emptyArray(),
                retrofit
            )!!.responseType()
        ).isEqualTo(String::class.java)
    }

    @Test
    fun nonListenableFutureReturnsNull() {
        val adapter = factory.get(String::class.java, emptyArray(), retrofit)
        assertThat(adapter).isNull()
    }

    @Test
    fun response() = runBlocking {
        val request = retrofit
            .create(TestAPI::class.java)
            .test()
        val callback: (ApiResult<String>) -> Unit = spyk({
            println(it)
        })

        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                request.collect {
                    callback(it)
                }
            }

            Thread.sleep(3_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        assertThat((request as StateFlow).value)
            .isNotNull
            .isEqualTo(Success("hello!"))

        verify { callback(InProgress) }
        verify { callback(Success("hello!")) }

        confirmVerified(callback)
    }

    @Test
    fun response_notFound() = runBlocking {
        val request = retrofit
            .create(TestAPI::class.java)
            .badRequest()
        val callback: (ApiResult<String>) -> Unit = spyk({
            println(it)
        })

        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                request.collect {
                    callback(it)
                }
            }

            Thread.sleep(3_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        assertThat((request as StateFlow).value)
            .isNotNull
            .isInstanceOfSatisfying(
                Failure::class.java
            ) { assertThat(it.error).isInstanceOf(HttpException::class.java) }

        verify { callback(InProgress) }
        verify { callback(request.value) }

        confirmVerified(callback)
    }
}