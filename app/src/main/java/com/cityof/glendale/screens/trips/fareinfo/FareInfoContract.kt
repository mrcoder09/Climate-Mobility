package com.cityof.glendale.screens.trips.fareinfo

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.FareInfo
import com.cityof.glendale.screens.feedback.FeedbackContract

interface FareInfoContract {

    data class State(
        val routes: List<FareInfo> = emptyList(), val toastMsg: UIStr? = null,

        val isAuthErr: Boolean = false, val isLoading: Boolean = false
    )


    sealed class Intent {
        object TapStoreClicked : Intent()

        data class ShowToast(val msg: UIStr) : Intent()


        object ResetNav : Intent()


    }


    sealed class NavAction {

        object NavTapStore : NavAction()
        object NavNone: NavAction()
    }
}