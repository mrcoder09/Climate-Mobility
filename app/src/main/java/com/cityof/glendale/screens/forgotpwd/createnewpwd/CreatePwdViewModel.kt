package com.cityof.glendale.screens.forgotpwd.createnewpwd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdContract.Intent
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdContract.NavAction
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.asMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CreatePwdViewModel"

@HiltViewModel
class CreatePwdViewModel @Inject constructor(
    private val validator: InputValidator,
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


    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.PwdEdited -> updateState(
                currentState.copy(
                    password = intent.pwd,
                    pwdErr = validator.validateNewPassword(intent.pwd).err,
                    isLoading = false,
                    confirmPwdErr = if (currentState.passwordConfirmation.isEmpty()) UIStr.Str("") else validator.validateConfirmPassword2(
                        intent.pwd, currentState.passwordConfirmation
                    ).err
                )
            )

            is Intent.ConfirmPwdEdited -> updateState(
                currentState.copy(
                    passwordConfirmation = intent.confirmPwd,
                    confirmPwdErr = validator.validateConfirmPassword2(
                        currentState.password, intent.confirmPwd
                    ).err,
                    isLoading = false
                )
            )


            Intent.Submit -> {
                validate(currentState.password, currentState.passwordConfirmation).let {
                    if (it) {
                        createNewPassword()
                    }
                }
            }

            is Intent.ShowCongratulations -> {
                updateState(
                    currentState.copy(
                        isLoading = false, isCongratulations = intent.canShow
                    )
                )
            }

            Intent.NavLogin -> {
                sendNavAction(
                    NavAction.NavLogin
                )
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    msgToast = intent.msg
                )
            )

            is Intent.SetData -> updateState(
                currentState.copy(
                    email = intent.email
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

    private fun validate(password: String, confirmPassword: String): Boolean {

        return validator.isFormValid(validator.validateNewPassword(password).let {
            updateState(
                currentState.copy(
                    pwdErr = it.err
                )
            )
            it
        }, validator.validateConfirmPassword(password, confirmPassword).let {
            updateState(
                currentState.copy(
                    confirmPwdErr = it.err
                )
            )
            it
        }).isValid
    }

    private fun createNewPassword() {


        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                updateState(
                    currentState.copy(isLoading = true, msgToast = null)
                )
                appRepository.changePassword(
                    currentState.asMap()
                ).onSuccess {
                    if (it.isSuccess()) {
                        dispatch(Intent.ShowCongratulations(true))
//                        preferenceManager.reset()
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
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }

}