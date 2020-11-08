package dev.mk2481.retrofit2.adapter.apiresult

import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.reflect.javaType
import kotlin.reflect.typeOf

@ExperimentalStdlibApi
class ApiResultCallAdapterFactoryTest {
    private lateinit var retrofit: Retrofit

    private val factory = ApiResultCallAdapterFactory()

    @Rule
    @JvmField
    val server = MockWebServer()

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
                typeOf<ApiResult<String>>().javaType,
                emptyArray(),
                retrofit
            )!!.responseType()
        ).isEqualTo(String::class.java)

        assertThat(
            factory.get(
                typeOf<ApiResult<List<String>>>().javaType,
                emptyArray(),
                retrofit
            )!!.responseType()
        ).isEqualTo(typeOf<List<String>>().javaType)

        assertThat(
            factory.get(
                typeOf<ApiResult<Response<String>>>().javaType,
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
}