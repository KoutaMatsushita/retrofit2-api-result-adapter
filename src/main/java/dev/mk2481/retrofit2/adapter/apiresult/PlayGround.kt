package dev.mk2481.retrofit2.adapter.apiresult

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    val flow: MutableStateFlow<ApiResult<String>> = MutableStateFlow(Initialize)
    val job = GlobalScope.launch {
        flow.collect { println(it) }
    }
    Thread.sleep(5_000)
    flow.value = Success("test")
    Thread.sleep(1_000)
    job.cancel()
}