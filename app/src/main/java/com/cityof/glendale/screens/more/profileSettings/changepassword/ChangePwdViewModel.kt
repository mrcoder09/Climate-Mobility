package com.cityof.glendale.screens.more.profileSettings.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isAuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.more.profileSettings.changepassword.ChangePwdContract.Intent
import com.cityof.glendale.screens.more.profileSettings.changepassword.ChangePwdContract.NavAction
import com.cityof.glendale.screens.more.profileSettings.changepassword.ChangePwdContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.asMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ChangePwdViewModel @Inject constructor(
    private val inputValidator: InputValidator,
    private val appRepository: AppRepository,
    private val preferenceManager: AppPreferenceManager
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


    fun resetDialog() {
        updateState(State())
        Timber.d("reseting state")
    }

    fun dispatch(intent: Intent) {

        when (intent) {

            is Intent.CurrentPwdEdited -> updateState(
                newState = currentState.copy(
                    currentPassword = intent.currentPwd,
                    currentPwdErr = inputValidator.validateCurrentPassword(
                        intent.currentPwd
                    ).err,
                    newPasswordErr = inputValidator.validateNewPassword2(
                        currentState.password, intent.currentPwd
                    ).err
                )
            )

            is Intent.NewPasswordEdited -> updateState(
                newState = currentState.copy(
                    password = intent.newPassword,
                    newPasswordErr = inputValidator.validateNewPassword2(
                        intent.newPassword, currentState.currentPassword
                    ).err,
                    confirmPasswordErr = if (currentState.passwordConfirmation.isEmpty()) UIStr.Str("") else inputValidator.validateConfirmPassword2(
                        intent.newPassword, currentState.passwordConfirmation
                    ).err
                )
            )


            is Intent.ConfirmPwdEdited -> updateState(
                currentState.copy(
                    passwordConfirmation = intent.confirmPwd,
                    confirmPasswordErr = inputValidator.validateConfirmPassword2(
                        currentState.password,
                        intent.confirmPwd,
                    ).err,
                )
            )


            Intent.SubmitClicked -> {
                validate(
                    currentState.currentPassword,
                    currentState.password,
                    currentState.passwordConfirmation
                ).let {
                    if (it) changePassword(currentState)
                }
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    msgToast = intent.msg, isLoading = false
                )
            )
        }
    }

    fun updateState(newState: State) {
        currentState = newState
    }

    private fun sendNavAction(action: NavAction) {

        viewModelScope.launch {
            _navigation.emit(action)
        }
    }

    private fun changePassword(state: State) {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(isLoading = true, msgToast = null)
                )
                appRepository.updatePassword(state.asMap()).onSuccess {

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    if (it.isSuccess()) {
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
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

    private fun validate(
        currentPassword: String, newPassword: String, confirmPassword: String
    ): Boolean {
        return inputValidator.isFormValid(inputValidator.validateCurrentPassword(
            currentPassword
        ).let {
            updateState(
                currentState.copy(
                    currentPwdErr = it.err
                )
            )
            it
        }, inputValidator.validateNewPassword2(newPassword, currentPassword).let {
            updateState(
                currentState.copy(
                    newPasswordErr = it.err
                )
            )
            it
        }, inputValidator.validateConfirmPassword2(newPassword, confirmPassword).let {
            updateState(
                currentState.copy(
                    confirmPasswordErr = it.err
                )
            )
            it
        }).isValid
    }

}
