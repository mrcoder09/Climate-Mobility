package com.cityof.glendale.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber

private const val TAG = "EventBus"

class EventBus {
    private val _events = MutableSharedFlow<AppEvent>()
    val events = _events.asSharedFlow()

    suspend fun emitEvent(event: AppEvent) {
        Timber.d("Emitting New Event: $event")
        _events.emit(event)
    }
}

sealed class AppEvent