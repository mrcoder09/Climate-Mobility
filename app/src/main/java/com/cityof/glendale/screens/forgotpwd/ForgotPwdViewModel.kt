package com.cityof.glendale.screens.forgotpwd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.forgotpwd.ForgotPwdContract.Intent
import com.cityof.glendale.screens.forgotpwd.ForgotPwdContract.NavAction
import com.cityof.glendale.screens.forgotpwd.ForgotPwdContract.State
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.asMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPwdViewModel @Inject constructor(
    private val validator: InputValidator, private val appRepository: AppRepository
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


    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.EmailChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    email = intent.email,
                    emailErr = validator.validateEmail(intent.email).err,
                    isShowDialog = false,
                    msgToast = null
                )
            )

            Intent.Submit -> {

                validate(currentState.email).let {
                    if (it) forgotPassword()
                }
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    msgToast = intent.message
                )
            )
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
    }

    private fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }

    private fun validate(email: String): Boolean {

        return validator.isFormValid(validator.validateEmail(email).let {
            updateState(
                currentState.copy(
                    emailErr = it.err
                )
            )
            it
        }).isValid
    }


    private fun forgotPassword() {

        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )

                delay(100L)
                appRepository.forgotPassword(
                    currentState.asMap()
                ).onSuccess {
                    if (it.isSuccess()) {
                        sendNavAction(NavAction.NavOtpVerify)
                    } else {
                        it.message?.let { msg ->
                            updateState(
                                currentState.copy(
                                    msgToast = UIStr.Str(msg)
                                )
                            )
                        }
                    }
                }.onError {
                    it.message?.let { message ->
                        dispatch(Intent.ShowToast(UIStr.Str(message)))
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