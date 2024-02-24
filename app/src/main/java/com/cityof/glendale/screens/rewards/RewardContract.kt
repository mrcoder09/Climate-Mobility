package com.cityof.glendale.screens.rewards

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant

interface RewardContract {

    data class State(
        var title: String = "",

        var showToast: Boolean = false,
        val toastMsg: UIStr? = null,

        var isLoading: Boolean = false,
        var page: Int = 1,
        var isEndReached: Boolean = false,
        var list: List<Merchant> = listOf(),

        var hivePoints: HivePoints = HivePoints(),

        var isAuthErr: Boolean = false
    )

    sealed class Intent {
        object LoadMerchant : Intent()
        data class ShowToast(val msg: UIStr) : Intent()

        //        object NavMerchantDetailBeeline : Intent()
        object NavMerchantDetail : Intent()
    }


    sealed class NavAction {
        //        object MERCHANT_DETAILS_BEELINE : NavAction()
        object MERCHANT_DETAILS : NavAction()
    }
}