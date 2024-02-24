package com.cityof.glendale.network.responses

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?
)

fun BaseResponse.isSuccess() = (isSuccess(customCode) && success ?: false)




