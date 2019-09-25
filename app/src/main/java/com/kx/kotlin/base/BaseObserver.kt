package com.kx.kotlin.base

import com.kx.kotlin.bean.BaseResponse
import com.kx.kotlin.http.ErrorStatus
import com.kx.kotlin.http.ExceptionHandle
import io.reactivex.observers.ResourceObserver

abstract class BaseObserver<T> : ResourceObserver<BaseResponse<T>>() {

    override fun onNext(t: BaseResponse<T>) {
        when {
            t.errorCode == ErrorStatus.SUCCESS  -> onSuccess(t.data!!)
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
     * 成功的回调
     */
    protected abstract fun onSuccess(result: T)

    protected abstract fun onError(errorMsg: String)

}