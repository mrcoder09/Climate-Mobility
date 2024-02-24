package com.cityof.glendale.screens.trips.tripPlan

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.TravelModeData
import com.cityof.glendale.data.enums.getAllTransportModes
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.utils.combineDateTime
import timber.log.Timber


enum class TripSelect {
    ORIGIN_LOC, DESTINATION_LOC
}


interface TripPlanContract {


    data class State(
        var travelModes: List<TravelModeData> = getAllTransportModes(),
        val selectedTravelMode: TravelModeData = travelModes[0],

        val suggestedTitle: UIStr = UIStr.Str(""),
        val routes: List<Route> = emptyList(),
        val date: Long = System.currentTimeMillis(),
        val time: Long = System.currentTimeMillis(),

        val tripSelect: TripSelect = TripSelect.ORIGIN_LOC,
        var originLoc: LocationSearched? = null,
        var destinationLoc: LocationSearched? = null,

        val route: Route = Route(),
        val isAuthErr: Boolean = false,
        var isLoading: Boolean = false,
        val toastMsg: UIStr? = null,

        //TRIP DETAIL'S
        val label: String = "",
        val showCongratulations: Boolean = false,
        val savedTrip: SavedTrip? = null,
        val isEdit: Boolean = false,
        val saveTripId: String? = null
    )

    sealed class Intent {
        data class OriginLocChanged(
            val loc: LocationSearched
        ) : Intent()

        data class DestinationLocChanged(
            val loc: LocationSearched
        ) : Intent()

        object InitCurrentLocation : Intent()
        object FetchCurrentLocation : Intent()

        data class DateChanged(
            val date: Long
        ) : Intent()

        data class TimeChanged(
            val time: Long
        ) : Intent()

        object SwitchLocations : Intent()

        data class NavLocationSearch(
            val tripSelect: TripSelect
        ) : Intent()

        data class NavTripDetails(
            val route: Route
        ) : Intent()

        data class ShowToast(val msg: UIStr) : Intent()

        data class TravelModeChanged(val mode: TravelModeData) : Intent()


        //TRIP DETAIL's INTENT
        data class SaveTrip(val value: String, val isSaveTrip: Boolean = true) : Intent()
        data class EditTrip(val value: String) : Intent()
        data class ShowCongrats(val show: Boolean) : Intent()
        data class LabelChanged(val value: String) : Intent()
    }


    sealed class NavAction {

        object NavLocationSearch : NavAction()
        data class NavTripDetails(val route: Route) : NavAction()
        data class NavRouteTracking(val trip: SavedTrip?) : NavAction()

    }
}

fun TripPlanContract.State.isTripEdit(): Boolean {
    Timber.d("SAVED_TRIP: (savedTrip!=null) = ${savedTrip != null}")
    return (savedTrip != null)
}

fun TripPlanContract.State.canSearchDirection() = (originLoc != null && destinationLoc != null)


fun TripPlanContract.State.canStartTrip(): Boolean {

    val tripDateTime =  combineDateTime(date, time)//calendar.timeInMillis
    val currentDate = System.currentTimeMillis()
    Timber.d("SAVED_TRIP: TripDateTime: $tripDateTime CurrentDate: $currentDate = ${tripDateTime < currentDate}")
    return currentDate >= tripDateTime
}