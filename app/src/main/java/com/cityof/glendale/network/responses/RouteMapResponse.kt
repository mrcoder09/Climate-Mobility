package com.cityof.glendale.network.responses


import com.google.gson.annotations.SerializedName

data class RouteMapResponse(
    @SerializedName("customcode")
    val customcode: Int? = null,
    @SerializedName("data")
    val data: RouteMap? = null,
    @SerializedName("success")
    val success: Boolean? = null
)

data class RouteMap(
    @SerializedName("route_map")
    val routeMap: String? = null
)