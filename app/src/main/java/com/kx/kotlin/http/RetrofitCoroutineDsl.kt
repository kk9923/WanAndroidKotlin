package com.kx.kotlin.http

import com.kx.kotlin.bean.HttpResult
import com.kx.kotlin.http.dsl.RequestAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import java.io.IOException
import java.net.ConnectException

class RetrofitCoroutineDsl<ResultType> {
    var api: (suspend () -> HttpResult<ResultType>)? = null

    fun api(block: suspend () -> HttpResult<ResultType>) {
        this.api = block
    }

    internal var onSuccess: ((ResultType?) -> Unit)? = null
        private set
    internal var onComplete: (() -> Unit)? = null
        private set
    internal var onFailed: ((error: String?, code: Int) -> Unit)? = null
        private set

    var showFailedMsg = false

    internal fun clean() {
        onSuccess = null
        onComplete = null
        onFailed = null
    }

    fun onSuccess(block: (ResultType?) -> Unit) {
        this.onSuccess = block
    }

    fun onComplete(block: () -> Unit) {
        this.onComplete = block
    }

    fun onFailed(block: (error: String?, code: Int) -> Unit) {
        this.onFailed = block
    }
}

fun <ResultType> CoroutineScope.requestLiveData(
    dsl: RetrofitCoroutineDsl<ResultType>.() -> Unit //传递方法，需要哪个，传递哪个
) {
    this.launch(Dispatchers.Main) {
        val retrofitCoroutine = RetrofitCoroutineDsl<ResultType>().apply (dsl)
        retrofitCoroutine.dsl()
        retrofitCoroutine.api?.let { it ->
            val work = async(Dispatchers.IO) { // io线程执行
                try {
                    it.invoke()
                } catch (e: ConnectException) {
                    retrofitCoroutine.onFailed?.invoke("网络连接出错", -100)
                    null
                } catch (e: IOException) {
                    retrofitCoroutine.onFailed?.invoke("未知网络错误", -1)
                    null
                }
            }
            work.invokeOnCompletion { _ ->
                // 协程关闭时，取消任务
                if (work.isCancelled) {
                    //  it.cancel()
                    //  retrofitCoroutine.clean()
                }
            }
            val response = work.await()
            response?.let {
                if (response.isSuccessful()) {
                    retrofitCoroutine.onSuccess?.invoke(response.data)
                    retrofitCoroutine.onComplete?.invoke()
                } else {
                    // 处理 HTTP code
                    when (response.errorCode) {
                        401 -> {
                        }
                        500 -> {
                        }
                    }
                    retrofitCoroutine.onFailed?.invoke(response.errorMsg, response.errorCode)
                }
            }
        }
    }
}

