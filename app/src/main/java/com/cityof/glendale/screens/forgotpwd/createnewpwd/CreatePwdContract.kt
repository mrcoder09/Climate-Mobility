package com.cityof.glendale.screens.forgotpwd.createnewpwd

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.utils.Exclude

interface CreatePwdContract {


    data class State(
        var email: String = "",
        var password: String = "",
        var passwordConfirmation: String = "",

        @Exclude var pwdErr: UIStr = UIStr.Str(),
        @Exclude var confirmPwdErr: UIStr = UIStr.Str(),
        @Exclude var isCongratulations: Boolean = false,
        @Exclude var isLoading: Boolean = false,
        @Exclude var msgToast: UIStr? = null
    )

    sealed class Intent {
        data class PwdEdited(var pwd: String = "") : Intent()
        data class ConfirmPwdEdited(var confirmPwd: String = "") : Intent()

        data class ShowCongratulations(var canShow: Boolean = false) : Intent()
        data class ShowToast(var msg: UIStr) : Intent()
        data class SetData(val email: String) : Intent()

        object Submit : Intent()
        object NavLogin : Intent()

    }

    sealed class NavAction {
        object NavLogin : NavAction()
    }
}