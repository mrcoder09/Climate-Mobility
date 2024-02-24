package com.cityof.glendale.network.umoresponses


import androidx.compose.ui.graphics.Color
import com.cityof.glendale.R
import com.cityof.glendale.utils.fromHex
import com.google.gson.annotations.SerializedName

data class UmoRoute(
    @SerializedName("color") val color: String? = null,
    @SerializedName("hidden") val hidden: Boolean? = null,
    @SerializedName("id") val id: String? = null,
    @SerializedName("rev") val rev: Int? = null,
    @SerializedName("shortTitle") val shortTitle: String? = null,
    @SerializedName("textColor") val textColor: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("title") val title: String? = null
)

fun UmoRoute.toComposeColor(): Color? {
    return color?.let {
        Color.fromHex(it)
    }
}

fun UmoRoute.toBusMarker() = id?.let { id ->
    when (id) {
        "01" -> R.drawable.marker_bus_vibrant_orange
        "03" -> R.drawable.marker_bus_blue_deep
        "31", "32" -> R.drawable.marker_bus_cobalt_blue
        "33", "34" -> R.drawable.marker_bus_green
        "04" -> R.drawable.marker_bus_lavendar
        "05" -> R.drawable.marker_bus_vibrant_green
        "06" -> R.drawable.marker_bus_tan//Missing
        "07" -> R.drawable.marker_bus_pink_dark
        "11" -> R.drawable.marker_bus_sky_blue
        "12" -> R.drawable.marker_bus_orange
        "08" -> R.drawable.marker_bus_red
        else -> R.drawable.marker_bus_purple
    }
} ?: run {
    R.drawable.marker_bus_purple
}

