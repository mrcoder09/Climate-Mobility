package com.cityof.glendale.screens.more.notifications

import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr

interface NotificationContract {

    data class NotificationItem(
        var title: Int = R.string.service_delay,
        var desc: Int = R.string.desc_service_delays,
        var isOn: Boolean = false
    )

    data class State(
        var serviceDelay: NotificationItem = NotificationItem(),
        var detour: NotificationItem = NotificationItem(),

        val toastMsg: UIStr? = null, val isAuthErr: Boolean = false, val isLoading: Boolean = false
    )


    sealed class Intent {
        data class ServiceDelayEdited(var isOn: Boolean = false) : Intent()
        data class DetoursEdited(var isOn: Boolean = false) : Intent()

        data class ShowToast(val msg: UIStr) : Intent()
    }

    sealed class NavActions {

    }
}

fun NotificationContract.NotificationItem.getO1(): Int{
    return if (isOn){
        1
    } else{
        0
    }
}