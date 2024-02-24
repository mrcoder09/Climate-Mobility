package com.cityof.glendale.screens.trips.routetracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.directionApiTransitMode
import com.cityof.glendale.data.enums.isBeeline
import com.cityof.glendale.data.enums.transitMode
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.GoogleEndPoints
import com.cityof.glendale.network.UmoRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.googleresponses.getDestinationLoc
import com.cityof.glendale.network.googleresponses.getTransitRouteId
import com.cityof.glendale.network.googleresponses.toLatLng
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.TripActivity
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.isTripOngoing
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.network.responses.timeInMillis
import com.cityof.glendale.network.responses.toRoute
import com.cityof.glendale.network.responses.toTravelMode
import com.cityof.glendale.network.umoresponses.getLatLng
import com.cityof.glendale.screens.trips.routetracking.RouteTrackingContract.Intent
import com.cityof.glendale.screens.trips.routetracking.RouteTrackingContract.State
import com.cityof.glendale.utils.Event
import com.cityof.glendale.utils.ILocationService
import com.cityof.glendale.utils.isInDistance
import com.cityof.glendale.utils.xtJson
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RouteTrackingViewModel @Inject constructor(
    private val locationService: ILocationService,
    private val googleApiRepository: GoogleApiRepository,
    private val repository: AppRepository,
    private val umoRepository: UmoRepository
) : ViewModel() {


    val tripActivityEvent = Event<TripActivity?>()
    val isLoading = Event<Boolean>()

    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    fun init(trip: SavedTrip?) {
        trip?.let {
            updateState(
                currentState.copy(
                    tripIn = it, routeIn = it.toRoute(), isExitTrip = it.isTripOngoing()
                )
            )
        }
    }


    fun dispatch(intent: Intent) {
        when (intent) {

            is Intent.GetDirections -> {
                googleDirections(intent.currentLatLng)
                tripLog()
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    toastMsg = intent.msg
                )
            )

            is Intent.SetDestination -> {

            }

            Intent.ExitTrip -> {
                tripActivity(
                    RouteTrackingContract.TripStatus.COMPLETE
                )
            }

            Intent.StartTrip -> {
                tripActivity(RouteTrackingContract.TripStatus.ONGOING)
            }

            is Intent.UpdateCurrentLatLng -> {
                updateState(
                    currentState.copy(
                        previousLatLng = currentState.currentLatLng,
                        currentLatLng = intent.latLng,
                    )
                )
            }

            Intent.ShowFeedback -> {

            }

            Intent.GetBusNumber -> {

            }
        }

    }


    private fun updateState(newState: State) {
        currentState = newState
    }


    /***
     * {
     *   "trip_id": 1,
     *   "journey_id": 1,
     *   "status": "ongoing/complete(journey_id is optional it's for the complete only)",
     *   "lat": 8886686,
     *   "long": 8886686,
     *   "transport_mode": "bus"
     * }
     */
    private fun tripActivity(status: RouteTrackingContract.TripStatus) {
        doIfNetwork(noNet = {
            dispatch(
                Intent.ShowToast(NetworkUnavailableMessage())
            )
        }) {

            isLoading.postValue(true)
            updateState(
                currentState.copy(
                    isLoading = true, toastMsg = null, onExit = false
                )
            )

            val latLng = currentState.currentLatLng
            val map = mapOf(
                "trip_id" to currentState.tripIn?.id,
                "journey_id" to currentState.tripIn?.journeyId,
                "status" to status.name.lowercase(Locale.ROOT),
                "lat" to latLng?.latitude,
                "long" to latLng?.longitude,
                "transport_mode" to currentState.tripIn?.toTravelMode()?.transitMode()
            )
            Timber.d(map.xtJson())


            viewModelScope.launch {
                repository.tripActivity(map).onSuccess { res ->
                    res.isSuccess().let {
                        when (status) {
                            RouteTrackingContract.TripStatus.ONGOING -> {
                                updateState(
                                    currentState.copy(
                                        tripIn = currentState.tripIn?.copy(
                                            journeyId = "${res.journeyId}",
                                        ), isExitTrip = true
                                    )
                                )
                                dispatch(Intent.ShowToast(UIStr.Str(res.message ?: "")))
                            }

                            RouteTrackingContract.TripStatus.COMPLETE -> {

                                currentState.userRoute?.getTransitRouteId()?.let {
                                    Timber.d("$TAG $it")
                                    vehicleOnRoute(it)
                                }


                                res.data?.let { tripActivity ->
                                    tripActivityEvent.postValue(tripActivity)
                                }


                            }
                        }
                    }
                }.onError {

                }
            }.invokeOnCompletion {
                isLoading.postValue(false)
                updateState(
                    currentState.copy(
                        isLoading = false, toastMsg = null, onExit = false
                    )
                )
            }
        }
    }


    /**
     *
     * {
     *   "trip_id": 1,
     *   "journey_id": 3,
     *   "latitude": 1545855,
     *   "longitude": 55455545
     * }
     */
    private fun tripLog() {
        doIfNetwork(noNet = {
            dispatch(
                Intent.ShowToast(NetworkUnavailableMessage())
            )
        }) {

            val latLng = currentState.currentLatLng
            val map = mapOf(
                "trip_id" to currentState.tripIn?.id,
                "journey_id" to currentState.tripIn?.journeyId,
                "latitude" to latLng?.latitude,
                "longitude" to latLng?.longitude
            )

            viewModelScope.launch {
                repository.tripLog(map).onSuccess {}.onError {}
            }
        }
    }


    private fun googleDirections(currentLatLng: LatLng) {
        doIfNetwork(noNet = {
//            dispatch(
//                TripPlanContract.Intent.ShowToast(NetworkUnavailableMessage())
//            )
        }) {

            val destination = currentState.tripIn?.toRoute()?.getDestinationLoc()?.toLatLng()
//            val travelMode = currentState.tripIn?.toTravelMode()
            val mode = currentState.tripIn?.toTravelMode()
//                AppConstants.travelMode.directionApiTransitMode() // currentState.travelMode?.directionApiMode() ?: ""

            val map = mutableMapOf(
                "destination" to "${destination?.latitude}, ${destination?.longitude}",
                "origin" to "${currentLatLng.latitude}, ${currentLatLng.longitude}",
                "mode" to (mode?.directionApiTransitMode() ?: ""),
                "key" to GoogleEndPoints.GOOGLE_API_KEY
            )
            if (mode?.isBeeline() == true) {
                map["transit_mode"] = mode.transitMode()

//                val date = currentState.tripIn?.date
//                val time = currentState.tripIn?.time
//                val calendar = Calendar.getInstance().apply {
//                    if (date != null && time != null) {
//                        timeInMillis = date
//                        set(Calendar.HOUR_OF_DAY, time.xtFormat("HH").toInt())
//                        set(Calendar.MINUTE, time.xtFormat("mm").toInt())
//                    }
//                }

                val dateTime = currentState.tripIn?.timeInMillis()?.let {
                    map["departure_time"] = "$it"
                }
//                map["departure_time"] = "${calendar.timeInMillis / 1000}"
            }
//            if (AppConstants.travelMode == TravelMode.Bus) map["transit_mode"] =
//                AppConstants.travelMode.directionApiMode()
//
//            Timber.d(
//                map.xtJson()
//            )
            viewModelScope.launch {
                googleApiRepository.directions(map).onSuccess { directionRes ->
                    directionRes.routes?.let { routes ->
                        if (routes.isNotEmpty()) {
                            updateState(
                                currentState.copy(
                                    userRoute = routes[0]
                                )
                            )

                            //UNCOMMENT TO TEST TRANSIT ROUTE ID
                            currentState.userRoute?.getTransitRouteId()?.let {
                                Timber.d("$TAG $it")
                                vehicleOnRoute(it)
                            }
                        }
                    }
                }.onError {

                }
            }.invokeOnCompletion {
//                updateState(
//                    currentState.copy(
//                        isLoading = false
//                    )
//                )
            }
        }

    }


    private fun vehicleOnRoute(routeId: String) {


        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                umoRepository.vehiclesOnRoute(routeId).onSuccess { list ->

                    val id = list.filter {
                        currentState.currentLatLng?.let { latLng ->
                            it.getLatLng()?.isInDistance(latLng, 200.0)
                        } == true
                    }.getOrNull(0)?.id

                    Timber.d("$TAG BUS_NUMBER: $id")
                    id?.let {
                        updateState(
                            currentState.copy(
                                vehicleId = it
                            )
                        )
                    }

                }.onError {
                    if (it is AuthorizationErr) {

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