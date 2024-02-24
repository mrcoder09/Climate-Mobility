package com.cityof.glendale.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.QueryParams
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isInvalidData
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.login.LoginContract.Intent
import com.cityof.glendale.screens.login.LoginContract.NavAction
import com.cityof.glendale.screens.login.LoginContract.State
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.BiometricCredentials
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.LegacyPreferences
import com.cityof.glendale.utils.asMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val validator: InputValidator,
    private val preferenceManager: AppPreferenceManager,
    private val repository: AppRepository
) : ViewModel() {


//    private val _biometricCredentials = MutableSharedFlow<BiometricCredentials>(replay = 0)
//    val biometricCredentials: Flow<BiometricCredentials> = _biometricCredentials


    private val _state = MutableStateFlow(State(msgToast = null))
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation


    private fun initUI() {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceManager.rememberDetail.firstOrNull()?.let { triple ->
                updateState(
                    currentState.copy(
                        isRememberMe = triple.third ?: currentState.isRememberMe
                    )
                )
                if (triple.third == true) {
                    updateState(
                        currentState.copy(
                            email = triple.first ?: "", password = triple.second ?: ""
                        )
                    )
                }
            }

            preferenceManager.biometricDetails.collectLatest {
                it?.let {
                    updateState(
                        currentState.copy(
                            biometricCredentials = BiometricCredentials(
                                it.first,
                                it.second,
                                it.third ?: false
                            )
                        )
                    )
                }
            }
        }
    }

    fun dispatch(intent: Intent) {

        when (intent) {
            Intent.CreateAccountClicked -> {}
            is Intent.EmailEdited -> updateState(
                currentState.copy(
                    email = intent.email,
                    emailErr = validator.validateEmail(intent.email).err,
                    isLoading = false,
                    biometricLogin = false
                )
            )

            is Intent.PwdEdited -> updateState(
                currentState.copy(
                    password = intent.pwd,
                    pwdErr = validator.validatePasswordForLogin(intent.pwd).err,
                    isLoading = false,
                    biometricLogin = false
                )
            )

            is Intent.RememberMe -> {
                viewModelScope.launch(
                    Dispatchers.IO
                ) {
                    updateState(
                        currentState.copy(
                            isLoading = false,
                            isRememberMe = intent.isRememberMe,
                            biometricLogin = false
                        )
                    )
                }
            }

            is Intent.FingerSensorClicked -> {
                AppConstants.isBiometricAfterLogout = false
                updateState(
                    currentState.copy(
                        biometricLogin = intent.isLaunch, msgToast = null, isLoading = false
                    )
                )
            }

            Intent.ForgotPwdClicked -> {
                sendNavAction(NavAction.NavForgotPassword)
                viewModelScope.launch {
                    delay(1 * 500L)
                    updateState(currentState.copy(
                        msgToast = null, isLoading = false
                    ).apply {
                        this.setEmailPwd()
                    })
                }

            }

            Intent.LoginClicked -> {
                validate(currentState.email, currentState.password).takeIf { it }?.let {
                    doLogin()
                }
            }

            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        isLoading = false, msgToast = intent.msg, biometricLogin = false
                    )
                )
            }

            Intent.SetData -> initUI()
            Intent.SignUpClicked -> {
                sendNavAction(NavAction.NavSignup)
                viewModelScope.launch {
                    delay(1 * 500L)
                    updateState(currentState.copy(
                        msgToast = null, isLoading = false
                    ).apply {
                        this.setEmailPwd()
                    })
                }
            }

            is Intent.BiometricLogin -> {
                doLogin(
                    mapOf(
                        QueryParams.EMAIL to intent.pair.first,
                        QueryParams.PASSWORD to intent.pair.second
                    )
                )
            }
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

    private fun validate(email: String, pwd: String): Boolean {
        return validator.isFormValid(validator.validateEmail(email).let {
            updateState(
                currentState.copy(
                    emailErr = it.err,
                    biometricLogin = false,
                    isLoading = false,
                )
            )
            it
        }, validator.validatePasswordForLogin(pwd).let {
            updateState(
                currentState.copy(
                    pwdErr = it.err,
                    biometricLogin = false,
                    isLoading = false,
                )
            )
            it
        }).isValid
    }


    private fun doLogin(mapIn: Map<String, Any?> = currentState.asMap()) {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {

            updateState(
                currentState.copy(
                    isLoading = true, msgToast = null
                )
            )

            val map = mapIn.toMutableMap().also {
                it["fcm_token"] = LegacyPreferences.fcmToken
            }


            viewModelScope.launch(Dispatchers.IO) {
                repository.login(
                    map
                ).onSuccess { response ->
                    if (response.isSuccess()) {
                        preferenceManager.setToken(response.token)
                        preferenceManager.setUserDetail(response.data)
                        preferenceManager.setIsLoggedIn(true)

                        preferenceManager.setIsRememberMe(currentState.isRememberMe)
                        preferenceManager.setRememberDetail(
                            "${map[QueryParams.EMAIL]}",
                            "${map[QueryParams.PASSWORD]}",
                            currentState.isRememberMe
                        )

                        preferenceManager.setBiometricDetail(
                            "${map[QueryParams.EMAIL]}",
                            "${map[QueryParams.PASSWORD]}",
                            response.data?.isBiometric ?: false
                        )

                        sendNavAction(NavAction.NavDashboard)
                    } else {

                        if (isInvalidData(response.customCode)) {
                            preferenceManager.setIsRememberMe(false)
                            preferenceManager.setRememberDetail()
                        }

                        dispatch(
                            Intent.ShowToast(
                                UIStr.Str(response.message ?: "")
                            )
                        )
                    }
                }.onError { err ->
                    err.printStackTrace()
                    dispatch(
                        Intent.ShowToast(
                            UIStr.Str(err.message ?: "")
                        )
                    )
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


//    fun biometricCredentials() {
//        Timber.d("doData")
//        viewModelScope.launch {
//            Timber.d("launch")
//            preferenceManager.biometricDetails.firstOrNull()?.let {
//                Timber.d("$it")
//                _biometricCredentials.emit(
//                    BiometricCredentials(it.first, it.second, it.third ?: false)
//                )
//            }
//        }
//    }

}