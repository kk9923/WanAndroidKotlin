package com.kx.kotlin.base;

import com.kx.kotlin.bean.BaseResponse;
import com.kx.kotlin.http.ErrorStatus;
import com.kx.kotlin.http.ExceptionHandle;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseObserverJava<T> implements Observer<BaseResponse<T>> {
    @Override
    public void onSubscribe(Disposable d) {

    }

    /**
     *  根据 errorcode 处理返回结果
     */
    @Override
    public void onNext(BaseResponse<T> baseResponse) {
        int errorCode = baseResponse.getErrorCode();
        if (errorCode == ErrorStatus.SUCCESS) {
            onSuccess(baseResponse.getData());
        } else {
            onError(baseResponse.getErrorMsg());
        }
    }
    @Override
    public void onError(Throwable e) {
        onError(ExceptionHandle.Companion.handleException(e));
    }
    @Override
    public void onComplete() {

    }
    abstract void onSuccess(T t);

    abstract void onError(String errorMsg);
}
