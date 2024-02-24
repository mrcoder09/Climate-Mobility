package com.cityof.glendale.screens.feedback.myfeedback

import android.os.Parcelable
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.screens.feedback.Survey
import com.cityof.glendale.screens.feedback.SurveyOption
import com.cityof.glendale.utils.Exclude
import com.cityof.glendale.utils.ParamName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyFeedbackIn(
    val from: String? = null,
    val to: String? = null,
    val busNumber: String? = null,
    val isFromRouteTracking: Boolean = false
) : Parcelable

interface MyFeedbackContract {


    data class State(
        @Exclude val safetyQuestion: Survey = Survey(
            UIStr.ResStr(R.string.safety), listOf(
                SurveyOption(option = UIStr.ResStr(R.string.very_safe),
                    value = "Very Safe"), SurveyOption(option = UIStr.ResStr(R.string.unsafe),
                    value = "Unsafe")
            ), moreOption = listOf(
                SurveyOption(option = UIStr.ResStr(R.string.choose_the_reason),
                    value = "Choose the reason"),
                SurveyOption(option = UIStr.ResStr(R.string.unruly_behavior),
                    value = "Unruly Behavior"),
                SurveyOption(option = UIStr.ResStr(R.string.suspicious_activity),
                    value = "Suspicious Activity"),
                SurveyOption(option = UIStr.ResStr(R.string.threatening_activity),
                    value = "Threatening Activity"),
                SurveyOption(option = UIStr.ResStr(R.string.harassment),
                    value = "Harassment"),
//                SurveyOption("Someone didn’t pay fare"),
                SurveyOption(option = UIStr.ResStr(R.string.soliciting_panhandling),
                    value = "Soliciting/Panhandling")
            )
        ),
        @Exclude val servicePerformanceQuestion: Survey = Survey(
            UIStr.ResStr(R.string.service_performance), listOf(
                SurveyOption(option = UIStr.ResStr(R.string.on_time),
                    value = "On Time"),
                SurveyOption(option = UIStr.ResStr(R.string.early),
                    value = "Early"),
                SurveyOption(option = UIStr.ResStr(R.string.running_late),
                    value = "Running Late"),
                SurveyOption(option = UIStr.ResStr(R.string.missed_stop),
                    value = "Missed Stop")
            )
        ),
        @Exclude val driverConductQuestion: Survey = Survey(
            UIStr.ResStr(R.string.driver_behavior_conduct), listOf(
                SurveyOption(option = UIStr.ResStr(R.string.courteous),
                    value = "Courteous"), SurveyOption(option = UIStr.ResStr(R.string.neutral),
                    value = "Neutral"),
                SurveyOption(option = UIStr.ResStr(R.string.rude),
                    value = "Rude")
            )
        ),
        @Exclude val vehicleMaintQuestion: Survey = Survey(
            UIStr.ResStr(R.string.vehicle_maintenance), listOf(
                SurveyOption(option = UIStr.ResStr(R.string.clean),
                    value = "Clean"),
                SurveyOption(option = UIStr.ResStr(R.string.graffiti),
                    value = "Graffiti"),
                SurveyOption(option = UIStr.ResStr(R.string.no_ac),
                    value = "No AC"),
                SurveyOption(option = UIStr.ResStr(R.string.no_heat),
                    value = "No Heat"),
                SurveyOption(option = UIStr.ResStr(R.string.dirty),
                    value = "Dirty")
            ), moreOption = listOf(
//                SurveyOption("Choose the reason"),
                SurveyOption(option = UIStr.ResStr(R.string.seats),
                    value = "Seats"),
                SurveyOption(option = UIStr.ResStr(R.string.windows),
                    value = "Windows"),
                SurveyOption(option = UIStr.ResStr(R.string.floor),
                    value = "Floor"),
                SurveyOption(option = UIStr.ResStr(R.string.inside_the_bus),
                    value = "Inside the bus"),
                SurveyOption(option = UIStr.ResStr(R.string.outside_the_bus),
                    value = "Outside the bus")
            )
        ),
        @Exclude val ratingQuestion: Survey = Survey(
            UIStr.ResStr(R.string.overall_experience)
        ),
        val from: String = "",
        val to: String = "",
        @ParamName("bus_number") val busNumber: String = "",
        @ParamName("route") val route: String = "Route Information",
        @ParamName("safety") val safety: String = safetyQuestion.answers[0].value,
        @ParamName("reason") val reason: String = "",
        @ParamName("service_performance") val servicePerformance: String = servicePerformanceQuestion.answers[0].value,
        @ParamName("driver_behaviour_conduct") val driverBehaviour: String = driverConductQuestion.answers[0].value,
        @ParamName("vehicle_maintenance") val vehicleMaintenance: String = vehicleMaintQuestion.answers[0].value,
        @ParamName("rating") val rating: Float = 0f,
        @ParamName("comment") val comment: String = "",
//        @ParamName("vehicle_id") val vehicleId: Int = 1,

        @Exclude val reasonSelected: String = safetyQuestion.moreOption[0].value,
        @Exclude val isReasonForSafety: Boolean = false,
        @Exclude val reasonIndex: Int = 0,


        val dirtyReason: String = vehicleMaintQuestion.moreOption[0].value,
        @Exclude val isReasonForDirty: Boolean = false,
        @Exclude val dirtyReasonIndex: Int = 0,

        @Exclude val isFromRouteTracking: Boolean = false,
        @Exclude val isLoading: Boolean = false,
        @Exclude var toastMsg: UIStr? = null,
        @Exclude val isSuccess: Boolean = false,
        @Exclude val isAuthErr: Boolean = false
    )

    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()

        data class ShowSuccess(val show: Boolean) : Intent()

        data class SafetyChanged(val value: String) : Intent()
        data class ReasonChanged(val value: String) : Intent()
        data class ServicePerformanceChanged(val value: String) : Intent()
        data class DriverBehaviourChanged(val value: String) : Intent()
        data class VehicleMaintenanceChanged(val value: String) : Intent()

        data class DirtyReasonChanged(val value: String) : Intent()

        data class RatingChanged(val value: Float) : Intent()

        data class CommentChanged(val value: String) : Intent()

        object CreateFeedback : Intent()
        object NavFeedbackList : Intent()
    }


    sealed class NavActions {
        object NavFeedbackList : NavActions()
        object NavDashboard : NavActions()
    }
}

fun MyFeedbackContract.State.isUnSafe() = safety.equals("Unsafe", true)
fun MyFeedbackContract.State.isValidReasonForUnsafe() =
    ( SurveyOption(option = UIStr.ResStr(R.string.choose_the_reason),
        value = "Choose the reason") != safetyQuestion.moreOption[0])


