package com.cityof.glendale.network.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SchoolResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("data")
    val `data`: List<School>? = null,
    @SerializedName("nextPage")
    val nextPage: Any? = null,
    @SerializedName("previousPage")
    val previousPage: Any? = null,
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("total")
    val total: Int? = null
)


@Parcelize
data class School(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("type") val type: String? = null,
) : Parcelable{
    @SerializedName("address") val address: String? = null
    @SerializedName("city") val city: String? = null
    @SerializedName("fax") val fax: String? = null
    @SerializedName("phone") val phone: String? = null
    @SerializedName("state") val state: String? = null
    @SerializedName("zip") val zip: String? = null
}