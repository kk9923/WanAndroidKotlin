package com.kx.kotlin.bean

open class BaseBean {
    var errorCode: Int = 0
        get() {
            return field
        }
    var errorMsg: String = ""

    fun isSuccessful() = errorCode == 200 || errorCode == 0

}