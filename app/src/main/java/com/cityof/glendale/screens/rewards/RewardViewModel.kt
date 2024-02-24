package com.cityof.glendale.screens.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.rewards.RewardContract.Intent
import com.cityof.glendale.screens.rewards.RewardContract.NavAction
import com.cityof.glendale.screens.rewards.RewardContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.DefaultPaginator
import com.cityof.glendale.utils.xtJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RewardViewModel @Inject constructor(
    private val preferenceManager: AppPreferenceManager, private val appRepository: AppRepository
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


    private val paginator =
        DefaultPaginator(page = currentState.page, onRequest = { nextPage ->
            Timber.d("PAGINATOR: onRequest : $nextPage")
            appRepository.merchantList(currentState.page, 10)
        }, onSuccess = { items, nextPage ->

            Timber.d("PAGINATOR: onSuccess : $nextPage")


            updateState(
                currentState.copy(
                    list = currentState.list + items,
                    page = nextPage,
                    isEndReached = items.isEmpty()
                )
            )
        }, onError = { throwable ->
            if (throwable is AuthorizationErr) {
                updateState(
                    currentState.copy(
                        isAuthErr = true
                    )
                )
            }
        }, onLoadUpdated = {
            Timber.d("PAGINATOR: onLoadUpdated: $it")
            updateState(
                currentState.copy(
                    isLoading = it
                )
            )
        }, getNextPage = {
            Timber.d("PAGINATOR: getNextPage: ${it.xtJson()} ${currentState.page}")
            currentState.page + 1
        })

    fun initUi() {
        if (currentState.list.isEmpty()) merchantList()
        hivePoints()
    }


    fun dispatch(intent: Intent) {
        when (intent) {
            Intent.LoadMerchant -> {
                merchantList()
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    toastMsg = intent.msg, isAuthErr = false
                )
            )

            Intent.NavMerchantDetail -> sendNavAction(NavAction.MERCHANT_DETAILS)
//            Intent.NavMerchantDetailBeeline -> sendNavAction(NavAction.MERCHANT_DETAILS_BEELINE)
        }
    }


    fun updateState(state: State) {
        currentState = state
    }

    fun sendNavAction(navAction: NavAction) {
        viewModelScope.launch {
            _navigation.emit(
                navAction
            )
        }
    }

    fun merchantList() {

        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch {
                paginator.loadItems()
            }
        }
    }

    fun hivePoints() {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {
                appRepository.hivePoints().onSuccess {

//                    if(isAuthorizationErr(it.customCode)) {
//                        updateState(
//                            currentState.copy(
//                                isAuthErr = true
//                            )
//                        )
//                        return@launch
//                    }

                    if (it.isSuccess()) {
                        it.data?.let { hivePoints ->
                            updateState(
                                currentState.copy(
                                    hivePoints = hivePoints
                                )
                            )

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

}