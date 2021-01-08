package com.kx.kotlin.http.dsl

internal sealed class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error)
        }

        fun <T> create(body: T?): ApiResponse<T> {
            return if (body == null) {
                ApiEmptyResponse()
            } else {
                ApiSuccessResponse(body)
            }
        }
    }
}

internal class ApiEmptyResponse<T> : ApiResponse<T>()

internal data class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()

internal data class ApiErrorResponse<T>(val throwable: Throwable) : ApiResponse<T>()
