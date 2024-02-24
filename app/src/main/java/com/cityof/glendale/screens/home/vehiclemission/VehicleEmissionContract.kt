package com.cityof.glendale.screens.home.vehiclemission

import com.cityof.glendale.data.fixes.VehicleEmission

interface VehicleEmissionContract {

    data class State(
        val list: List<VehicleEmission> = emptyList()
    )

    object VehicleEmissionDimen{
        const val CARD_ELEVATION = 1
        const val CARD_CONTENT_PADDING = 12
        const val SPACE_BETWEEN = 14
        const val HORIZONTAL_PADDING = 24
    }

    sealed class Intent

    sealed class NavAction
}