package com.cityof.glendale.screens.feedback

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.UmoVehicle
import com.google.android.gms.maps.model.LatLng

interface FeedbackContract {

    data class State(
        val routes: List<UmoRoute> = emptyList(),
        val vehicles: List<UmoVehicle> = emptyList(),
        val toastMsg: UIStr? = null,
        val isLoading: Boolean = false,

        val is911Dialog: Boolean = false,
        val selectedRoute: UmoRoute = UmoRoute(),
        val selectedIndex: Int = 0,

        val currentLatLng: LatLng = LatLng(0.0, 0.0),
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()
//        data class VehicleList(val it: String) : Intent()
        data class VehicleList(val route: UmoRoute) : Intent()
        data class Set911Visibility(val show: Boolean) : Intent()
        data class NavFeedbackList(var vehicle: UmoVehicle) : Intent()
    }


    sealed class NavAction {
        data class NavFeedbackList(val vehicle: UmoVehicle) : NavAction()
    }
}