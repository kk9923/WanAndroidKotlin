//package com.kx.kotlin.http.dsl
//
//data class ResultData<T>(val requestStatus: RequestStatus, val data: T?, val error: Throwable? = null) {
//
//    companion object {
//        fun <T> start(): ResultData<T> {
//            return ResultData(RequestStatus.START, null, null)
//        }
//
//        fun <T> success(data: T?, isCache: Boolean = false): ResultData<T> {
//            return ResultData(RequestStatus.SUCCESS, data, null)
//        }
//
//        fun <T> complete(data: T?): ResultData<T> {
//            return ResultData(RequestStatus.COMPLETE, data, null)
//        }
//

//        fun <T> error(error: Throwable?): ResultData<T> {
//            return ResultData(RequestStatus.ERROR, null, error)
//        }
//    }
//}