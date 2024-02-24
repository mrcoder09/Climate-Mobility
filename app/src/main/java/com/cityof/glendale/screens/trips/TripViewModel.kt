package com.cityof.glendale.screens.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.UmoRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.trips.TripContract.Intent
import com.cityof.glendale.screens.trips.TripContract.NavAction
import com.cityof.glendale.screens.trips.TripContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val appRepository: AppRepository, private val umoRepository: UmoRepository? = null
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

    fun initUi() {
        updateState(
            currentState.copy(
                list = fakeTrips()
            )
        )
        routeList()
        savedTrips()
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            Intent.NavFare -> sendNavAction(NavAction.NavFare)
            Intent.NavRouteList -> sendNavAction(NavAction.NavRouteList)
            is Intent.ShowToast -> updateState(currentState.copy(toastMsg = intent.msg))
            is Intent.EditTrip -> sendNavAction(NavAction.NavTripPlan(intent.savedTrip))
            Intent.NavSavedTrips -> sendNavAction(NavAction.NavSavedTrips)
            is Intent.DeleteTrip -> deleteTrip(intent.tripId, intent.index)
        }
    }


    fun updateState(state: State) {
        currentState = state
    }

    fun sendNavAction(navAction: NavAction) {
        viewModelScope.launch {
            _navigation.emit(
                navAction
            )
        }
    }


    private fun routeList() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )
                umoRepository?.routeList()?.onSuccess {
                        updateState(
                            currentState.copy(
                                routes = it.take(3)
                            )
                        )
                    }?.onError {
                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    }
            }.invokeOnCompletion {
//                delay(3*1000L)
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }


    private fun savedTrips() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                appRepository.savedTrips().onSuccess {

                    it.data?.let { list ->
                        updateState(
                            currentState.copy(saveTrips = list.take(3))
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


    private fun deleteTrip(tripId: String, index: Int) {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                appRepository.deleteTrip(tripId).onSuccess {

                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    if (it.isSuccess()) {
                        delay(500L)
                        savedTrips()
//                        val savedTrips = currentState.saveTrips.toMutableList()
//                        savedTrips.removeAt(index = index)
//                        updateState(
//                            currentState.copy(
//                                saveTrips = savedTrips,
//                            )
//                        )
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


//    private fun routeList() {
//        doIfNetwork(noNet = { dispatch(TripContract.Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
//            viewModelScope.launch {
//
//                updateState(
//                    currentState.copy(
//                        isLoading = true, toastMsg = null
//                    )
//                )
//
//
//
//                appRepository.routeList().onSuccess {
//
//                    it.data?.let {
//                        updateState(
//                            currentState.copy(routes = it.take(3))
//                        )
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
//                        dispatch(TripContract.Intent.ShowToast(UIStr.Str(it.message ?: "")))
//                    }
//                }
//            }.invokeOnCompletion {
////                delay(3*1000L)
//                updateState(
//                    currentState.copy(
//                        isLoading = false
//                    )
//                )
//            }
//        }
//    }


}