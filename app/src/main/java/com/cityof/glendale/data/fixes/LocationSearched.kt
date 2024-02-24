package com.cityof.glendale.data.fixes

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationSearched(
    val name: String? = "",
    val address: String? = "",
    val id: String? = "",
    var latLng: LatLng? = null
) : Parcelable


fun LocationSearched.getAddressLatLng() = "${latLng?.latitude}, ${latLng?.longitude}"
