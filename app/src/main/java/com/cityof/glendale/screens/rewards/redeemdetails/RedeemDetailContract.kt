package com.cityof.glendale.screens.rewards.redeemdetails

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem

interface RedeemDetailContract {


    data class State(
        val merchant: Merchant = Merchant(),
        val item: MerchantItem = MerchantItem(),
        val hivePoints: HivePoints = HivePoints(),

        val showRedeemDialog: Boolean = false,
        val showRedeemDialogForPass: Boolean = false,
        val showCongratulations: Boolean = false,

        val toastMsg: UIStr? = null,
        val isLoading: Boolean = false,
        val isAuthErr: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()
        data class ShowRedeemDialog(val show: Boolean = false) : Intent()
        data class ShowRedeemDialogForPass(val show: Boolean = false) : Intent()
        data class ShowCongratulations(val show: Boolean = false) : Intent()

        object RedeemRewardClick : Intent()
    }


    sealed class NavAction {
        object NavRedeemItem : NavAction()
    }

}