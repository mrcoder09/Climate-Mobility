package com.cityof.glendale.network.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class HivePointResponse(
    @SerializedName("data") val data: HivePoints? = null,
    @SerializedName("customcode") val customCode: Int? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("message") val message: String? = null
)


@Parcelize
data class HivePoints(
    @SerializedName("available_points") val availablePoints: Double? = null,
    @SerializedName("total_points") val totalPoints: Double? = null
) : Parcelable

fun HivePointResponse.isSuccess() = (isSuccess(customCode) && success ?: false)

