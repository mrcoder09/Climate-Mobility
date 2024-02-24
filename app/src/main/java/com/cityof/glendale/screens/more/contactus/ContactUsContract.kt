package com.cityof.glendale.screens.more.contactus

import androidx.compose.ui.graphics.Color
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFFFE7B8

interface ContactUsContract {

    data class ContactUsItem(
//        var mobile: UIStr =UIStr.ResStr(R.string.suppor_mobile),
        val value :UIStr = UIStr.ResStr(R.string.support_email),
        var id: Int = R.drawable.ic_dialer_purple,
        var bgColor: Color = FFEFE8FB
    )

    data class State(
        var call: ContactUsItem = ContactUsItem(value = UIStr.ResStr(R.string.suppor_mobile),
            id= R.drawable.ic_dialer_purple, bgColor = FFEFE8FB),
        var email: ContactUsItem = ContactUsItem(value = UIStr.ResStr(R.string.support_email),
            id = R.drawable.ic_email_yellow_outlined,
            bgColor = FFFFE7B8
        )
    )

    sealed class Intent {
        object CallClicked : Intent()
        object EmailClicked : Intent()
        object FormClicked : Intent()
        object ResetNav: Intent()
    }


    sealed class NavAction {
        object Dialer : NavAction()
        object Email : NavAction()
        object Form : NavAction()
        object None: NavAction()
    }
}
