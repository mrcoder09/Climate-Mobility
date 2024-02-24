package com.cityof.glendale.screens.more.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.isAuthorizationErr
import com.cityof.glendale.network.responses.isDetour
import com.cityof.glendale.network.responses.isServiceDelay
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.more.notifications.NotificationContract.Intent
import com.cityof.glendale.screens.more.notifications.NotificationContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    init {
        updateState(
            currentState.copy(
                detour = currentState.detour.copy(
                    title = R.string.detours, desc = R.string.desc_detours
                )
            )
        )
        getUserProfile()
    }


    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.DetoursEdited -> {
                updateState(
                    currentState.copy(
                        isLoading = false, detour = currentState.detour.copy(
                            isOn = intent.isOn
                        )
                    )
                )
                updateNotification()
            }

            is Intent.ServiceDelayEdited -> {
                updateState(
                    currentState.copy(
                        isLoading = false, serviceDelay = currentState.serviceDelay.copy(
                            isOn = intent.isOn
                        )
                    )
                )
                updateNotification()
            }

            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg
                    )
                )
            }
        }
    }

    fun updateState(newState: State) {
        currentState = newState
    }


    private fun updateNotification() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                val map = mutableMapOf(
                    "service_delay" to currentState.serviceDelay.getO1(),
                    "detours" to currentState.detour.getO1()
                )

                appRepository.updateNotification(map).onSuccess {

                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))

                    if (it.isSuccess()) {

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


    private fun getUserProfile() {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(NetworkUnavailableMessage()))
        }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )
                appRepository.userProfile().onSuccess {

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
//                        preferenceManager.doLogout()
//                        sendNavAction(NavAction.NavLogin)
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                        return@launch
                    }

                    it.data?.let { loginData ->
                        updateState(
                            currentState.copy(
                                serviceDelay = currentState.serviceDelay.copy(
                                    isOn = loginData.isServiceDelay()
                                ), detour = currentState.detour.copy(
                                    isOn = loginData.isDetour()
                                )
                            )
                        )
                    }

                }.onError {
                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                }
            }.invokeOnCompletion {
                updateState(
                    currentState.copy(
                        isLoading = false,
                    )
                )
            }
        }
    }

}