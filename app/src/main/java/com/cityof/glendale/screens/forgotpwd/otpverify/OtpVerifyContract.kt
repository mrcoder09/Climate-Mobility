package com.cityof.glendale.screens.forgotpwd.otpverify

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.utils.Exclude

interface OtpVerifyContract {


    data class State(
        var email: String = "",
        var otp: String = "",

        @Exclude var otpError: UIStr = UIStr.Str(),
        @Exclude var otpReceived: String = "",
        @Exclude var canResend: Boolean = false,
        @Exclude var isLoading: Boolean = false,

        @Exclude var isCongratulations: Boolean = true,
        @Exclude var msgToast: UIStr? = null
    )

    sealed class Intent {
        data class SetEmail(val email: String) : Intent()
        data class OtpEdited(var otp: String = "") : Intent()
        data class CanResend(var canResend: Boolean = false) : Intent()

        object Submit : Intent()
        object Resend : Intent()

        data class ShowDialog(var show: Boolean = false) : Intent()

        data class ShowToast(var message: UIStr) : Intent()
    }

    sealed class NavActions {
        object NavCreateNewPassword : NavActions()
    }
}