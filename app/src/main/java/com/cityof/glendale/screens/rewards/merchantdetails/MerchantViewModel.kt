package com.cityof.glendale.screens.rewards.merchantdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.screens.rewards.merchantdetails.MerchantContract.NavAction
import com.cityof.glendale.screens.rewards.merchantdetails.MerchantContract.State
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.DefaultPaginator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MerchantViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager, private val appRepository: AppRepository
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


    private val paginator = DefaultPaginator(page = currentState.page, onRequest = {
        appRepository.merchantItems(
            page = currentState.page, limit = 10, id = currentState.merchant.id ?: -1
        )
    }, onSuccess = { items, nextPage ->
        updateState(
            currentState.copy(
                list = currentState.list + items, page = nextPage, isEndReached = items.isEmpty()
            )
        )
    }, onError = {
        if (it is AuthorizationErr) {
            updateState(
                currentState.copy(
                    isAuthErr = true
                )
            )
        }
    }, onLoadUpdated = {
        updateState(
            currentState.copy(
                isLoading = it
            )
        )
    }, getNextPage = {
        currentState.page + 1
    })


    fun initUi(incomingData: Merchant?, hivePoints: HivePoints?) {
        incomingData?.let {
            updateState(
                currentState.copy(
                    merchant = it
                )
            )
        }
        hivePoints?.let {
            updateState(
                currentState.copy(
                    hivePoints = it
                )
            )
        }
        merchantItems()
    }

    fun dispatch(intent: MerchantContract.Intent) {
        when (intent) {
            is MerchantContract.Intent.ShowToast -> {
                updateState(
                    currentState.copy(
                        toastMsg = intent.msg, isAuthErr = false
                    )
                )
            }

            MerchantContract.Intent.NavRedeemDetails -> {
                sendNavAction(NavAction.NavRedeemDetail)
            }

            else -> {}
        }
    }

    fun updateState(newState: State) {
        currentState = newState
    }

    fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }


    fun merchantItems() {
        doIfNetwork(noNet = {
            dispatch(MerchantContract.Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {
            viewModelScope.launch {
                viewModelScope.launch {
                    paginator.loadItems()
                }
            }
        }
    }

}