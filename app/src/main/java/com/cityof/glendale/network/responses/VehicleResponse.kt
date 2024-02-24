package com.cityof.glendale.network.responses


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class VehicleResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("data") val data: List<Vehicle>? = null,
    @SerializedName("nextPage") val nextPage: Any? = null,
    @SerializedName("previousPage") val previousPage: Any? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("total") val total: Int? = null
)

@Parcelize
data class Vehicle(
    @SerializedName("description") val description: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("isActivated") val isActivated: Boolean? = null,
    @SerializedName("isDeleted") val isDeleted: Boolean? = null,
    @SerializedName("name") val name: String? = null
) : Parcelable