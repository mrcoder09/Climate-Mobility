package com.cityof.glendale.screens.trips.tripPlan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.TravelMode
import com.cityof.glendale.data.enums.directionApiMode2
import com.cityof.glendale.data.enums.isBeeline
import com.cityof.glendale.data.enums.transitMode
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.data.fixes.getAddressLatLng
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.GoogleEndPoints
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.googleresponses.filterRoutesWithBeelineAgency
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.getDate
import com.cityof.glendale.network.responses.getTime
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.network.responses.toLocationSearchedDest
import com.cityof.glendale.network.responses.toLocationSearchedOrigin
import com.cityof.glendale.network.responses.toRoute
import com.cityof.glendale.network.responses.toTravelModeData
import com.cityof.glendale.screens.trips.tripBody
import com.cityof.glendale.screens.trips.tripPlan.TripPlanContract.Intent
import com.cityof.glendale.screens.trips.tripPlan.TripPlanContract.NavAction
import com.cityof.glendale.screens.trips.tripPlan.TripPlanContract.State
import com.cityof.glendale.utils.ILocationService
import com.cityof.glendale.utils.getAddressFromLatLng
import com.cityof.glendale.utils.xtFormat
import com.cityof.glendale.utils.xtJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

/**
 *
 * This is a Shared Viewmodel between
 * Trip Plan and Trip Detail
 */

