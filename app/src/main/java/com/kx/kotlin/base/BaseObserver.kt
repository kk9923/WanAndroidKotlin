package com.kx.kotlin.base

import com.kx.kotlin.bean.BaseResponse
import com.kx.kotlin.http.ErrorStatus
import com.kx.kotlin.http.ExceptionHandle
import io.reactivex.observers.ResourceObserver

open class BaseObserver<T> : ResourceObserver<BaseResponse<T>>() {

    override fun onNext(t: BaseResponse<T>) {
        when {
            t.errorCode == ErrorStatus.SUCCESS ->
                if (t.data != null) {
                    onSuccess(t.data!!)
                } else {
                    onSuccess()
                }
            t.errorCode == ErrorStatus.TOKEN_INVALID -> {
                // TODO Token 过期，重新登录
            }
            else -> {
                onError(t.errorMsg)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onComplete() {

    }

    override fun onError(e: Throwable) {
        onError(ExceptionHandle.handleException(e))
    }

    /**
     * 成功的回调--有返回结果
     */
    open fun onSuccess(result: T) {
    }
    /**
     * 成功的回调--无返回结果
     */
    open fun onSuccess() {
    }
    /**
     * 失败回调
     */
    open fun onError(errorMsg: String) {
    }
}