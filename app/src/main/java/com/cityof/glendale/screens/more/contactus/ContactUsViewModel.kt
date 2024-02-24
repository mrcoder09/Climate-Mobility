package com.cityof.glendale.screens.more.contactus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.screens.more.contactus.ContactUsContract.Intent
import com.cityof.glendale.screens.more.contactus.ContactUsContract.NavAction
import com.cityof.glendale.screens.more.contactus.ContactUsContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ContactUsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction?> = _navigation

    fun dispatch(intent: Intent) {
        when (intent) {
            Intent.CallClicked -> {
                sendNavAction(NavAction.Dialer)
            }

            Intent.EmailClicked -> {
                sendNavAction(NavAction.Email)
            }

            Intent.FormClicked -> {
                sendNavAction(NavAction.Form)
            }

            Intent.ResetNav -> sendNavAction(NavAction.None)
        }
    }

    fun updateState(newState: State) {
        currentState = newState
    }

    private fun sendNavAction(action: NavAction) {
        Timber.d("New NavAction: $action")
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }
}