package com.cityof.glendale.screens.login

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.utils.BiometricCredentials
import com.cityof.glendale.utils.Exclude


interface LoginContract {

    data class State(
        var email: String = "",
        var password: String = "",

        @Exclude var biometricLogin: Boolean = true,
        @Exclude var isLoading: Boolean = false,
        @Exclude var isRememberMe: Boolean = false,

        @Exclude var emailErr: UIStr = UIStr.Str(),
        @Exclude var pwdErr: UIStr = UIStr.Str(),

        @Exclude var msgToast: UIStr? = null,
        @Exclude var showDialog: UIStr? = null,
        @Exclude var biometricCredentials: BiometricCredentials? = null
    ){
        fun setEmailPwd(){
            if (isRememberMe.not()){
                email = ""
                password = ""
                emailErr = UIStr.Str()
                pwdErr = UIStr.Str()
            }
        }
    }


    sealed class Intent {
        data class EmailEdited(
            var email: String = ""
        ) : Intent()

        data class PwdEdited(var pwd: String = "") : Intent()

        data class RememberMe(var isRememberMe: Boolean = false) : Intent()

        data class ShowToast(
            var msg: UIStr
        ) : Intent()

        object SetData : Intent()
        object LoginClicked : Intent()
        data class BiometricLogin(var pair: Pair<String?,String?>) : Intent()
        object ForgotPwdClicked : Intent()
        object CreateAccountClicked : Intent()

        object SignUpClicked : Intent()
        data class FingerSensorClicked(var isLaunch: Boolean = false) : Intent()

    }

    sealed class NavAction {
        object NavDashboard : NavAction()
        object NavForgotPassword : NavAction()
        object NavSignup : NavAction()
    }
}