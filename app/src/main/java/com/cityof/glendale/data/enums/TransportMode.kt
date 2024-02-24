package com.cityof.glendale.data.enums

import androidx.annotation.DrawableRes
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr


/**
 * This class represent transport mode in whole app.
 * @author Satnam Singh
 */
enum class TravelMode {
    Bus, Walking, Bicycling
}

fun TravelMode.transitMode(): String{
    return when (this) {
        TravelMode.Bus -> "bus"
        TravelMode.Bicycling -> "bicycling"
        else -> "walking"
    }
}

fun TravelMode.directionApiTransitMode(): String{
    return when (this) {
        TravelMode.Bus -> "transit"
        TravelMode.Bicycling -> "bicycling"
        else -> "walking"
    }
}

fun TravelMode.isBeeline() = (this == TravelMode.Bus)





data class TravelModeData(
    var name: UIStr = UIStr.Str(""),
    @DrawableRes var icon: Int = R.drawable.ic_bus_purple,
    var mode: TravelMode = TravelMode.Bus,
)

fun TravelModeData.transitMode(): String {
    return when (mode) {
        TravelMode.Bus -> "transit"
        TravelMode.Bicycling -> "bicycling"
        else -> "walking"
    }
}

fun TravelModeData.directionApiMode2(): String{
    return when (mode) {
        TravelMode.Bus -> "bus"
        TravelMode.Bicycling -> "bicycling"
        else -> "walking"
    }
}

fun TravelModeData.isBeeline() = (mode == TravelMode.Bus)

fun TravelMode.getCircledIcon(): Int {
    return when(this){
        TravelMode.Bus -> R.drawable.ic_bus_purple_circled
        TravelMode.Walking -> R.drawable.ic_walk_circled_purple
        TravelMode.Bicycling -> R.drawable.ic_bicycle_purple_circled
    }
}




fun getAllTransportModes(): List<TravelModeData> {

    return mutableListOf(
        TravelModeData(
            UIStr.ResStr(R.string.mode_beeline),
            R.drawable.ic_bus_purple,
            TravelMode.Bus,
        ),
        TravelModeData(
            UIStr.ResStr(R.string.walking),
            R.drawable.ic_walking,
            TravelMode.Walking,
        ),
        TravelModeData(
            UIStr.ResStr(R.string.biking),
            R.drawable.ic_bicycle,
            TravelMode.Bicycling,
        ),
    )
}