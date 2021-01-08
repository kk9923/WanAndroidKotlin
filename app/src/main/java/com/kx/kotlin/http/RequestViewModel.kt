package com.kx.kotlin.http

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kx.kotlin.bean.HttpResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by PengFeifei on 2019-12-24.
 *
 * https://developer.android.google.cn/topic/libraries/architecture/coroutines#livedata
 */
open class RequestViewModel : ViewModel() {
    open val apiException: MutableLiveData<Throwable> = MutableLiveData()

    open val apiLoading: MutableLiveData<Boolean> = MutableLiveData()

    private fun <Response> api(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        ViewModelDsl<Response>().apply(apiDSL).launch(viewModelScope)
    }

    protected fun <Response> request(apiDSL: ViewModelDsl<Response>.() -> Unit) {
        api<Response> {
            onRequest {
                ViewModelDsl<Response>().apply(apiDSL).request()
            }

            onResponse {
                ViewModelDsl<Response>().apply(apiDSL).onResponse?.invoke(it)
            }

            onStart {
                val override = ViewModelDsl<Response>().apply(apiDSL).onStart?.invoke()
                if (override == null || !override) {
                    onApiStart()
                }
                override
            }

            onError { errorMsg,httpCode ->
                val override = ViewModelDsl<Response>().apply(apiDSL).onError?.invoke(errorMsg,httpCode)
                if (override == null || !override) {
                  //  onApiError(error)
                }
                override

            }

            onFinally {
                val override = ViewModelDsl<Response>().apply(apiDSL).onFinally?.invoke()
                if (override == null || !override) {
                    onApiFinally()
                }
                override
            }
        }
    }
    protected open fun onApiStart() {
        apiLoading.value = true
    }

    protected open fun onApiError(e: String?,httpCode :Int) {
        apiLoading.value = false
      //  apiException.value = e

    }

    protected open fun onApiFinally() {
        apiLoading.value = false

    }
}

/**
 * Result必须加泛型 不然response的泛型就会被擦除!!
 * damn it
 */
sealed class Result<T> {
    class Start<T> : Result<T>()
    class Finally<T> : Result<T>()
    data class Response<T>(val response: T) : Result<T>()
    data class Error<T>(val exception: Exception) : Result<T>()
}
