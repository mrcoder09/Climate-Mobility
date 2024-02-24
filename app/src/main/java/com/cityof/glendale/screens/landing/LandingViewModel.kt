package com.cityof.glendale.screens.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class NavAction {
    object NavLogin : NavAction()
    object NavSignUp : NavAction()
    object None : NavAction()
}

sealed class Intent {
    object LoginClicked : Intent()
    object SignUpClicked : Intent()
}


@HiltViewModel
class LandingViewModel @Inject constructor() : ViewModel() {

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation

    fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            Intent.LoginClicked -> {
                sendNavAction(NavAction.NavLogin)
            }

            Intent.SignUpClicked -> {
                sendNavAction(NavAction.NavSignUp)
            }
        }
    }
}