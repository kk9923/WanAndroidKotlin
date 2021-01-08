package com.kx.kotlin.bean

open class BaseResponse<T> {
    var errorCode: Int = 0
    var errorMsg: String = ""
    var data: T? = null
        get(){
            return field
        }

    fun isSuccessful() = errorCode == 200 || errorCode == 0
}