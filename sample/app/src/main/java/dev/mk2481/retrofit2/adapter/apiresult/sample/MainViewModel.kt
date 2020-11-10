package dev.mk2481.retrofit2.adapter.apiresult.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mk2481.retrofit2.adapter.apiresult.ApiResult
import dev.mk2481.retrofit2.adapter.apiresult.Initialize
import dev.mk2481.retrofit2.adapter.apiresult.sample.domain.Status
import dev.mk2481.retrofit2.adapter.apiresult.sample.domain.StatusRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: StatusRepository
) : ViewModel() {
    private val mutableState = MutableLiveData<ApiResult<Status>>().apply {
        postValue(Initialize)
    }
    val state: LiveData<ApiResult<Status>> = mutableState

    val code = MutableLiveData<String>().apply { postValue("200") }
    private val codeValue: Int
        get() = code.value?.toInt() ?: 200
    val sleep = MutableLiveData<String>().apply { postValue("0") }
    private val sleepValue: Int
        get() = sleep.value?.toInt() ?: 0

    fun get() {
        viewModelScope.launch {
            repository.get(codeValue, sleepValue).collect {
                mutableState.postValue(it)
            }
        }
    }

    fun post() {
        viewModelScope.launch {
            repository.post(codeValue, sleepValue).collect {
                mutableState.postValue(it)
            }
        }
    }

    fun put() {
        viewModelScope.launch {
            repository.put(codeValue, sleepValue).collect {
                mutableState.postValue(it)
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            repository.delete(codeValue, sleepValue).collect {
                mutableState.postValue(it)
            }
        }
    }
}