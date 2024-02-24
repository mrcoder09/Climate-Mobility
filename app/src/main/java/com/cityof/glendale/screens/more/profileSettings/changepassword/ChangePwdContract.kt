package com.cityof.glendale.screens.more.profileSettings.changepassword

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.LoginData
import com.cityof.glendale.utils.Exclude


/**
{
"currentPassword": "anshuarya1",
"password": "anshuarya1",
"passwordConfirmation": "anshuarya1"
}
 */
interface ChangePwdContract {
    data class State(
        var currentPassword: String = "",
        var password: String = "",
        var passwordConfirmation: String = "",

        @Exclude var isSuccess: Boolean = false,

        @Exclude var currentPwdErr: UIStr = UIStr.Str(),
        @Exclude var newPasswordErr: UIStr = UIStr.Str(),
        @Exclude var confirmPasswordErr: UIStr = UIStr.Str(),

        @Exclude var isLoading: Boolean = false,
        @Exclude var loginData: LoginData? = null,
        @Exclude var msgToast: UIStr? = null,
    )


    sealed class Intent {
        data class CurrentPwdEdited(var currentPwd: String = "") : Intent()
        data class NewPasswordEdited(var newPassword: String = "") : Intent()
        data class ConfirmPwdEdited(var confirmPwd: String = "") : Intent()
        data class ShowToast(var msg: UIStr) : Intent()
        object SubmitClicked : Intent()
    }

    sealed class NavAction{
        object NavLogin : NavAction()
    }
}