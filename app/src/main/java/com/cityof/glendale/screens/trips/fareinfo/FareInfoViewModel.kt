package com.cityof.glendale.screens.trips.fareinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.trips.fareinfo.FareInfoContract.Intent
import com.cityof.glendale.screens.trips.fareinfo.FareInfoContract.NavAction
import com.cityof.glendale.screens.trips.fareinfo.FareInfoContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FareInfoViewModel @Inject constructor(
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
        fareInfo()
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

            Intent.TapStoreClicked -> {
                sendNavAction(NavAction.NavTapStore)
            }

            Intent.ResetNav -> sendNavAction(NavAction.NavNone)
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

    private fun fareInfo() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null
                    )
                )

                appRepository.fareList().onSuccess {

                    it.data?.let { list ->
                        updateState(
                            currentState.copy(routes = list)
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