package com.cityof.glendale.network.responses

import android.content.Context
import android.os.Parcelable
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.TravelMode
import com.cityof.glendale.data.enums.TravelModeData
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.DateFormats.DATE_FORMAT_2
import com.cityof.glendale.utils.combineDateTime
import com.cityof.glendale.utils.xtFormat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import timber.log.Timber

data class SavedTripResponse(
    @SerializedName("customcode") val customcode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<SavedTrip>? = null,
)


@Parcelize
data class SavedTrip(
    @SerializedName("bus_number") val busNumber: String? = null,
    @SerializedName("distance") val distance: Double? = null,
    @SerializedName("end_destination") val endDestination: String? = null,
    @SerializedName("from_address") val fromAddress: String? = null,
    @SerializedName("from_lat") val fromLat: Double? = null,
    @SerializedName("from_long") val fromLong: Double? = null,
    @SerializedName("from_time") val fromTime: Long? = null,
    @SerializedName("label") val label: String? = null,
    @SerializedName("route_information") val routeInformation: String? = null,
    @SerializedName("starting_point") val startingPoint: String? = null,
    @SerializedName("to_address") val toAddress: String? = null,
    @SerializedName("to_lat") val toLat: Double? = null,
    @SerializedName("to_long") val toLong: Double? = null,
    @SerializedName("to_time") val toTime: Long? = null,
    @SerializedName("transport_mode") val transportMode: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("emission") val emission: Double? = null,
    @SerializedName("point") val point: Double? = null,
    @SerializedName("date") val date: Long? = null,
    @SerializedName("time") val time: Long? = null,
    @SerializedName("journey_id") val journeyId: String? = null,
    @SerializedName("journey_status") val journeyStatus: String? = null,
) : Parcelable


fun SavedTrip.formattedPoints(context: Context): String {
    return "$emission ${
        UIStr.ResStr(R.string.emission_reduced).toStr(context)
    }/ ${point ?: ""} ${UIStr.ResStr(R.string.green_points).toStr(context)}"
}

fun SavedTrip.isTripOngoing(): Boolean {
    return journeyId != null
}

fun SavedTrip.toLocationSearchedOrigin(): LocationSearched {

    return LocationSearched(
        name = this.startingPoint,
        address = this.fromAddress,
        latLng = LatLng(fromLat ?: 0.0, fromLong ?: 0.0)
    )
}

fun SavedTrip.toLocationSearchedDest(): LocationSearched {
    return LocationSearched(
        name = this.endDestination,
        address = this.toAddress,
        latLng = LatLng(toLat ?: 0.0, toLong ?: 0.0)
    )
}

fun SavedTrip.toRoute(): Route? {
    return routeInformation?.let { route ->
        Gson().fromJson(route, Route::class.java)
    } ?: run {
        null
    }
}


fun SavedTrip.toIcon(): Int {
    return when (transportMode) {
        "walking" -> R.drawable.ic_walk
        "bicycling" -> R.drawable.ic_bicycle
        else -> R.drawable.ic_bus_purple
    }
}

fun SavedTrip.getCircledIcon(): Int {
    return when (transportMode) {
        "bus" -> R.drawable.ic_bus_purple_circled
        "walking" -> R.drawable.ic_walk_circled_purple
        "bicycling" -> R.drawable.ic_bicycle_purple_circled
        else -> R.drawable.ic_bus_purple_circled
    }
}


fun SavedTrip.getFormattedFromTime(): String {
    return fromTime?.let {
        if (it > 0) (it * 1000).xtFormat(DateFormats.TIME_FORMAT_2)
        else ""
    } ?: ""
}

fun SavedTrip.getFormattedToTime(): String {
    return toTime?.let {
        if (it > 0) (it * 1000).xtFormat(DateFormats.TIME_FORMAT_2)
        else ""
    } ?: ""
}

fun SavedTrip.getDate(): Long? {
    return date?.let {
        it * 1000
    }
}

fun SavedTrip.getTime(): Long? {
    return time?.let {
        it * 1000
    }
}


fun SavedTrip.timeInMillis(): Long{

    val tripDateTime = combineDateTime(date, time)//calendar.timeInMillis
    val currentDate = System.currentTimeMillis()
    Timber.d("SAVED_TRIP: TripDateTime: $tripDateTime CurrentDate: $currentDate = ${tripDateTime < currentDate}")
    return if (tripDateTime < currentDate){
        currentDate / 1000
    } else {
       tripDateTime / 1000
   }
}

fun SavedTrip.formattedDateTime():String{
    val tripDateTime = combineDateTime(date,time)
    Timber.d("SAVED_TRIP: DATE: $date TIME: $time")
    return tripDateTime.xtFormat(DATE_FORMAT_2)
}

fun SavedTrip.toTravelMode(): TravelMode? {
    return when (transportMode) {
        "walking" -> TravelMode.Walking
        "bus" -> TravelMode.Bus
        "bicycling" -> TravelMode.Bicycling
        else -> null
    }
}

fun SavedTrip.isBeeline(): Boolean {
    return toTravelMode()?.let {
        it == TravelMode.Bus
    } ?: false
}

fun SavedTrip.toTravelModeData(): TravelModeData {
    return when (transportMode) {
        "walking" -> TravelModeData(
            UIStr.ResStr(R.string.walking),
            R.drawable.ic_walking,
            TravelMode.Walking,
        )

        "bicycling" -> TravelModeData(
            UIStr.ResStr(R.string.biking),
            R.drawable.ic_bicycle,
            TravelMode.Bicycling,
        )

        else -> TravelModeData(
            UIStr.ResStr(R.string.mode_beeline),
            R.drawable.ic_bus_purple,
            TravelMode.Bus,
        )
    }
}


fun SavedTrip.canStartTrip(): Boolean {
    val tripDateTime = combineDateTime(getDate(), getTime()) //calendar.timeInMillis
    val currentDate = System.currentTimeMillis()
    Timber.d("SAVED_TRIP: TripDateTime: $tripDateTime CurrentDate: $currentDate = ${tripDateTime >= currentDate}")
    return currentDate >= tripDateTime
}