package com.cityof.glendale.data.fixes

import com.cityof.glendale.utils.AppConstants
import com.google.gson.annotations.SerializedName


data class VehicleEmissionData(
    @SerializedName("emission_list") val emissionList: List<VehicleEmission>? = null,
    @SerializedName("Title") val title: String? = null
)


data class VehicleEmission(
    val id: Int,
    val category: String?,
    val subcategory: String?,
    val emission: String?,
    val description: String?,
    val vehicleList: List<String>?
)

fun VehicleEmission.isPickup() =
    (id == 5 && category.equals(AppConstants.PICK_UP_TRUCKS, true) && subcategory == null)

fun VehicleEmission.isPickUpSubCategory() =
    (id == 5 && category.equals(AppConstants.PICK_UP_TRUCKS, true) && subcategory != null)
fun VehicleEmission.getTitle() = if (isPickUpSubCategory()) subcategory
else category
