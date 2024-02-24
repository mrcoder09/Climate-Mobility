package com.cityof.glendale.screens.trips.savedtrips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.trips.savedtrips.SavedTripContract.Intent
import com.cityof.glendale.screens.trips.savedtrips.SavedTripContract.NavAction
import com.cityof.glendale.screens.trips.savedtrips.SavedTripContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedTripViewModel @Inject constructor(
    private val appRepository: AppRepository
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
        savedTrips()
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg
                    )
                )
            }

            is Intent.DeleteTrip -> deleteTrip(intent.tripId, intent.index)
//            is Intent.EditTrip -> {
//
//            }
            is Intent.StartTrip -> {
//                intent.savedTrip.toRoute()?.let {
//                    sendNavAction(NavAction.RouteTracking(it))
//                }

                sendNavAction(NavAction.RouteTracking(
                    route = null,
                    trip = intent.savedTrip
                ))
            }

            is Intent.NavTripPlan -> {
                if (intent.isEditTrip) sendNavAction(NavAction.TripPlan(intent.savedTrip))
                else sendNavAction(NavAction.TripPlan(null))
            }

            Intent.ResetNav -> sendNavAction(NavAction.None)
        }
    }


    fun updateState(state: State) {
        currentState = state
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun sendNavAction(navAction: NavAction) {
        viewModelScope.launch {
            _navigation.emit(navAction)
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

                    it.data?.let {
                        updateState(
                            currentState.copy(list = it)
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
                        val savedTrips = currentState.list.toMutableList()
                        savedTrips.removeAt(index = index)
                        updateState(
                            currentState.copy(
                                list = savedTrips,
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