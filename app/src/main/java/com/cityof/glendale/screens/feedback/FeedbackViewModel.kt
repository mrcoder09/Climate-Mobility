package com.cityof.glendale.screens.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.UmoRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.feedback.FeedbackContract.Intent
import com.cityof.glendale.screens.feedback.FeedbackContract.NavAction
import com.cityof.glendale.screens.feedback.FeedbackContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.ILocationService
import com.cityof.glendale.utils.xtJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val umoRepository: UmoRepository,
    private val appPreferenceManager: AppPreferenceManager,
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

    fun initUi() {
        routeList()
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            Timber.d("getCurrentLocation")
            locationService.requestSingleLocation().firstOrNull()?.let {
                Timber.d("CURRENT LOCATION FOUND  : ${it.xtJson()}")
                updateState(
                    currentState.copy(
                        currentLatLng = it
                    )
                )
            } ?: run {
                Timber.d("CURRENT LOCATION NOT FOUND")
            }
        }
    }

    fun is911Dialog() {
        viewModelScope.launch {
            appPreferenceManager.is911Dialog.firstOrNull()?.let {
                if (it) {
                    updateState(
                        currentState.copy(
                            is911Dialog = it
                        )
                    )
                }
            }
        }
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg,
                    )
                )
            }

            is Intent.VehicleList -> {
                updateState(
                    currentState.copy(
                        selectedRoute = intent.route
                    )
                )
                vehicleOnRoute(intent.route.id ?: "")
            }

            is Intent.NavFeedbackList -> {
                sendNavAction(NavAction.NavFeedbackList(intent.vehicle))
            }

            is Intent.Set911Visibility -> {
                viewModelScope.launch {
                    appPreferenceManager.set911Dialog(false)
                    updateState(
                        currentState.copy(
                            is911Dialog = false
                        )
                    )
                }
            }
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

                umoRepository.routeList().onSuccess {

                    updateState(
                        currentState.copy(routes = it)
                    )

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

    private fun vehicleOnRoute(routeId: String) {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                umoRepository.vehiclesOnRoute(routeId).onSuccess {

                    if (it.isEmpty()) {
                        dispatch(
                            Intent.ShowToast(UIStr.ResStr(R.string.no_vehicle_found))
                        )
//                        return@launch
                    }
                    updateState(
                        currentState.copy(vehicles = it)
                    )

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

    override fun onCleared() {
        super.onCleared()
        Timber.d("on Clear")
    }
}