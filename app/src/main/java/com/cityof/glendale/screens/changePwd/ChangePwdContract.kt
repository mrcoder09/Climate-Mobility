package com.cityof.glendale.screens.changePwd

import com.cityof.glendale.composables.UIStr

interface ChangePwdContract {


    data class State(
        var email: String = "",
        var pwd: String = "",
        var confirmPwd: String = "",
        var pwdErr: UIStr = UIStr.Str(),
        var confirmPwdErr: UIStr = UIStr.Str(),
        var isLoading: Boolean = false
    )

    sealed class Intent {
        data class PwdEdited(var pwd: String = "") : Intent()
        data class ConfirmPwdEdited(var confirmPwd: String = "") : Intent()

        object Submit : Intent()
        object Close : Intent()

    }
}