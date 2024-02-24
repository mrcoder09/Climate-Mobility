package com.cityof.glendale.screens.languages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.screens.languages.LanguageContract.Intent
import com.cityof.glendale.screens.languages.LanguageContract.NavAction
import com.cityof.glendale.screens.languages.LanguageContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.LanguageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LanguageViewModel"

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
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

    fun setLanguages(list: List<LanguageItem>) {
        updateState(
            currentState.copy(
                list = list
            )
        )
    }


    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.LanguageSelected -> {
                val updatedList = currentState.list.map { langItem ->
                    langItem.copy(isSelected = langItem == intent.item)
                }.toMutableList()
                updateState(currentState.copy(list = updatedList))
            }

            Intent.ContinueClicked -> {
                viewModelScope.launch {
                    appPreferenceManager.setIsLanguageShown(true)
                    sendNavAction(NavAction.NavLogin)
                }

            }
        }
    }



    fun updateState(newState: State) {
        currentState = newState
    }


    fun sendNavAction(navAction: NavAction) {
        viewModelScope.launch {
            _navigation.emit(navAction)
        }
    }
}