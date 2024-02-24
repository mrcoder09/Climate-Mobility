package com.cityof.glendale.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.DashboardContract.Intent
import com.cityof.glendale.screens.DashboardContract.NavAction
import com.cityof.glendale.screens.DashboardContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val appRepository: AppRepository, private val preferenceManager: AppPreferenceManager
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


    init {
        viewModelScope.launch {

        }
    }


    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.CheckOngoingTrip -> updateState(
                currentState.copy(
                    savedTrip = intent.trip
                )
            )

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    toastMsg = intent.msg
                )
            )

            is Intent.NavRouteTrack -> {
                sendNavAction(NavAction.NavRouteTrack(intent.trip))
            }

            Intent.ResetNav -> sendNavAction(NavAction.None)
        }
    }


    private fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
    }

    private fun emissionDetails(duration: String = "") {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null, isAuthErr = false
                    )
                )

                appRepository.emissionDetails(
                    duration
                ).onSuccess {
                    updateState(
                        currentState.copy(
                            savedTrip = it.data?.tripData
                        )
                    )
                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    }
                }
            }.invokeOnCompletion {
                updateState(
                    currentState.copy(
                        isLoading = false,
                        toastMsg = null,

                        )
                )
            }
        }
    }

}