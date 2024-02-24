package com.cityof.glendale.network.responses

import android.os.Parcelable
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.data.enums.idToGender
import com.cityof.glendale.data.enums.idToGenderType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils

@Parcelize
data class LoginResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: LoginData?
) : Parcelable

@Parcelize
data class LoginData(
    @SerializedName("city") val city: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("gender") val gender: Int? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("isBiometric") val isBiometric: Boolean? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("profile_pic") val profilePic: String? = null,
    @SerializedName("roles") val roles: String? = null,
    @SerializedName("school") val school: School? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("streetAddress") val streetAddress: String? = null,
    @SerializedName("vehicle") val vehicle: Vehicle? = null,
    @SerializedName("zip") val zip: String? = null,
    @SerializedName("service_delay") val serviceDelay: Int? = null,
    @SerializedName("detours") val detours: Int? = null,

) : Parcelable

fun LoginData.userName() =
    "${StringUtils.capitalize(firstName)} ${StringUtils.capitalize(lastName)}"

fun LoginResponse.isSuccess() = isSuccess(customCode) && success ?: false

//fun LoginData.getGenderFromId() = idToGender(gender ?: 0)

fun LoginData.getGender(): Gender {
    return Gender(
        name = idToGender(gender ?: 0), type = idToGenderType(gender ?: 0)
    )
}

fun LoginData.isDetour() = (detours == 1)

fun LoginData.isServiceDelay() = (serviceDelay == 1)