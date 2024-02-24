package com.cityof.glendale.screens.more

import androidx.compose.ui.graphics.painter.Painter
import com.cityof.glendale.composables.UIStr

interface MoreContract {

    data class State(
        val profileUrl: String? = null,
        val userName: String = "",
        val showPhotoPicker: Boolean = false,
        val showLogout: Boolean = false,
        var msgToast: UIStr? = null,
        var isLoading: Boolean = false,

        var isUnauthorised: Boolean = false
    )

    data class MoreItem(
        val title: String = "", val route: String = "", val id: Painter
    )

    sealed class Intent {

        object Init : Intent()
        object ProfileClicked : Intent()
        object LanguageClicked : Intent()
        object NotificationClicked : Intent()
        object ContactUsClicked : Intent()
        object LicenseClicked : Intent()
        object PrivacyPolicyClicked : Intent()
        object HelpClicked : Intent()
        object Logout : Intent()
        data class LogoutClicked(var showDialog: Boolean = false) : Intent()

        data class ShowToast(var msg: UIStr) : Intent()
        data class ProfilePicUpdate(var it: String) : Intent()
    }

    sealed class NavAction {

        object NavLogin : NavAction()
        object NavProfile : NavAction()
        object NavLanguage : NavAction()
        object NavNotification : NavAction()
        object NavContactUs : NavAction()
        object NavLicense : NavAction()
        object NavPrivacyPolicy : NavAction()
        object NavHelp : NavAction()

    }
}