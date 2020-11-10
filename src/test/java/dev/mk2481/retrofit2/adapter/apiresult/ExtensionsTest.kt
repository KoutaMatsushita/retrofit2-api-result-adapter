package dev.mk2481.retrofit2.adapter.apiresult

import io.mockk.confirmVerified
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class ExtensionsTest {
    @Test
    fun transformSuccess_success() = runBlocking {
        val transform: (Int) -> String = spyk({ "hello!" })
        val callback: (ApiResult<Any>) -> Unit = spyk({
            println(it)
        })

        val result = MutableStateFlow(Success(123))
            .transformSuccess(transform)


        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                result.collect {
                    callback(it)
                }
            }

            Thread.sleep(1_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        verify { transform(123) }
        verify { callback(Success("hello!")) }

        confirmVerified(callback)
    }

    @Test
    fun transformSuccess_failure() = runBlocking {
        val transform: (Int) -> String = spyk({ "hello!" })
        val callback: (ApiResult<Any>) -> Unit = spyk({
            println(it)
        })
        val exception = RuntimeException()
        val result = MutableStateFlow(Failure(exception))
            .transformSuccess(transform)

        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                result.collect {
                    callback(it)
                }
            }

            Thread.sleep(1_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        verify(exactly = 0) { transform(any()) }
        verify { callback(Failure(exception)) }

        confirmVerified(callback)
    }

    @Test
    fun transformFailure_success() = runBlocking {
        class ExpectException: Exception()
        val exception = RuntimeException()
        val expect = ExpectException()
        val transform: (RuntimeException) -> ExpectException = spyk({ expect })
        val callback: (ApiResult<Any>) -> Unit = spyk({
            println(it)
        })

        val result = MutableStateFlow(Success(123))
            .transformFailure(transform)

        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                result.collect {
                    callback(it)
                }
            }

            Thread.sleep(1_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        verify(exactly = 0) { transform(exception) }
        verify { callback(Success(123)) }

        confirmVerified(callback)
    }

    @Test
    fun transformFailure_failure() = runBlocking {
        class ExpectException: Exception()
        val exception = RuntimeException()
        val expect = ExpectException()
        val transform: (RuntimeException) -> ExpectException = spyk({ expect })
        val callback: (ApiResult<Any>) -> Unit = spyk({
            println(it)
        })

        val result = MutableStateFlow(Failure(exception))
            .transformFailure(transform)

        val ctx = TestCoroutineScope().coroutineContext
        launch(ctx) {
            val def = async {
                result.collect {
                    callback(it)
                }
            }

            Thread.sleep(1_000)
            kotlin.runCatching { def.cancel() }
            kotlin.runCatching { ctx.cancel() }
        }

        verify { transform(exception) }
        verify { callback(Failure(expect)) }

        confirmVerified(callback)
    }
}