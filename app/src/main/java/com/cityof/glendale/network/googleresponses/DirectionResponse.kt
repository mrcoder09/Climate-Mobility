package com.cityof.glendale.network.googleresponses

import android.os.Parcelable
import com.cityof.glendale.data.enums.TravelMode
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.xtFormat
import com.cityof.glendale.utils.xtJson
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import timber.log.Timber
import java.util.Locale

@Parcelize
data class DirectionResponse(
    @SerializedName("geocoded_waypoints") val geocodedWaypoints: List<GeocodedWaypoint>? = listOf(),
    @SerializedName("routes") val routes: List<Route>? = listOf(),
    @SerializedName("status") val status: String? = ""
) : Parcelable

@Parcelize
data class Bounds(
    @SerializedName("northeast") val northeast: Location? = null,
    @SerializedName("southwest") val southwest: Location? = null
) : Parcelable

@Parcelize
data class Location(
    @SerializedName("lat") val lat: Double? = null, @SerializedName("lng") val lng: Double? = null
) : Parcelable


fun Location.toLatLng(): LatLng {
    return LatLng(lat ?: 0.0, lng ?: 0.0)
}

@Parcelize
data class Distance(
    @SerializedName("text") val text: String? = null,
    @SerializedName("value") val value: Int? = null
) : Parcelable

@Parcelize
data class Duration(
    @SerializedName("text") val text: String? = null,
    @SerializedName("value") val value: Int? = null
) : Parcelable

@Parcelize
data class GeocodedWaypoint(
    @SerializedName("geocoder_status") val geocoderStatus: String? = null,
    @SerializedName("partial_match") val partialMatch: Boolean? = null,
    @SerializedName("place_id") val placeId: String? = null,
    @SerializedName("types") val types: List<String?>? = null
) : Parcelable

@Parcelize
data class Leg(
    @SerializedName("distance") val distance: Distance? = Distance(),
    @SerializedName("duration") val duration: Duration? = Duration(),
    @SerializedName("end_address") val endAddress: String? = "",
    @SerializedName("end_location") val endLocation: Location? = Location(),
    @SerializedName("start_address") val startAddress: String? = "",
    @SerializedName("start_location") val startLocation: Location? = Location(),
    @SerializedName("steps") val steps: List<Step>? = listOf(),
    @SerializedName("traffic_speed_entry") val trafficSpeedEntry: @RawValue List<Any>? = listOf(),
    @SerializedName("arrival_time") val arrivalTime: ArrivalTime = ArrivalTime(),
    @SerializedName("departure_time") val departureTime: ArrivalTime = ArrivalTime(),

    ) : Parcelable

@Parcelize
data class OverviewPolyline(
    @SerializedName("points") val points: String? = null
) : Parcelable

@Parcelize
data class Polyline(
    @SerializedName("points") val points: String? = null
) : Parcelable

@Parcelize
data class Route(
    @SerializedName("bounds") val bounds: Bounds? = Bounds(),
    @SerializedName("copyrights") val copyrights: String? = "",
    @SerializedName("legs") val legs: List<Leg>? = listOf(),
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline? = OverviewPolyline(),
    @SerializedName("summary") val summary: String? = "",
    @SerializedName("warnings") val warnings: List<String>? = listOf(),
    @SerializedName("waypoint_order") val waypointOrder: @RawValue List<Any>? = listOf()
) : Parcelable

@Parcelize
data class Step(
    @SerializedName("distance") val distance: Distance? = Distance(),
    @SerializedName("duration") val duration: Duration? = Duration(),
    @SerializedName("end_location") val endLocation: Location? = Location(),
    @SerializedName("html_instructions") val htmlInstructions: String? = "",
    @SerializedName("maneuver") val maneuver: String? = "",
    @SerializedName("polyline") val polyline: Polyline? = Polyline(),
    @SerializedName("start_location") val startLocation: Location? = Location(),
    @SerializedName("travel_mode") val travelMode: String? = "",
    @SerializedName("steps") val steps: List<Step>? = listOf(),
    @SerializedName("transit_details") val transitDetail: TransitDetail? = TransitDetail()
) : Parcelable


