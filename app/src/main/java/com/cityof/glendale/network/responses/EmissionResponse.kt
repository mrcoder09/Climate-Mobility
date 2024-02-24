package com.cityof.glendale.network.responses


import com.google.gson.annotations.SerializedName

data class EmissionResponse(
    @SerializedName("customcode") val customcode: Int? = null,
    @SerializedName("data") val data: Emission? = null,
    @SerializedName("success") val success: Boolean? = null
)


data class Emission(
    @SerializedName("available_points") val availablePoints: Double? = null,
    @SerializedName("community_emmision") val communityEmmision: Double? = null,
    @SerializedName("group_emission") val groupEmission: Double? = null,
    @SerializedName("personal_emission") val personalEmission: Double? = null,
    @SerializedName("tripData") val tripData: SavedTrip? = null
)


fun Emission.isGroupEmission() = groupEmission != 0.0

fun Emission.isTripOngoing() = (tripData != null && tripData.isTripOngoing())

fun Emission.isNoActivity() = (personalEmission == 0.0 && groupEmission == 0.0 && communityEmmision == 0.0)
