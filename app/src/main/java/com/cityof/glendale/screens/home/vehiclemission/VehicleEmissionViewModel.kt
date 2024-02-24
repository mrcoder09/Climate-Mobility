package com.cityof.glendale.screens.home.vehiclemission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.data.fixes.VehicleEmissionData
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionContract.Intent
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionContract.NavAction
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionContract.State
import com.cityof.glendale.utils.jsonFromRaw
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "VehicleEmission"

@HiltViewModel
class VehicleEmissionViewModel @Inject constructor(
    private val gson: Gson
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
        initUI()
    }

    private fun initUI() {
        viewModelScope.launch {
            jsonFromRaw(R.raw.about_emission).let {
                Timber.d(it)
                val temp = gson.fromJson(it, VehicleEmissionData::class.java)
                temp.emissionList?.let { list ->
                    updateState(
                        currentState.copy(
                            list = list
                        )
                    )
                }
            }
        }
    }

    fun dispatch(intent: Intent) {

        when (intent) {
            else -> {}
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

    //private fun validate(): Boolean {}

}