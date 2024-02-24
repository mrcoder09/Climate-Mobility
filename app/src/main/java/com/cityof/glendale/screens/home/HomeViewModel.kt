package com.cityof.glendale.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.composables.NetworkUnavailableMessage
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.home.HomeContract.Intent
import com.cityof.glendale.screens.home.HomeContract.NavAction
import com.cityof.glendale.screens.home.HomeContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.capitalizeWords
import com.cityof.glendale.utils.xt2Digit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferenceManager: AppPreferenceManager,
    private val appRepository: AppRepository
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

    init {
//        initUI()
    }

    fun initUI() {
        Timber.d("HOME VIEW MODEL: initUI")
//        if (currentState.hivePoints == HivePoints()) hivePoints()
        viewModelScope.launch {
            val isShown = preferenceManager.isHomeDialog.firstOrNull() ?: false
            dispatch(Intent.ShowDialog(isShown))

//            preferenceManager.userDetails.firstOrNull()?.let {
//                updateState(
//                    currentState.copy(
//                        userName = it.firstName?.capitalizeWords() ?: "",
//                        group = it.school?.name ?: ""
//                    )
//                )
//            }

            preferenceManager.hivePoints.collectLatest {
                it?.let { hivePoints ->
                    updateState(
                        currentState.copy(
                            hivePoint = hivePoints.availablePoints?.xt2Digit() ?: ""
                        )
                    )
                }
            }
        }
        emissionDetails("30")
        emissionDetails("")
        socialMediaPost()
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.ShowDialog -> {
                viewModelScope.launch {
                    updateState(
                        currentState.copy(
                            showDialog = intent.show
                        )
                    )
                    if (intent.show.not()) preferenceManager.setIsHomeDialog(false)
                }
            }

//            Intent.setUi -> {
//                initUI()
//            }


            Intent.LearnMoreClicked -> sendNavAction(NavAction.NavEmission)
            is Intent.LoadEmission -> emissionDetails(intent.duration)
            is Intent.ShowToast -> {}
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

    private fun emissionDetails(duration: String) {


        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true,
                        toastMsg = null
                    )
                )

                appRepository.emissionDetails(
                    duration
                ).onSuccess {
                    it.data?.let { emissionDetails->
                        val userDetail = preferenceManager.userDetails.firstOrNull()

                        if (duration.isEmpty()){
                            Timber.d("LIFETIME")
                            updateState(
                                currentState.copy(
                                    userName = userDetail?.firstName?.capitalizeWords() ?: "",
                                    group = userDetail?.school?.name ?: "",
                                    lifeTimeEmission = emissionDetails,
                                    savedTrip = emissionDetails.tripData,
                                    hivePoint = "${emissionDetails.availablePoints?.xt2Digit()}",
                                )
                            )
                        } else{
                            Timber.d("MONTHLY")
                            updateState(
                                currentState.copy(
                                    monthEmission = emissionDetails,
                                    userName = userDetail?.firstName?.capitalizeWords() ?: "",
                                    group = userDetail?.school?.name ?: "",
                                    savedTrip = emissionDetails.tripData,
                                    hivePoint = "${emissionDetails.availablePoints?.xt2Digit()}",
                                )
                            )
                        }

//                        updateState(
//                            currentState.copy(
//                                userName = userDetail?.firstName?.capitalizeWords() ?: "",
//                                group = userDetail?.school?.name ?: "",
//                                userEmission = "${emissionDetails.personalEmission?.xt2Digit()}",
//                                groupEmission = "${emissionDetails.groupEmission?.xt2Digit()}",
//                                isGroupEmission =  emissionDetails.isGroupEmission(),
//                                communityEmmision = "${emissionDetails.communityEmmision?.xt2Digit()}",
//                                hivePoint = "${emissionDetails.availablePoints?.xt2Digit()}",
//                                savedTrip = emissionDetails.tripData
//                            )
//                        )
                    }
                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
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


    fun hivePoints() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                appRepository.hivePoints().onSuccess {

                    if (it.isSuccess()) {
                        it.data?.let { hivePoints ->
                            preferenceManager.setHivePoint(hivePoints)
                        }
                    }
                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    }
                }
            }.invokeOnCompletion {

            }
        }
    }


    private fun socialMediaPost() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(NetworkUnavailableMessage())) }) {
            viewModelScope.launch {
                appRepository.socialMediaTemplate().onSuccess {
                    if (it.isSuccess()) {
                        updateState(
                            currentState.copy(
                                socialMediaTemplate = it.data
                            )
                        )
                    }
                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    }
                }
            }.invokeOnCompletion {

            }
        }
    }

}