package com.cityof.glendale.screens.trips

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.umoresponses.UmoRoute

interface TripContract {

    data class State(
        val list: List<FakeSavedTrips> = emptyList(),

        val saveTrips: List<SavedTrip> = emptyList(),

        val routes: List<UmoRoute> = emptyList(),

        val toastMsg: UIStr? = null, val isAuthErr: Boolean = false, val isLoading: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()

        data class EditTrip(val savedTrip: SavedTrip? = null) : Intent()
        data class DeleteTrip(val tripId: String, val index: Int) : Intent()

        object NavFare : Intent()
        object NavRouteList : Intent()

        object NavSavedTrips : Intent()

    }


    sealed class NavAction {

        object NavFare : NavAction()
        object NavRouteList : NavAction()
        data class NavTripPlan(val trip: SavedTrip?) : NavAction()

        object NavSavedTrips : NavAction()

    }
}


