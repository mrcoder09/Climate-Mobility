package com.cityof.glendale.screens.forgotpwd.otpverify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyContract.Intent
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyContract.NavActions
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyContract.State
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.asMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


private const val TAG = "OtpVerifyViewModel"

@HiltViewModel
class OtpVerifyViewModel @Inject constructor(
    private val validator: InputValidator, private val appRepository: AppRepository
) : ViewModel() {


    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavActions>()
    val navigation: Flow<NavActions> = _navigation

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.OtpEdited -> {
                updateState(
                    currentState.copy(
                        otp = intent.otp, otpError = validator.validateOtp(intent.otp).err
                    )
                )
            }

            is Intent.Submit -> {
                validate(currentState.otp).let {
                    if (it) {
                        verifyOtp()
                    }
                }
            }

            Intent.Resend -> {
                forgotPassword()
            }

            is Intent.CanResend -> {
                updateState(
                    currentState.copy(
                        canResend = intent.canResend
                    )
                )
            }

            is Intent.ShowDialog -> updateState(
                currentState.copy(
                    isCongratulations = intent.show
                )
            )

            is Intent.SetEmail -> updateState(
                currentState.copy(
                    email = intent.email
                )
            )

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    msgToast = intent.message
                )
            )
        }
    }


    private fun updateState(newState: State) {
        Log.d(TAG, "updateState: $newState")
        currentState = newState
    }


    private fun sendNavAction(navActions: NavActions) {
        viewModelScope.launch {
            _navigation.emit(navActions)
        }
    }


    private fun validate(otp: String): Boolean {

        return validator.isFormValid(validator.validateOtp(otp).let {
            updateState(
                currentState.copy(
                    otpError = it.err
                )
            )
            it
        }).isValid
    }


    private fun forgotPassword() {


        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                updateState(
                    currentState.copy(
                        isLoading = true, otp = ""
                    )
                )
                appRepository.forgotPassword(
                    mapOf(
                        "email" to currentState.email
                    )
                ).onSuccess {
                    if (it.isSuccess()) {
                        dispatch(Intent.ShowDialog(show = true))
                    } else {
                        it.message?.let { msg ->
                            dispatch(Intent.ShowToast(UIStr.Str(msg)))
                        }
                    }
                }.onError {
                    it.message?.let { msg ->
                        dispatch(Intent.ShowToast(UIStr.Str(msg)))
                    }
                }
            }.invokeOnCompletion {
                Timber.d("OnCompletion")
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }


    private fun verifyOtp() {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )

                appRepository.verifyOTP(
                    currentState.asMap()
                ).onSuccess {
                    if (it.isSuccess()) {
                        sendNavAction(NavActions.NavCreateNewPassword)
                    } else {
                        it.message?.let { msg ->
                            dispatch(
                                Intent.ShowToast(UIStr.Str(msg))
                            )
                        }
                    }
                }.onError {
                    it.message?.let { msg ->
                        dispatch(Intent.ShowToast(UIStr.Str(msg)))
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