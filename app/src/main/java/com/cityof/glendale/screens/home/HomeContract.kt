package com.cityof.glendale.screens.home

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.Emission
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.SocialMediaTemplate

interface HomeContract {

    data class FakeEmission(
        val title: String?, val value: String?, val desc: String?
    )


    data class State(
        val userName: String = "",
        val userEmission: String = "",
        val group: String = "",
//        val groupEmission: String = "",
//        val isGroupEmission: Boolean = false,
//        val communityEmmision: String = "",
        val hivePoint: String = "",
        val savedTrip: SavedTrip? = null,
        val socialMediaTemplate: SocialMediaTemplate? = null,


        val lifeTimeEmission: Emission? = null,
        val monthEmission: Emission? = null,

//        val emissionList: List<FakeEmission> = mutableListOf(),
        val showDialog: Boolean = false,
        val msg: String = "",

        val isDialogShowAlready: Boolean = true,

//        var hivePoints: HivePoints = HivePoints(),

        val isLoading: Boolean = false,
        val toastMsg: UIStr? = null,
        var isAuthErr: Boolean = false
    )

    object HomeScreenDimens {
        const val CARD_ELEVATION = 1
        const val CARD_CONTENT_PADDING = 18

        const val SPACE_BETWEEN = 14
        const val SPACE_HORIZONTAL = 20
    }

    sealed class Intent {

        data class ShowDialog(val show: Boolean = false) : Intent()
//        object setUi: Intent()

        object LearnMoreClicked : Intent()

        data class ShowToast(val msg: UIStr) : Intent()
        data class LoadEmission(val duration: String) : Intent()

    }

    sealed class NavAction {
        object NavEmission : NavAction()
    }
}