package com.kx.kotlin.util

import com.kx.kotlin.bean.BaseResponse
import com.kx.kotlin.http.ApiException
import com.kx.kotlin.http.ErrorStatus
import com.kx.kotlin.http.ExceptionHandle
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

class RxUtil {

    companion object {
        /**
         * 统一返回结果处理
         * @param <T> 指定的泛型类型
         * @return ObservableTransformer
         */
         fun <T> ioMainHandResult(): ObservableTransformer<BaseResponse<T>, T> {
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                    .onErrorResumeNext(Function { throwable ->
                        Observable.error(ApiException(ExceptionHandle.handleException(throwable)))
                    }).flatMap {
                        if (it.errorCode == ErrorStatus.SUCCESS) {
                            Observable.just(it.data!!)
                        } else {
                            Observable.error(ApiException(it.errorMsg))
                        }
                    }.observeOn(AndroidSchedulers.mainThread())
            }
        }
        /**
         * 统一线程处理
         */
         fun <T> ioMain(): ObservableTransformer<T, T> {
            return ObservableTransformer { upstream ->
                upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        }

    }

}