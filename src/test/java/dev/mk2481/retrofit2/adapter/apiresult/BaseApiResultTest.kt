package dev.mk2481.retrofit2.adapter.apiresult

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test

class BaseApiResultTest {
    @Test
    fun isLoading() {
        assertThat(ApiResult(Initialize).isLoading)
            .isFalse
        assertThat(ApiResult(InProgress).isLoading)
            .isTrue
        assertThat(ApiResult(Success(123)).isLoading)
            .isFalse
        assertThat(ApiResult(Failure(Exception())).isLoading)
            .isFalse
    }

    @Test
    fun fold_success() {
        val callback: (Int) -> Unit = mockk {
            val self = this
            every { self.invoke(123) } returns Unit
        }
        ApiResult(Success(123)).fold(
            success = callback,
            failure = { fail("呼ばれないはず") }
        )
        verify { callback(123) }
        confirmVerified(callback)
    }

    @Test
    fun fold_failure() {
        val exception = RuntimeException()
        val callback: (RuntimeException) -> Unit = mockk {
            val self = this
            every { self.invoke(exception) } returns Unit
        }
        BaseApiResult(Failure(exception)).fold(
            success = { fail("呼ばれないはず") },
            failure = callback
        )
        verify { callback(exception) }
        confirmVerified(callback)
    }

    @Test
    fun transformSuccess() {
        val transform: (Int) -> String = mockk {
            val self = this
            every { self.invoke(123) } returns "123"
        }
        val callback: (String) -> Unit = mockk {
            val self = this
            every { self.invoke("123") } returns Unit
        }
        ApiResult(Success(123)).transformSuccess(transform)
            .fold(success = callback)
        verify { transform(123) }
        verify { callback("123") }

        val notCalledTransform: (Int) -> String = mockk {
            val self = this
            every { self.invoke(123) } returns "123"
        }

        ApiResult(Failure(RuntimeException())).transformSuccess(notCalledTransform)
        ApiResult(Initialize).transformSuccess(notCalledTransform)
        ApiResult(InProgress).transformSuccess(notCalledTransform)

        verify(exactly = 0) { notCalledTransform(any()) }

        confirmVerified(transform, notCalledTransform, callback)
    }

    @Test
    fun transformFailure() {
        class ExpectException: Throwable()
        val exception = RuntimeException()
        val expect = ExpectException()
        val transform: (Throwable) -> ExpectException = mockk {
            val self = this
            every { self.invoke(exception) } returns expect
        }
        val callback: (ExpectException) -> Unit = mockk {
            val self = this
            every { self.invoke(expect) } returns Unit
        }
        ApiResult<String>(Failure(exception)).transformFailure(transform)
            .fold(failure = callback)
        verify { transform(exception) }
        verify { callback(expect) }

        val notCalledTransform: (Throwable) -> ExpectException = mockk {
            val self = this
            every { self.invoke(exception) } returns expect
        }

        ApiResult(Success(123)).transformFailure(notCalledTransform)
        ApiResult(Initialize).transformFailure(notCalledTransform)
        ApiResult(InProgress).transformFailure(notCalledTransform)

        verify(exactly = 0) { notCalledTransform(any()) }

        confirmVerified(transform, notCalledTransform, callback)
    }
}