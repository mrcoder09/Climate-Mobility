package com.cityof.glendale.screens.trips.savedtrips

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.SavedTrip

interface SavedTripContract {

    data class State(
        val list: List<SavedTrip> = emptyList(),

        val toastMsg: UIStr? = null, val isAuthErr: Boolean = false, val isLoading: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()
//        data class EditTrip(val tripId: String) : Intent()
        data class DeleteTrip(val tripId: String, val index: Int) : Intent()
        data class StartTrip(
            val savedTrip: SavedTrip
        ) : Intent()

        data class NavTripPlan(val isEditTrip: Boolean = false, val savedTrip: SavedTrip? = null) :
            Intent()

        object ResetNav: Intent()


    }


    sealed class NavAction {
        data class RouteTracking(
            val route: com.cityof.glendale.network.googleresponses.Route?,
            val trip: SavedTrip
        ) : NavAction()

        data class TripPlan(val trip: SavedTrip?) : NavAction()

        object None: NavAction()
    }
}