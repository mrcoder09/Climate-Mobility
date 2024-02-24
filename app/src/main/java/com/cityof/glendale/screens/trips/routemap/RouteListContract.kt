package com.cityof.glendale.screens.trips.routemap

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.umoresponses.UmoRoute

interface RouteListContract {


    data class State(
//        val routes: List<Route> = emptyList(),

        val routes: List<UmoRoute> = emptyList(), val toastMsg: UIStr? = null,

        val isAuthErr: Boolean = false, val isLoading: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()

        data class OpenRouteMap(val routeId: String) : Intent()

        data class NavWebview(
            val url:String
        ): Intent()

    }


    sealed class NavAction {
        data class NavWebView(
            val url: String
        ) : NavAction()
    }
}