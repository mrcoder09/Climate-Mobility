package com.cityof.glendale.screens.trips.locationSearch

import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.googleresponses.PlaceDetails
import com.cityof.glendale.network.googleresponses.PlacePrediction

data class FakeLocation(
    val name: String, val address: String, val icon: Int
)




interface LocationSearchContract {
    data class State(
        val list: List<FakeLocation> = listOf(
            FakeLocation("West Windsor Road", "Glendale, CA, USA", R.drawable.ic_loc_purple),
            FakeLocation("West Windsor Township", "NJ, USA", R.drawable.ic_clock_yellow),
            FakeLocation(
                "West Windsor Community", "West Windsor Township, NJ, USA", R.drawable.ic_loc_purple
            ),
            FakeLocation(
                "West Windsor - Plainsboro",
                "Clarksville Road, Princeton Junction",
                R.drawable.ic_clock_yellow
            ),
            FakeLocation(
                "West Windsor Community", "West Windsor Township, NJ, USA", R.drawable.ic_loc_purple
            )
        ),

        val title: UIStr = UIStr.ResStr(R.string.suggested),
//        val suggestionList: List<PlacePrediction> = emptyList(),

        val suggestionList: List<LocationSearched> = emptyList(),
        val input: String = "",

        val selectedPlace: PlacePrediction = PlacePrediction(),
        val placeDetails: PlaceDetails = PlaceDetails(),

        val placeSelected: LocationSearched = LocationSearched(),
        val isLoading: Boolean = false
    )


    sealed class Intent {
        data class ShowToast(val msg: UIStr) : Intent()

        data class SearchTextChanged(val value: String) : Intent()

        //        data class PlaceSelected(val placePrediction: PlacePrediction) : Intent()
        data class PlaceSelected(val placePrediction: LocationSearched) : Intent()
        data class SendSelectedPlace(val place: PlaceDetails) : Intent()

    }


    sealed class NavAction {

        data class NavTripPlan(val placeDetails: LocationSearched) : NavAction()

    }
}