//SOME USEFULL EXTENSIONS
fun Route.getFormattedArrivalTime(): String? {

    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            "${temp.departureTime.text} - ${temp.arrivalTime.text}"
        } else null
    } ?: run { null }
}

fun Route.getFormattedDepartureTime(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            (temp.departureTime.value?.times(1000))?.xtFormat(DateFormats.DATE_FORMAT_WITH_DAY)
        } else null
    } ?: run { null }
}

fun Route.getDepartureTimeStamp(): Long? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.departureTime.value
        } else null
    } ?: run { null }
}

fun Route.getArrivalTimeStamp(): Long? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.arrivalTime.value
        } else null
    } ?: run { null }
}

fun Route.getLegStart(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.startAddress
        } else null
    } ?: run { null }
}

fun Route.getLegEnd(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.endAddress
        } else null
    } ?: run { null }
}

fun Route.getDistanceFormatted(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.distance?.text
        } else null
    }
}

fun Route.getDistanceMeter(): Int? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.distance?.value
        } else null
    }
}

fun Route.getDuration(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            val temp = it[0]
            temp.duration?.text
        } else null
    }
}

fun Route.getStopCounts(): Int? {
    return legs?.let {
        if (it.isNotEmpty()) {
            it[0].steps?.count()
        } else null
    }
}

fun Route.getArrivalTime(): String? {

    return legs?.let {
        if (it.isNotEmpty()) {
            it[0].arrivalTime.text
        } else null
    }
}

fun Route.getDepartureTime(): String? {

    return legs?.let {
        if (it.isNotEmpty()) {
            it[0].departureTime.text
        } else null
    }
}

fun Route.getOriginLoc(): Location? {
    return legs?.let {
        if (it.isNotEmpty()) it[0].startLocation
        else null
    }
}

fun Route.getDestinationLoc(): Location? {
    return legs?.let {
        if (it.isNotEmpty()) it[0].endLocation
        else null
    }
}

fun Step.getTravelMode(): TravelMode {
    return if (travelMode.equals("WALKING", true)) TravelMode.Walking
    else if (travelMode.equals("TRANSIT")) TravelMode.Bus
    else TravelMode.Bicycling
}

fun Route.getPolyline(): List<LatLng>? {
    return overviewPolyline?.points?.let {
        PolyUtil.decode(it)
    } ?: run { null }
}

fun Route.getStepJson(): String? {
    return legs?.let {
        if (it.isNotEmpty()) {
            it[0].steps?.xtJson()
        } else null
    }
}

fun Route.getHtmlInstruction(): String? {
    return legs?.let { leg ->
        if (leg.isNotEmpty()) {
            leg.getOrNull(0)?.steps?.getOrNull(0)?.htmlInstructions
        } else null
    }
}


//fun Route.getBusShortName(): String? {
//    val result = legs?.getOrNull(0)?.steps?.filter {
//        it.travelMode?.equals("TRANSIT", true) == true
//    }?.getOrNull(0)?.transitDetail?.line?.shortName
//
//
//    val result1 = legs?.getOrNull(0)?.steps?.filter {
//        it.travelMode?.equals("TRANSIT", true) == true
//    }?.getOrNull(0)?.transitDetail?.line?.shortName
//
//    Timber.d("BUS_NAME: $result")
//
//    return result
//}

fun Route.getTransitRouteId(): String? {


    val list = legs?.getOrNull(0)?.steps?.filter {
        it.travelMode?.equals("TRANSIT", true) == true
    }?.filter {
        it.isStepWithBeelineAgency()
    }

    var shortName = list?.getOrNull(0)?.transitDetail?.line?.shortName

    if (shortName?.length == 1){
        val temp = shortName
        shortName = "0${shortName}"
    }
    Timber.d("BUS_NAME: $shortName")

    return shortName
}

