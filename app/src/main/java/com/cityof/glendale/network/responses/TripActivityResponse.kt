package com.cityof.glendale.network.responses


import com.google.gson.annotations.SerializedName

data class TripActivityResponse(
    @SerializedName("customcode") val customcode: Int? = null,
    @SerializedName("journey_id") val journeyId: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("data") val data: TripActivity? = null
)

data class TripActivity(
    @SerializedName("point") val point: Double? = null,
    @SerializedName("emission") val emission: Double? = null,
)


fun TripActivityResponse.isSuccess() = (isSuccess(customcode) && success ?: false)