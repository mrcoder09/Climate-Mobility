package com.cityof.glendale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

sealed class SessionStatus {
    object UnAuthorized : SessionStatus()
}


class MainViewModel : ViewModel() {

    private val _unauthorizedEvent = MutableSharedFlow<SessionStatus>()
    val unauthorizedEvent: Flow<SessionStatus> = _unauthorizedEvent


    fun onUnAuthorized() {
        viewModelScope.launch {
            _unauthorizedEvent.tryEmit(SessionStatus.UnAuthorized)
        }
    }

}