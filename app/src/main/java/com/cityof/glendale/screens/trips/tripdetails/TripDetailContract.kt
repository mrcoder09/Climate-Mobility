package com.cityof.glendale.screens.trips.tripdetails

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.googleresponses.Route

interface TripDetailContract {


    data class State(
        val route: Route? = null,
        val originLoc: LocationSearched? = null,
        val destinationLoc: LocationSearched? = null,
        val tripDate: Long? = null,
        val tripTime:Long? = null,

        val toastMsg: UIStr? = null,
        val isAuthErr: Boolean = false,
        val isLoading: Boolean = false,
        val isEdit: Boolean = false
    )

    sealed class Intent{
        object SaveTrip: Intent()
        data class ShowToast(val msg: UIStr) : Intent()
    }


    sealed class NavAction
}