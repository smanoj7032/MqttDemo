package com.manoj.base.network.helper

import com.google.gson.annotations.SerializedName

open class ApiResponse<T> : SimpleApiResponse() {
    @SerializedName("data")
    val data: T? = null
    override fun toString(): String {
        return "ApiResponse{" +
                "data=" + data +
                '}'
    }
}