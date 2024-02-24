package com.cityof.glendale.network.responses


import com.cityof.glendale.utils.AppConstants
import com.google.gson.annotations.SerializedName

data class FareInfoResponse(
    @SerializedName("customcode") val customcode: Int? = null,
    @SerializedName("data") val data: List<FareInfo>? = null,
    @SerializedName("nextPage") val nextPage: Any? = null,
    @SerializedName("previousPage") val previousPage: Any? = null,
    @SerializedName("success") val success: Boolean? = null,
    @SerializedName("total") val total: Int? = null
)

data class FareInfo(
    @SerializedName("beeline_fare_media") val beelineFareMedia: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("isActivated") val isActivated: Boolean? = null,
    @SerializedName("isDeleted") val isDeleted: Boolean? = null,
    @SerializedName("regular") val regular: Double? = null,
    @SerializedName("regularUnit") val regularUnit: String? = null,
    @SerializedName("senior_disabled_medicare") val seniorDisabledMedicare: Double? = null,
    @SerializedName("senior_disabled_medicareUnit") val seniorDisabledMedicareUnit: String? = null,
    @SerializedName("student") val student: Double? = null,
    @SerializedName("studentUnit") val studentUnit: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)





fun FareInfo.getSeniorCitizenPrice(): String {
    return getPrice(seniorDisabledMedicare) { getCurrencySymbol(it) }
}

fun FareInfo.getStudentPrice(): String {
    return getPrice(student) { getCurrencySymbol(it) }
}

fun FareInfo.getRegularPrice(): String {
    return getPrice(regular) { getCurrencySymbol(it) }
}


fun isCent(symbol: String) = symbol == "¢"
fun isFree(symbol: String) = symbol.equals(AppConstants.FREE, true)


fun getPrice(price: Double?, getSymbol: (Double) -> String): String {
    val symbol = getSymbol(price ?: 0.0)
    return if (isFree(symbol)) symbol
    else if (isCent(symbol)) {
        val temp = (price ?: 0.0) * 100
        "${temp.toInt()}$symbol"
    }
    else "$symbol$price"
}

fun getCurrencySymbol(price: Double?): String {
    price?.let {
        if (it == 0.0) return "Free"
        if (it < 1.0) return "¢"
        if (it >= 1.0) return "$"
    }
    return ""
}
