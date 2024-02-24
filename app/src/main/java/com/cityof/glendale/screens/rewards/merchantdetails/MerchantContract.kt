package com.cityof.glendale.screens.rewards.merchantdetails

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem

interface MerchantContract {


    data class State(
        val merchant: Merchant = Merchant(),
        val hivePoints: HivePoints = HivePoints(),

        var showToast: Boolean = false,
        val toastMsg: UIStr? = null,

        var isLoading: Boolean = false,
        var page: Int = 1,
        var isEndReached: Boolean = false,
        var list: List<MerchantItem> = listOf(),
        var isAuthErr: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg:UIStr): Intent()
        object NavRedeemDetails : Intent()
    }


    sealed class NavAction {
        object NavRedeemDetail : NavAction()
    }

}