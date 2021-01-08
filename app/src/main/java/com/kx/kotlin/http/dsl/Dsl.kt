//package com.kx.kotlin.http.dsl
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.liveData
//import kotlinx.coroutines.CoroutineScope
//
//enum class RequestStatus {
//    START,
//    SUCCESS,
//    COMPLETE,
//    ERROR
//}
//
///**
// * DSL网络请求
// */
//inline fun <ResultType> CoroutineScope.requestLiveData(dsl: RequestAction<ResultType>.() -> Unit): LiveData<ResultData<ResultType>> {
//    val action = RequestAction<ResultType>().apply(dsl)
//    return liveData(this.coroutineContext) {
//        // 通知网络请求开始
//        emit(ResultData.start())
//        val apiResponse = try {
//            // 获取网络请求数据
//            val resultBean = action.api?.invoke()
//            ApiResponse.create(resultBean)
//        } catch (e: Throwable) {
//            ApiResponse.create<ResultType>(e)

//        }
//
//        // 根据 ApiResponse 类型，处理对于事物
//        val result = when (apiResponse) {
//            is ApiEmptyResponse -> {
//                null
//            }
//            is ApiSuccessResponse -> {
//                apiResponse.body.apply {
//                    // 提交成功的数据给LiveData
//                    emit(ResultData.success<ResultType>(this))
//                }
//            }
//            is ApiErrorResponse -> {
//                // 提交错误的数据给LiveData
//                emit(ResultData.error<ResultType>(apiResponse.throwable))
//                null
//            }
//        }
//        // 提交成功的信息
//        emit(ResultData.complete<ResultType>(result))
//    }
//}
//
