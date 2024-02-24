package com.cityof.glendale.network.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchantResponse(
    @SerializedName("customcode") val customCode: Int?,
    @SerializedName("success") val success: Boolean?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: List<Merchant>? = listOf(),
    @SerializedName("total") val total: Int?,
    @SerializedName("previousPage") val previousPage: Int?,
    @SerializedName("nextPage") val nextPage: Int?
) : Parcelable


@Parcelize
data class Merchant(
    @SerializedName("city") val city: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("id") val id: Int? = null,
    @SerializedName("item_count") val itemCount: String? = null,
    @SerializedName("merchant_image") val merchantImage: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("organisation_name") val organisationName: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("streetAddress") val streetAddress: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("zip") val zip: String? = null,
    @SerializedName("roles") val roles: String? = null
) : Parcelable


fun MerchantResponse.isSuccess() = (isSuccess(customCode) && success ?: false)

fun Merchant.completeAddress() = "$streetAddress $city, $state"
//fun Merchant.completeAddress2() = "$streetAddress $city, $state"
fun Merchant.isBeeline() = roles.isNullOrBlank().not() && roles.equals("Beeline", true)
fun Merchant.isItemPlural() = (((itemCount?.toInt() ?: 0) > 9))

