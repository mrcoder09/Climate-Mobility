package com.cityof.glendale.screens.trips.locationSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.GoogleEndPoints
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.onComplete
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.trips.locationSearch.LocationSearchContract.Intent
import com.cityof.glendale.screens.trips.locationSearch.LocationSearchContract.NavAction
import com.cityof.glendale.screens.trips.locationSearch.LocationSearchContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.xtJson
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LocationSearchViewModel @Inject constructor(
    private val googleApiRepository: GoogleApiRepository,
    private val preferenceManager: AppPreferenceManager
) : ViewModel() {


    var job: Job? = null
    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation


    fun recents() {
        viewModelScope.launch {
            preferenceManager.recentLocationSearches.firstOrNull()?.let {
                Timber.d(
                    it.xtJson()
                )
                updateState(
                    currentState.copy(
                        suggestionList = it,
                        title = if (it.isNotEmpty()) UIStr.ResStr(R.string.recent) else UIStr.Str("")
                    )
                )
            }
        }
    }


    fun dispatch(intent: Intent) {

        when (intent) {
            is Intent.SearchTextChanged -> {
                updateState(
                    currentState.copy(
                        input = intent.value
                    )
                )
                placeSuggestion()
            }

            is Intent.ShowToast -> {}
            is Intent.PlaceSelected -> {
//                updateState(currentState.copy(selectedPlace = intent.placePrediction))

                Timber.d("LOCATION_SEACHE_VIEW_MODEL :: ${intent.placePrediction.xtJson()}")
                updateState(currentState.copy(placeSelected = intent.placePrediction))
                placeDetails(
                    intent.placePrediction.id
                )
            }

            is Intent.SendSelectedPlace -> {
                viewModelScope.launch {
                    currentState.placeSelected.latLng = LatLng(
                        intent.place.geometry?.location?.lat ?: 0.0,
                        intent.place.geometry?.location?.lng ?: 0.0
                    )
                    preferenceManager.updateLocationSearch(currentState.placeSelected)
                    sendNavAction(NavAction.NavTripPlan(currentState.placeSelected))
                }
            }
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
    }

    fun sendNavAction(navAction: NavAction) {
        viewModelScope.launch {
            _navigation.emit(
                navAction
            )
        }
    }

    private fun placeSuggestion() {

        if (currentState.input.trim().isEmpty()) return
        doIfNetwork {
            job?.cancel()
            job = viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true
                    )
                )
                googleApiRepository.placeSuggestions(
                    mapOf(
                        "input" to currentState.input, "key" to GoogleEndPoints.GOOGLE_API_KEY
                    )
                ).onSuccess {
                    it.placePredictions?.let { list ->
                        if (list.isNotEmpty()) {
//                            updateState(
//                                currentState.copy(
//                                    suggestionList = list, title = UIStr.ResStr(R.string.suggested)
//                                )
//                            )

                            val newList = mutableListOf<LocationSearched>()
                            list.forEach { place ->
                                newList.add(
                                    LocationSearched(
                                        id = place.placeId ?: "",
                                        name = place.structuredFormatting?.mainText ?: "",
                                        address = place.description ?: "",
                                    )
                                )
                            }
                            updateState(
                                currentState.copy(
                                    suggestionList = newList,
                                    title = UIStr.ResStr(R.string.suggested)
                                )
                            )

                        } else {
                            updateState(
                                currentState.copy(
                                    title = UIStr.ResStr(R.string.no_result_found),
                                    suggestionList = emptyList()
                                )
                            )
                        }
                    }
                }.onError {

                }.onComplete {
                    updateState(
                        currentState.copy(
                            isLoading = false
                        )
                    )
                }
            }
        }
    }


    private fun placeDetails(id: String?) {

        if (id == null) return
        doIfNetwork {
            viewModelScope.launch {
//                updateState(
//                    currentState.copy(
//                        isLoading = true
//                    )
//                )
                googleApiRepository.placeDetails(
                    mapOf(
                        "placeid" to id, "key" to GoogleEndPoints.GOOGLE_API_KEY
                    )
                ).onSuccess {
                    it.placeDetails?.let {
                        dispatch(Intent.SendSelectedPlace(it))
                    }
                }.onError {

                }
            }.invokeOnCompletion {
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }
}