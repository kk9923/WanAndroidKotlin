package com.kx.kotlin.http.dsl

open class RequestAction<ResponseType> {
    var api: (suspend () -> ResponseType)? = null

    fun api(block: suspend () -> ResponseType) {
        this.api = block
    }
}
