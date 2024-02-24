package com.cityof.glendale.network.umoresponses

import android.os.Parcelable
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.xtFormat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class UmoVehicle(
    @SerializedName("avlTime") val avlTime: Long? = null,
    @SerializedName("breakpointing") val breakpointing: Boolean? = null,
    @SerializedName("deadheadingBusYard") val deadheadingBusYard: @RawValue Any? = null,
    @SerializedName("detourId") val detourId: @RawValue Any? = null,
    @SerializedName("dir") val dir: Dir? = null,
    @SerializedName("gpsTime") val gpsTime: Long? = null,
    @SerializedName("heading") val heading: Int? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("jobTag") val jobTag: String? = null,
    @SerializedName("kph") val kph: Int? = null,
    @SerializedName("lastDepot") val lastDepot: String? = null,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("linkedVehicleIds") val linkedVehicleIds: String? = null,
    @SerializedName("lon") val lon: Double? = null,
    @SerializedName("occupancyDescription") val occupancyDescription: String? = null,
    @SerializedName("occupancyStatus") val occupancyStatus: Int? = null,
    @SerializedName("predUsingNavigationTm") val predUsingNavigationTm: Boolean? = null,
    @SerializedName("predictable") val predictable: Boolean? = null,
    @SerializedName("route") val route: Route? = null,
    @SerializedName("runId") val runId: @RawValue Any? = null,
    @SerializedName("scheduledAdherenceSec") val scheduledAdherenceSec: Int? = null,
    @SerializedName("scheduledHeadwaySec") val scheduledHeadwaySec: Int? = null,
    @SerializedName("secsSinceReport") val secsSinceReport: Int? = null,
    @SerializedName("tripTag") val tripTag: String? = null,
    @SerializedName("vehiclePosition") val vehiclePosition: VehiclePosition? = null,
    @SerializedName("vehicleType") val vehicleType: @RawValue Any? = null,
    @SerializedName("vehiclesInConsist") val vehiclesInConsist: Int? = null
) : Parcelable

fun UmoVehicle.getFromAndTo(): Pair<String, String>? {
    return route?.name?.split(" to ")?.let {
        val from = it[0]
        val to = it[1]
        Pair(
            from, to
        )
    } ?: run {
        null
    }
}

fun UmoVehicle.getTime(): String {
    return try {
        avlTime?.xtFormat(DateFormats.TIME_FORMAT_2) ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun UmoVehicle.getLatLng(): LatLng? {
    return if (lat != null && lon != null) LatLng(lat, lon)
    else null
}


@Parcelize
data class VehiclePosition(
    @SerializedName("atCurrentStop") val atCurrentStop: Boolean? = null,
    @SerializedName("currentStopOrderInTripPattern") val currentStopOrderInTripPattern: Int? = null,
    @SerializedName("currentStopTag") val currentStopTag: String? = null,
    @SerializedName("pathIndex") val pathIndex: Int? = null,
    @SerializedName("pathTag") val pathTag: String? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("predictionTime") val predictionTime: Long? = null
) : Parcelable

@Parcelize
data class Route(
    @SerializedName("id") val id: String? = null, @SerializedName("name") val name: String? = null
) : Parcelable

@Parcelize
data class Dir(
    @SerializedName("dirName") val dirName: String? = null,
    @SerializedName("id") val id: String? = null
) : Parcelable