@HiltViewModel
class TripPlanViewModel @Inject constructor(
    private val googleApiRepository: GoogleApiRepository,
    private val appRepository: AppRepository,
    private val locationService: ILocationService
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation


    fun handleTripEdit(tripIn: SavedTrip) {

        tripIn.toRoute()?.let { route ->
            val origin = tripIn.toLocationSearchedOrigin()
            val destination = tripIn.toLocationSearchedDest()
            val date = tripIn.getDate() ?: System.currentTimeMillis()
            val time = tripIn.getTime() ?: System.currentTimeMillis()
            val mode = tripIn.toTravelModeData()


            updateState(
                currentState.copy(
                    route = route,
                    originLoc = origin,
                    destinationLoc = destination,
                    savedTrip = tripIn,
                    date = date,
                    time = time,
                    label = tripIn.label ?: "",
                    isEdit = true,
                    selectedTravelMode = mode
                )
            )

            getDirections()
        }
    }

    fun dispatch(intent: Intent) {
        when (intent) {

            Intent.InitCurrentLocation -> {
                viewModelScope.launch {
                    if (currentState.isTripEdit().not()) locationService.requestSingleLocation()
                        .firstOrNull()?.let {
                            val address = getAddressFromLatLng(it)
                            val temp =
                                LocationSearched(name = address, address = address, latLng = it)
                            updateState(
                                currentState.copy(
                                    originLoc = temp
                                )
                            )
                            getDirections()
                        }
                }
            }

            Intent.FetchCurrentLocation -> {
                viewModelScope.launch {
                    locationService.requestSingleLocation().firstOrNull()?.let {
                        val address = getAddressFromLatLng(it)
                        val temp = LocationSearched(name = address, address = address, latLng = it)
                        updateState(
                            currentState.copy(
                                originLoc = temp
                            )
                        )
                        getDirections()
                    }
                }
            }

            is Intent.DateChanged -> {
                updateState(
                    currentState.copy(
                        date = intent.date,
//                        showCongratulations = false
                    )
                )
                getDirections()
            }

            is Intent.TimeChanged -> {


                updateState(
                    currentState.copy(
                        time = intent.time,
//                        showCongratulations = false
                    )
                )

                getDirections()
            }

            Intent.SwitchLocations -> {

                if (currentState.originLoc != null && currentState.destinationLoc != null) {
                    val originLoc = currentState.originLoc
                    updateState(
                        currentState.copy(
                            originLoc = currentState.destinationLoc, destinationLoc = originLoc,
//                        showCongratulations = false
                        )
                    )

                    getDirections()
                }
            }

            is Intent.DestinationLocChanged -> {
                updateState(
                    currentState.copy(
                        tripSelect = TripSelect.DESTINATION_LOC, destinationLoc = intent.loc,
//                        showCongratulations = false
                    )
                )

                getDirections()
            }

            is Intent.OriginLocChanged -> {
                updateState(
                    currentState.copy(
                        tripSelect = TripSelect.ORIGIN_LOC, originLoc = intent.loc,
//                        showCongratulations = false
                    )
                )

                getDirections()
            }

            is Intent.NavLocationSearch -> {
                updateState(
                    currentState.copy(
                        tripSelect = intent.tripSelect,
//                        showCongratulations = false
                    )
                )
                sendNavAction(NavAction.NavLocationSearch)
            }

            is Intent.TravelModeChanged -> {


                if (currentState.selectedTravelMode.mode == intent.mode.mode) return

                Timber.d(
                    "CURRENT MODE: ${currentState.selectedTravelMode.mode} ${intent.mode.mode}"
                )

                updateState(
                    currentState.copy(
                        selectedTravelMode = intent.mode,
//                        showCongratulations = false
                    )
                )

                getDirections()
            }

            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg,
//                        showCongratulations = false
                    )
                )
            }

            is Intent.NavTripDetails -> {

                updateState(
                    currentState.copy(
                        route = intent.route,
//                        showCongratulations = false
                    )
                )
                sendNavAction(
                    NavAction.NavTripDetails(
                        route = intent.route
                    )
                )
            }


            //TRIP DETAIL's INTENT HERE
            is Intent.SaveTrip -> {
                saveTrip(intent.value, intent.isSaveTrip)
            }

            is Intent.EditTrip -> editTrip(intent.value)
            is Intent.ShowCongrats -> updateState(
                currentState.copy(
                    showCongratulations = intent.show
                )
            )

            is Intent.LabelChanged -> updateState(
                currentState.copy(
                    label = intent.value
                )
            )


        }
    }

    private fun getDirections() {
        if (currentState.canSearchDirection()) {
            googleDirections()
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


    /**
     * destination:Magnolia, 1501 N Victory Pl, Burbank, CA 91502, United States
     * origin:W Dryden St, Glendale, CA 91202, USA
     * mode:walking
     * key:
     * transit_mode:Walking
     * alternatives:true
     */
    private fun googleDirections() {
        doIfNetwork(noNet = {
            dispatch(
                Intent.ShowToast(NetworkUnavailableMessage())
            )
        }) {

            updateState(
                currentState.copy(
                    isLoading = true, toastMsg = null
                )
            )
            val mode = currentState.selectedTravelMode.transitMode()

            val map = mutableMapOf(
                "destination" to "${currentState.destinationLoc?.getAddressLatLng()}",
                "origin" to "${currentState.originLoc?.getAddressLatLng()}",
                "mode" to mode,
                "key" to GoogleEndPoints.GOOGLE_API_KEY,
                "alternatives" to true,
            )
            if (currentState.selectedTravelMode.mode == TravelMode.Bus) {
                map["transit_mode"] = currentState.selectedTravelMode.directionApiMode2()
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = currentState.date
                    set(Calendar.HOUR_OF_DAY, currentState.time.xtFormat("HH").toInt())
                    set(Calendar.MINUTE, currentState.time.xtFormat("mm").toInt())
                }
                map["departure_time"] = "${calendar.timeInMillis / 1000}"
            }
            Timber.d(
                map.xtJson()
            )
            viewModelScope.launch {
                googleApiRepository.directions(
                    map
                ).onSuccess { directionRes ->
                    directionRes.routes?.let { routes ->

                        Timber.d("${currentState.selectedTravelMode.mode}")
                        if (currentState.selectedTravelMode.isBeeline()) {

                            directionRes.filterRoutesWithBeelineAgency().routes?.let { list ->
                                Timber.d("BEELINE_ROUTES: ${list.xtJson()}")
                                updateState(
                                    currentState.copy(
                                        routes = list,
                                        suggestedTitle = if (list.isNotEmpty()) UIStr.ResStr(R.string.suggestions)
                                        else UIStr.Str("")
                                    )
                                )
                            }

                        } else updateState(
                            currentState.copy(
                                routes = routes,
                                suggestedTitle = if (routes.isNotEmpty()) UIStr.ResStr(R.string.suggestions)
                                else UIStr.Str("")
                            )
                        )
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


    private fun saveTrip(label: String, saveTrip: Boolean) {

        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )


                val map = tripBody(
                    route = currentState.route,
                    origin = currentState.originLoc,
                    destination = currentState.destinationLoc,
                    date = currentState.date,
                    time = currentState.time,
                    travelMode = currentState.selectedTravelMode.mode,
                    label = label
                )

                Timber.d(map.xtJson())


                appRepository.addTrip(map).onSuccess {

                    if (it.isSuccess()) {
                        if (saveTrip) updateState(currentState.copy(showCongratulations = true))
                        else {
                            sendNavAction(
                                NavAction.NavRouteTracking(it.data)
                            )
                        }
                    } else {
                        updateState(
                            currentState.copy(
                                toastMsg = UIStr.Str(it.message ?: "")
                            )
                        )
                    }

                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    } else {
                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    }
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


    private fun editTrip(value: String) {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                val map = tripBody(
                    route = currentState.route,
                    origin = currentState.originLoc,
                    destination = currentState.destinationLoc,
                    date = currentState.date,
                    time = currentState.time,
                    travelMode = currentState.selectedTravelMode.mode,
                    label = value
                ).toMutableMap()

                map["id"] = currentState.savedTrip?.id ?: ""


                Timber.d(map.xtJson())

                appRepository.editTrip(map).onSuccess {

                    if (it.isSuccess()) {
                        updateState(currentState.copy(showCongratulations = true))
                    } else {
                        updateState(
                            currentState.copy(
                                toastMsg = UIStr.Str(it.message ?: "")
                            )
                        )
                    }

                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    } else {
                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    }
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