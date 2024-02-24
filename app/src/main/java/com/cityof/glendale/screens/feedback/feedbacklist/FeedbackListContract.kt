package com.cityof.glendale.screens.feedback.feedbacklist

import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.responses.Feedback
import com.cityof.glendale.network.umoresponses.UmoVehicle

interface FeedbackListContract {


    data class State(

        val list: List<Feedback> = emptyList(),
        val listBackUp: List<Feedback> = emptyList(),
        val vehicle: UmoVehicle = UmoVehicle(),
        val isAuthErr: Boolean = false,
        val isLoading: Boolean = false,


        var filters: List<FeedbackSort> = listOf(
            FeedbackSort(SortTypes.DATE_DESC, UIStr.ResStr(R.string.newest)),
            FeedbackSort(SortTypes.DATE_ASC, UIStr.ResStr(R.string.oldest)),
            FeedbackSort(SortTypes.RATING_DESC, UIStr.ResStr(R.string.highest)),
            FeedbackSort(SortTypes.RATING_ASC, UIStr.ResStr(R.string.lowest)),
        ),
        var filterIndex: Int = 0,
        var selectedSort: FeedbackSort = filters[0],
        var toastMsg: UIStr? = null
    )

    sealed class Intent {
        data class SortChange(val feedbackSort: FeedbackSort) : Intent()
        object RefreshFeedbacks : Intent()
        object NavMyFeedback : Intent()

        data class ShowToast(val msg: UIStr) : Intent()
    }


    sealed class NavActions {
        object NavMyFeedback : NavActions()
    }
}


data class FeedbackSort(
    val type: SortTypes = SortTypes.NONE, val title: UIStr = UIStr.Str("")
)

enum class SortTypes {
    NONE, DATE_ASC, DATE_DESC, RATING_ASC, RATING_DESC
}