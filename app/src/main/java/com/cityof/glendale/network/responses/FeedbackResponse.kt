package com.cityof.glendale.network.responses


import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.timesAgo
import com.cityof.glendale.utils.xtFormat
import com.google.gson.annotations.SerializedName

data class FeedbackResponse(
    @SerializedName("customcode") val customcode: Int? = null,
    @SerializedName("data") val `data`: List<Feedback>? = null,
    @SerializedName("nextPage") val nextPage: Any? = null,
    @SerializedName("previousPage") val previousPage: Any? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("total") val total: Int? = null
)


data class Feedback(
    @SerializedName("bus_number") val busNumber: String? = null,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("date") val date: String? = null,
    @SerializedName("driver_behaviour_conduct") val driverBehaviourConduct: String? = null,
    @SerializedName("from") val from: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("isActivated") val isActivated: Boolean? = null,
    @SerializedName("isDeleted") val isDeleted: Boolean? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("reason") val reason: String? = null,
    @SerializedName("route") val route: String? = null,
    @SerializedName("safety") val safety: String? = null,
    @SerializedName("time") val time: String? = null,
    @SerializedName("to") val to: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("vehicle_id") val vehicleId: Int? = null,
    @SerializedName("vehicle_maintenance") val vehicleMaintenance: String? = null,
    @SerializedName("dirty_reason") val dirtyReason: String? = null,
    @SerializedName("service_performance") val servicePerformance: String? = null,
    @SerializedName("date_time") val dateTime: String? = null
)

fun Feedback.getChips(): List<String> {


    val list = mutableListOf(
        safety ?: "", reason ?: "", driverBehaviourConduct ?: "",
//        vehicleMaintenance ?: "",
        servicePerformance ?: ""
    )

    vehicleMaintenance?.let {
        if (it.isEmpty().not() && it.equals("Dirty", true)) {
            list.add(formattedDirtyReason())
        } else {
            list.add(it)
        }
    }

    return list

//    return listOf(
//        safety ?: "",
//        reason ?: "",
//        driverBehaviourConduct ?: "",
//        vehicleMaintenance ?: "",
//        servicePerformance ?: ""
//    )
}

fun Feedback.formattedDirtyReason(): String {
    return "Dirty $dirtyReason"
}

fun Feedback.timeAgo(): String {
    val temp = dateTime?.toLong()
    val timesAgo = timesAgo(temp)
    val stringBuilder = StringBuilder()
    stringBuilder.append("${temp?.xtFormat(DateFormats.DATE_FORMAT_4)}")
    if (timesAgo.isEmpty().not()) {
        stringBuilder.append(" | ")
        stringBuilder.append(timesAgo(temp))
    }
    return stringBuilder.toString()
//    return "${temp?.xtFormat(DateFormats.DATE_FORMAT_4)} | ${
//        DateUtils.getRelativeTimeSpanString(temp ?: 0)
//    } "
}