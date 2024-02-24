package com.cityof.glendale.screens

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.isTripOngoing

interface DashboardContract {


    data class State(
        val savedTrip: SavedTrip? = null,

        val isLoading: Boolean = false,
        val toastMsg: UIStr? = null,
        var isAuthErr: Boolean = false
    )

    sealed class Intent {

        data class CheckOngoingTrip(val trip: SavedTrip?) : Intent()
        data class ShowToast(val msg: UIStr) : Intent()

        object ResetNav: Intent()
        data class NavRouteTrack(val trip: SavedTrip?): Intent()
    }

    sealed class NavAction{
        data class NavRouteTrack(val trip: SavedTrip?): NavAction()
        object None: NavAction()
    }
}

fun DashboardContract.State.isTripOnGoing() = (savedTrip != null && savedTrip.isTripOngoing())