fun DirectionResponse.filterRoutesWithBeelineAgency(): DirectionResponse {
    Timber.d("BEELINE_ROUTES: ${this.xtJson()}")

//
//    val filteredRoutes1 = routes?.forEach { route ->
//        route.legs?.forEach { leg ->
//            leg.steps?.forEach { step ->
//                val agenciesList = mutableListOf<String>()
//                val count = 0
//
//
//
//                Timber.d("NEW_FILER: ${step.htmlInstructions}")
//                Timber.d("NEW_FILTER: ${step.transitDetail?.line?.agencies?.size}")
//                step.transitDetail?.line?.agencies?.forEach { agency ->
//                    Timber.d("NEW_FILTER: ${agency?.name}")
//                    agency?.name?.lowercase(Locale.getDefault())?.let { agenciesList.add(it) }
//
//                }
//
//                val areAllValuesEqual = agenciesList.all {
//                    it.contains("beeline") || it.contains("glendale beeline") || it.contains("ctu")
//                }
//
//
//                Timber.d("NEW_FILTER: ${agenciesList.size} $areAllValuesEqual")
//            }
//        }
//    }


//    val filteredRoutes2 = routes?.filter { route ->
//        route.legs?.any { leg ->
//            leg.steps?.any { step ->
//                step.transitDetail?.line?.agencies?.let { agencies ->
//                    val containsGlendaleBeeline = agencies.all { agency ->
//                        val temp = agency?.name?.lowercase()
//                        Timber.d("FILTER_ROUTE: $temp ${agency?.name?.lowercase(Locale.getDefault()) == "glendale beeline"}")
//                        agency?.name?.lowercase(Locale.getDefault()) == "glendale beeline"
//                    }
//                    val containsOtherAgencies = agencies.any { agency ->
//                        val lowercaseName = agency?.name?.lowercase(Locale.getDefault())
//                        Timber.d("FILTER_ROUTE: $lowercaseName ${lowercaseName != "glendale beeline"}")
//                        lowercaseName != "glendale beeline"
//                    }
//                    Timber.d("FILTER_ROUTE: $containsGlendaleBeeline && ${!containsOtherAgencies}")
//                    containsGlendaleBeeline && !containsOtherAgencies
//                } ?: false
//            } ?: false
//        } ?: false
//    }


    val filteredRoutes = findBeelineRoutesOnly()


    return this.copy(routes = filteredRoutes)
}

fun DirectionResponse.findBeelineRoutesOnly(): List<Route> {

    val result = mutableListOf<Route>()
    routes?.forEach { route ->
        val list = mutableListOf<Boolean>()
        route.legs?.forEach { leg ->
            leg.steps?.forEach { step ->
                list.add(
                    step.isStepWithBeelineAgency()
                )
            }
        }
        Timber.d("NEW_FILTER: ${list.isNotEmpty()} && ${list.all { it }} == ${list.isNotEmpty() && list.all { it }} ")
        if (list.isNotEmpty() && list.all { it }) {
            result.add(route)
        }
    }
    return result
}

fun Step.isStepWithBeelineAgency(): Boolean {
    val agenciesList = mutableListOf<String>()

    transitDetail?.line?.agencies?.forEach { agency ->
        Timber.d("NEW_FILTER AGENCY: ${agency?.name}")
        agency?.name?.lowercase(Locale.getDefault())?.let { agenciesList.add(it) }
    }

    val areAllValuesEqual = agenciesList.all {
        it.contains("beeline") || it.contains("glendale beeline") || it.contains("ctu")
//                || it.contains("lacmta")
    }
//    Timber.d("NEW_FILTER CAN ADD: ${agenciesList.size} && ${areAllValuesEqual} == ${areAllValuesEqual}")
    return areAllValuesEqual
}

fun Step.getStepCount() = steps?.size
