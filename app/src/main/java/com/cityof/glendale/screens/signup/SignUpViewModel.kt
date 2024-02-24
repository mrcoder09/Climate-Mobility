package com.cityof.glendale.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.screens.signup.SignUpContract.Intent
import com.cityof.glendale.screens.signup.SignUpContract.NavAction
import com.cityof.glendale.screens.signup.SignUpContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.InputValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import javax.inject.Inject

private const val TAG = "SignUpViewModel"


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private var validator: InputValidator, private val preferenceManager: AppPreferenceManager
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
            is Intent.FirstNameEdited -> updateState(
                currentState.copy(
                    firstName = StringUtils.capitalize(intent.firstName),
                    firstNameErr = validator.validateFirstName(intent.firstName).err,
                    isLoading = false
                )
            )

            is Intent.LastNameEdited -> updateState(
                currentState.copy(
                    lastName = StringUtils.capitalize(intent.lastName),
                    lastNameErr = validator.validateLastName(intent.lastName).err,
                    isLoading = false
                )
            )

            is Intent.EmailEdited -> updateState(
                currentState.copy(
                    email = intent.email,
                    emailErr = validator.validateEmail(intent.email).err,
                    isLoading = false
                )
            )

            is Intent.PasswordEdited -> updateState(
                currentState.copy(
                    password = intent.password,
                    passwordErr = validator.validatePassword(intent.password).err,
                    confirmPasswordErr = if (currentState.confirmPassword.isEmpty()) UIStr.Str("") else validator.validateConfirmPassword(
                        intent.password, currentState.confirmPassword
                    ).err,
                    isLoading = false
                )
            )

            is Intent.ConfirmPasswordEdited -> updateState(
                currentState.copy(
                    confirmPassword = intent.confirmPassword,
                    confirmPasswordErr = validator.validateConfirmPassword(
                        currentState.password, intent.confirmPassword
                    ).err,
                    isLoading = false
                )
            )

//            is Intent.MobileEdited -> updateState(
//                currentState.copy(
//                    mobile = intent.mobile,
//                    maskedMobile = intent.maskedMobile,
//                    mobileErr = validator.validateMobile(intent.mobile).err,
//                    isLoading = false
//                )
//            )

            is Intent.BiometricClicked -> {
                viewModelScope.launch {
//                    preferenceManager.setIsBiometricEnabled(intent.isBiometricForLogin)
                    updateState(
                        currentState.copy(
                            isLoading = false, isBiometric = intent.isBiometricForLogin
                        )
                    )
                }
            }

            Intent.SignupClicked -> {
                if (isValidated()) {
                    sendNavAction(NavAction.NavPersonalDetails)
                }
            }

            Intent.PersonalDetailClicked -> {
                if (isValidated()) {
                    sendNavAction(NavAction.NavPersonalDetails)
                }
            }
        }
    }


//    fun isBiometricForLogin() {
//        viewModelScope.launch {
//            val isBiometricForLogin = preferenceManager.isBiometricEnabled.firstOrNull() ?: false
//            updateState(
//                currentState.copy(
//                    isBiometric = isBiometricForLogin
//                )
//            )
//        }
//    }


    private fun updateState(newState: State) {
        currentState = newState
    }

    private fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }


    private fun isValidated(): Boolean {
        return validate(
            currentState.firstName,
            currentState.lastName,
            currentState.email,
            currentState.password,
            currentState.confirmPassword
        )
    }

    private fun validate(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return validator.isFormValid(validator.validateFirstName(firstName).let {
            updateState(
                currentState.copy(
                    firstNameErr = it.err
                )
            )
            it
        }, validator.validateLastName(lastName).let {
            updateState(
                currentState.copy(
                    lastNameErr = it.err
                )
            )
            it
        }, validator.validateEmail(email).let {
            updateState(
                currentState.copy(
                    emailErr = it.err
                )
            )
            it
        }, validator.validatePassword(password).let {
            updateState(
                currentState.copy(
                    passwordErr = it.err
                )
            )
            it
        }, validator.validateConfirmPassword(password, confirmPassword).let {
            updateState(
                currentState.copy(
                    confirmPasswordErr = it.err
                )
            )
            it
        }).isValid

    }

}