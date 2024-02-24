package com.cityof.glendale.screens.more

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
import com.cityof.glendale.network.responses.userName
import com.cityof.glendale.screens.more.MoreContract.Intent
import com.cityof.glendale.screens.more.MoreContract.NavAction
import com.cityof.glendale.screens.more.MoreContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
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
            Intent.Init -> initUi()
            Intent.ProfileClicked -> sendNavAction(NavAction.NavProfile)
            Intent.LanguageClicked -> sendNavAction(NavAction.NavLanguage)
            Intent.ContactUsClicked -> sendNavAction(NavAction.NavContactUs)
            Intent.LicenseClicked -> sendNavAction(NavAction.NavLicense)
            Intent.HelpClicked -> sendNavAction(NavAction.NavHelp)
            Intent.NotificationClicked -> sendNavAction(NavAction.NavNotification)
            Intent.PrivacyPolicyClicked -> sendNavAction(NavAction.NavPrivacyPolicy)
            is Intent.LogoutClicked -> {
                updateState(
                    currentState.copy(
                        showLogout = intent.showDialog
                    )
                )
            }

            Intent.Logout -> {
                doLogout()
//                viewModelScope.launch {
//                    AppConstants.isFromLogout = true
//                    preferenceManager.doLogout()
//                    sendNavAction(NavAction.NavLogin)
//                }
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    isLoading = false, msgToast = intent.msg
                )
            )

            is Intent.ProfilePicUpdate -> {
                updateProfile(intent.it)
            }
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

    private fun initUi() {
        viewModelScope.launch(Dispatchers.IO) {
            profileDetails()
        }
    }


    private fun updateProfile(base64: String) {

//        Timber.d(
//            base64
//        )
        doIfNetwork({
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            updateState(currentState.copy(isLoading = true, msgToast = null))
            viewModelScope.launch {

                appRepository.profileUpdate(
                    mapOf("profile_pic" to base64)
                ).onSuccess {

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    if (it.isSuccess()) {
                        preferenceManager.setUserDetail(it.data)
                        profileDetails()
                    }
                }.onError {
                    it.message?.let { message ->
                        dispatch(Intent.ShowToast(UIStr.Str(message)))
                    }
                }
            }.invokeOnCompletion {
                updateState(currentState.copy(isLoading = false, msgToast = null))
            }
        }
    }


//    fun getUserProfile() {
//        doIfNetwork(noNet = {
//            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
//        }) {
//            viewModelScope.launch {
//                updateState(
//                    currentState.copy(
//                        isLoading = true, msgToast = null
//                    )
//                )
//                appRepository.userProfile().onSuccess {
//
//                    if (isAuthorizationErr(it.customCode)) {
//                        preferenceManager.setUnauthorized()
//                        sendNavAction(NavAction.NavLogin)
//                        return@launch
//                    }
//
//                    it.data?.let { loginData ->
//                        preferenceManager.setUserDetail(loginData)
//                    }
//
//                }.onError {
//                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
//                }
//            }.invokeOnCompletion {
//                updateState(
//                    currentState.copy(
//                        isLoading = false,
//                    )
//                )
//            }
//        }
//    }

    private suspend fun profileDetails() {
        preferenceManager.userDetails.firstOrNull()?.let {
            updateState(
                currentState.copy(
                    userName = it.userName(), profileUrl = it.profilePic
                )
            )
        }
    }

    private fun doLogout() {
        doIfNetwork({
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {

            updateState(currentState.copy(isLoading = true, msgToast = null))
            viewModelScope.launch {
                appRepository.logout().onSuccess {

                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    if (it.isSuccess()) {
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                    } else {
                        dispatch(
                            Intent.ShowToast(
                                UIStr.Str(it.message ?: "")
                            )
                        )
                    }

                }.onError { err ->
                    dispatch(
                        Intent.ShowToast(
                            UIStr.Str(err.message ?: "")
                        )
                    )
                }
            }.invokeOnCompletion {
                updateState(currentState.copy(isLoading = false))
            }
        }
    }

//    fun doUnAuthorised(code:Int){
//        if (isAuthorizationErr(code)) {
//            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
//            viewModelScope.launch { preferenceManager.doLogout() }
//            sendNavAction(NavAction.NavLogin)
//            return
//        }
//    }

}