package com.kx.kotlin.http

import com.kx.kotlin.bean.BaseResponse
import com.kx.kotlin.bean.HttpResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by PengFeifei on 2019-12-24.
 *
 * https://juejin.im/post/5d4d17e5f265da039401f6ea
 */

class ViewModelDsl<Response> {

    internal lateinit var request: suspend () -> BaseResponse<Response>

    internal var onStart: (() -> Boolean?)? = null

    internal var onResponse: ((Response?) -> Unit)? = null

    internal var onError: ((String, Int) -> Boolean?)? = null

    internal var onFinally: (() -> Boolean?)? = null


    infix fun onStart(onStart: (() -> Boolean?)?) {
        this.onStart = onStart
    }

    infix fun onRequest(request: suspend () -> BaseResponse<Response>) {
        this.request = request
    }

    infix fun onResponse(onResponse: ((Response?) -> Unit)?) {
        this.onResponse = onResponse
    }

    infix fun onError(onError: ((String, Int) -> Boolean?)?) {
        this.onError = onError
    }

    infix fun onFinally(onFinally: (() -> Boolean?)?) {
        this.onFinally = onFinally
    }


    internal fun launch(viewModelScope: CoroutineScope) {
        viewModelScope.launch(context = Dispatchers.Main) {
            onStart?.invoke()
            try {
                val response = withContext(Dispatchers.IO) {
                    request()
                }
                val successful = response.isSuccessful()
                if (successful) {
                    val data = response.data
                    onResponse?.invoke(data)
                } else {
                    // 错误码
                    when (response.errorCode) {
                        401 -> {

                        }
                        500 -> {

                        }
                    }
                    onError?.invoke(response.errorMsg, response.errorCode)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke(e.toString(), 0)
            } finally {
                onFinally?.invoke()
            }
        }
    }


}