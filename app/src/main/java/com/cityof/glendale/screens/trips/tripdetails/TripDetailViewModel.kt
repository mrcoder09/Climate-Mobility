package com.cityof.glendale.screens.trips.tripdetails

import androidx.lifecycle.ViewModel
import com.cityof.glendale.network.AppRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val gson: Gson, private val appRepository: AppRepository
) : ViewModel() {

//    private val _state = MutableStateFlow(State())
//    private var currentState
//        get() = _state.value
//        set(value) {
//            _state.value = value
//        }
//    val state = _state.asStateFlow()
//
//    private val _navigation = MutableSharedFlow<NavAction>()
//    val navigation: Flow<NavAction> = _navigation
//
//
//    fun handleIncomingRoute(
//        json: String?,
//        originLoc: LocationSearched?,
//        destinationLoc: LocationSearched?,
//        tripDate: Long?,
//        tripTime: Long?
//    ) {
//        json?.let {
//            val route = gson.fromJson(it, Route::class.java)
//            Timber.d("TRIP_DETAILS: ${route.xtJson()}")
//            updateState(
//                currentState.copy(
//                    route = route, originLoc = originLoc, destinationLoc = destinationLoc
//                )
//            )
//        }
//    }
//
//    fun dispatch(intent: Intent) {
//        when (intent) {
//            Intent.SaveTrip -> saveTrip()
//            is Intent.ShowToast -> updateState(
//                currentState.copy(
//                    toastMsg = intent.msg
//                )
//            )
//        }
//    }
//
//
//    fun updateState(state: State) {
//        currentState = state
//    }
//
//    fun sendNavAction(navAction: NavAction) {
//        viewModelScope.launch {
//            _navigation.emit(
//                navAction
//            )
//        }
//    }
//
//
//    private fun saveTrip() {
//        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
//            viewModelScope.launch {
//                updateState(
//                    currentState.copy(
//                        isLoading = true, toastMsg = null
//                    )
//                )
//
//
//                val map = tripBody(
//                    route = currentState.route,
//                    origin = currentState.originLoc,
//                    destination = currentState.destinationLoc,
//                    date = currentState.tripDate,
//                    time = currentState.tripTime,
//                    travelMode = AppConstants.travelMode
//                )
//
//
//                appRepository.addTrip(map).onSuccess {
//
//                    if (it.isSuccess()) {
//
//                    }
//
//                }.onError {
//                    if (it is AuthorizationErr) {
//                        updateState(
//                            currentState.copy(
//                                isAuthErr = true
//                            )
//                        )
//                    } else {
//                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
//                    }
//                }
//            }.invokeOnCompletion {
//                updateState(
//                    currentState.copy(
//                        isLoading = false
//                    )
//                )
//            }
//        }
//    }
//
//
//    private fun editTrip() {
//        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
//            viewModelScope.launch {
//                updateState(
//                    currentState.copy(
//                        isLoading = true, toastMsg = null
//                    )
//                )
//
//
//
//                appRepository.editTrip(mapOf()).onSuccess {
//
//                    if (it.isSuccess()) {
//
//                    }
//
//                }.onError {
//                    if (it is AuthorizationErr) {
//                        updateState(
//                            currentState.copy(
//                                isAuthErr = true
//                            )
//                        )
//                    } else {
//                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
//                    }
//                }
//            }.invokeOnCompletion {
//                updateState(
//                    currentState.copy(
//                        isLoading = false
//                    )
//                )
//            }
//        }
//    }
}