package com.cityof.glendale.screens.more.profileSettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.isAuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingContract.Intent
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingContract.NavAction
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val appRepository: AppRepository, private val preferenceManager: AppPreferenceManager
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
            Intent.EditProfileClicked -> sendNavAction(NavAction.NavEditProfile)

            is Intent.ShowDeleteAccount -> updateState(
                currentState.copy(
                    showDeleteAccount = intent.showDialog, isLoading = false, msgToast = null
                )
            )

            is Intent.ShowChangePassword -> updateState(
                currentState.copy(
                    showChangePassword = intent.showDialog, isLoading = false, msgToast = null
                )
            )

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    msgToast = intent.msg, isLoading = false
                )
            )

            Intent.DeleteAccount -> {
                deleteProfile()
            }

            Intent.NavLandingScreen -> sendNavAction(NavAction.NavLanding)
        }
    }


    private fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }


    fun updateState(newState: State) {
        currentState = newState
    }


    fun getUserProfile() {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(NetworkUnavailableMessage()))
        }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )
                appRepository.userProfile().onSuccess {

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    it.data?.let { loginData ->
                        updateState(
                            currentState.copy(
                                loginData = loginData
                            )
                        )
                        preferenceManager.setUserDetail(loginData)
                        preferenceManager.biometricDetails.firstOrNull()?.let { details ->
                            preferenceManager.setBiometricDetail(
                                loginData.email, details.second, loginData.isBiometric ?: false
                            )
                        }

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


    private fun deleteProfile() {

        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )
                appRepository.profileDelete().onSuccess {
                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    if (it.isSuccess()) {
                        dispatch(Intent.ShowDeleteAccount())
                        preferenceManager.deleteAccount()
                        sendNavAction(NavAction.NavLanding)
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
}