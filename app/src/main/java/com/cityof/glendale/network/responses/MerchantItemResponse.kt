package com.cityof.glendale.network.responses


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchantItemResponse(
    @SerializedName("customcode")
    val customcode: Int? = null,
    @SerializedName("data")
    val `data`: List<MerchantItem>? = null,
    @SerializedName("nextPage")
    val nextPage: Int? = null,
    @SerializedName("previousPage")
    val previousPage: Int? = null,
    @SerializedName("success")
    val success: Boolean? = null,
    @SerializedName("total")
    val total: Int? = null
): Parcelable


@Parcelize
data class MerchantItem(
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("end_date")
    val endDate: String? = null,
    @SerializedName("hive_points")
    val hivePoints: Int? = null,
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("merchant_id")
    val merchantId: Int? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("product_image")
    val productImage: String? = null,
    @SerializedName("qty")
    val qty: Int? = null,
    @SerializedName("start_date")
    val startDate: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
): Parcelable


fun MerchantItemResponse.isSuccess() = (isSuccess(customcode) && success ?: false)

fun MerchantItem.canRedeem(availablePts: Double) = ((hivePoints ?: 0) <= availablePts)

