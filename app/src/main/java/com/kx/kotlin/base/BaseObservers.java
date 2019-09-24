//package com.kx.kotlin.base;
//
//import com.kx.kotlin.bean.BaseResponse;
//import com.kx.kotlin.http.ErrorStatus;
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//
//public class BaseObservers<T extends BaseResponse> implements Observer<T> {
//    @Override
//    public void onSubscribe(Disposable d) {
//
//    }
//
//    @Override
//    public void onNext(T t) {
//        if (t instanceof BaseResponse) {
//            BaseResponse baseResponse = t;
//            int errorCode = baseResponse.getErrorCode();
//            if (errorCode == ErrorStatus.SUCCESS)
//                onSuccess(baseResponse.getData())
//
////            baseResponse.errorCode == ErrorStatus.TOKEN_INVALID ->{
////                // TODO Token 过期，重新登录
////            }
//        }
//    }
//
//    @Override
//    public void onError(Throwable e) {
//
//    }
//
//    @Override
//    public void onComplete() {
//
//    }
//
//    protected void onSuccess(T t) {
//
//    }
//
//    protected void onError(String errorMsg) {
//
//    }
//
//}
