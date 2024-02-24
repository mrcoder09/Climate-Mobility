package com.cityof.glendale.network.responses

import com.google.gson.annotations.SerializedName

data class AddTripResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: SavedTrip
)

fun AddTripResponse.isSuccess() = (isSuccess(customCode) && success ?: false)