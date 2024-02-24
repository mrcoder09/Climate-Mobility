package com.cityof.glendale.screens.trips.routetracking

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.TripActivity
import com.google.android.gms.maps.model.LatLng

interface RouteTrackingContract {


    data class State(
        val routeIn: Route? = null,
        val tripIn: SavedTrip? = null,
        val isExitTrip: Boolean = false,
        val tripActivity: TripActivity? = null,

        val userLatLng: List<LatLng> = emptyList(),
        val userRoute: Route? = null,
        val previousLatLng: LatLng? = null,
        val currentLatLng: LatLng? = null,

        val toastMsg: UIStr? = null,
        val isLoading: Boolean = false,
        val onExit: Boolean = false,
        val vehicleId: String? = null
    )

    sealed class Intent {
        object ExitTrip : Intent()
        object StartTrip : Intent()

        data class ShowToast(val msg: UIStr) : Intent()
//        object RequestLocationUpdates : Intent()
//        object RemoveLocationUpdates : Intent()

        data class SetDestination(val destination: LatLng) : Intent()
        data class GetDirections(val currentLatLng: LatLng) : Intent()
        data class UpdateCurrentLatLng(val latLng: LatLng?) : Intent()
        object ShowFeedback : Intent()
        object GetBusNumber : Intent()

    }

    enum class TripStatus {
        ONGOING, //ongoing
        COMPLETE, //complete
    }
}