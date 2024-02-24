package com.cityof.glendale.screens.trips.routemap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.UmoRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.trips.routemap.RouteListContract.Intent
import com.cityof.glendale.screens.trips.routemap.RouteListContract.NavAction
import com.cityof.glendale.screens.trips.routemap.RouteListContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RouteListViewModel @Inject constructor(
    private val appRepository: AppRepository, private val umoRepository: UmoRepository
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

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg, isAuthErr = false
                    )
                )
            }

            is Intent.OpenRouteMap -> routeMap(intent.routeId)
            is Intent.NavWebview -> sendNavAction(NavAction.NavWebView(intent.url))
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

                umoRepository.routeList().onSuccess {

                    updateState(
                        currentState.copy(routes = it)
                    )

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


    private fun routeMap(routeId: String) {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                appRepository.routeMap(routeId).onSuccess {

                    it.data?.routeMap?.let { url ->

                        if (url.isBlank()) updateState(
                            currentState.copy(
                                toastMsg = UIStr.ResStr(R.string.map_file_not_found)
                            )
                        )
                        else sendNavAction(
                            NavAction.NavWebView(url = url)
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


//    private fun routeList() {
//        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
//            viewModelScope.launch {
//
//                updateState(
//                    currentState.copy(
//                        isLoading = true, toastMsg = null
//                    )
//                )
//
//                appRepository.routeList().onSuccess {
//
//                    it.data?.let {
//                        updateState(
//                            currentState.copy(routes = it)
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
