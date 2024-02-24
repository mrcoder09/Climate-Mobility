package com.cityof.glendale.network.responses


import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("customcode")
    val customcode: Int? = null,
    @SerializedName("data")
    val data: List<Route>? = null,
    @SerializedName("nextPage")
    val nextPage: Any? = null,
    @SerializedName("previousPage")
    val previousPage: Any? = null,
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("total")
    val total: Int? = null
)

data class Route(
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("end_point")
    val endPoint: String? = null,
    @SerializedName("end_point_code")
    val endPointCode: String? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("isActivated")
    val isActivated: Boolean? = null,
    @SerializedName("isDeleted")
    val isDeleted: Boolean? = null,
    @SerializedName("route_id")
    val routeId: Int? = null,
    @SerializedName("starting_point")
    val startingPoint: String? = null,
    @SerializedName("starting_point_code")
    val startingPointCode: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

fun Route.fromTo() = "$startingPoint to $endPoint"