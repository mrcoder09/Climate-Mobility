package com.cityof.glendale.screens.changePwd

import androidx.lifecycle.ViewModel
import com.cityof.glendale.screens.changePwd.ChangePwdContract.Intent
import com.cityof.glendale.screens.changePwd.ChangePwdContract.State
import com.cityof.glendale.utils.InputValidatorImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "ChangePwdViewModel"

class ChangePwdViewModel : ViewModel() {

    val validator = InputValidatorImpl()
    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.PwdEdited -> updateState(
                currentState.copy(
                    pwd = intent.pwd,
                    isLoading = false
                )
            )

            is Intent.ConfirmPwdEdited -> updateState(
                currentState.copy(
                    confirmPwd = intent.confirmPwd,
                    isLoading = false
                )
            )

            Intent.Submit -> {
                validate(currentState.pwd, currentState.confirmPwd).let {
                    if (it) {
                        //TODO: HIT API HERE
                    }
                }
            }

            Intent.Close -> TODO()
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
    }

    private fun validate(password: String, confirmPassword: String): Boolean {


        return validator.isFormValid(
            validator.validatePassword(password).let {
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
            }
        ).isValid
    }


}