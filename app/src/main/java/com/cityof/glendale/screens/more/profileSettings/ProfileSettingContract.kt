package com.cityof.glendale.screens.more.profileSettings

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.LoginData

class ProfileSettingContract {

    data class State(
        var showDeleteAccount: Boolean = false,
        var showChangePassword: Boolean = false,

        var isLoading: Boolean = false,
        var loginData: LoginData? = null,
        var msgToast: UIStr? = null,
    )


    sealed class Intent {

        data class ShowDeleteAccount(var showDialog: Boolean = false) : Intent()
        data class ShowChangePassword(var showDialog: Boolean = false) : Intent()

        data class ShowToast(var msg: UIStr) : Intent()
        object EditProfileClicked : Intent()
//        data class BioMetricClicked(var isBioMetric: Boolean = false) : Intent()
//        data class ChangePassword(var state: ChangePwdContract.State) : Intent()
        object DeleteAccount : Intent()
//        object NavLoginScreen : Intent()
        object NavLandingScreen : Intent()
    }

    sealed class NavAction {
        object NavEditProfile : NavAction()
        object NavLogin : NavAction()
        object NavLanding : NavAction()
    }
}