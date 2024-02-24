package com.cityof.glendale.screens.forgotpwd

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.utils.Exclude

interface ForgotPwdContract {

    data class State(
        var email: String = "",

        @Exclude var emailErr: UIStr = UIStr.Str(),
        @Exclude var isLoading: Boolean = false,
        @Exclude var isShowDialog: Boolean = false,
        @Exclude var isMobile: Boolean = false,

        @Exclude var msgToast: UIStr? = null
    )

    sealed class Intent {
        data class EmailChanged(var email: String = "") : Intent()
        data class ShowToast(var message: UIStr) : Intent()

        //        data class ShowDialog(var isShowDialog: Boolean = false) : Intent()
        object Submit : Intent()
    }

    sealed class NavAction {
        object NavOtpVerify : NavAction()
    }
}