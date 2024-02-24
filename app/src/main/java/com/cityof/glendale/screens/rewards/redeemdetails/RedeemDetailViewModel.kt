package com.cityof.glendale.screens.rewards.redeemdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemDetailContract.Intent
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemDetailContract.NavAction
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemDetailContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RedeemDetailViewModel @Inject constructor(
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

    fun initUi(merchant: Merchant?, item: MerchantItem?, hivepoint: HivePoints?) {
        merchant?.let {
            updateState(
                currentState.copy(
                    merchant = it, toastMsg = null, isAuthErr = false
                )
            )
        }

        item?.let {
            updateState(
                currentState.copy(
                    item = item, toastMsg = null, isAuthErr = false
                )
            )
        }

        hivepoint?.let {
            updateState(
                currentState.copy(
                    hivePoints = it, toastMsg = null, isAuthErr = false
                )
            )
        }
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.ShowToast -> updateState(
                currentState.copy(toastMsg = intent.msg)
            )

            is Intent.ShowCongratulations -> updateState(
                currentState.copy(
                    showCongratulations = intent.show,
                    isLoading = false,
                    toastMsg = null,
                    isAuthErr = false

                )
            )

            is Intent.ShowRedeemDialog -> updateState(
                currentState.copy(
                    showRedeemDialog = intent.show,
                    isLoading = false,
                    toastMsg = null,
                    isAuthErr = false

                )
            )

            is Intent.ShowRedeemDialogForPass -> updateState(
                currentState.copy(
                    showRedeemDialogForPass = intent.show,
                    isLoading = false,
                    toastMsg = null,
                    isAuthErr = false
                )
            )

            Intent.RedeemRewardClick -> {
                redeemHivePoints()
            }
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

    /**
     * {
     *   "merchant_id": 12,
     *   "merchant_items_id": 6,
     *   "merchant_items_title": "string",
     *   "point": 100,
     *   "price": 100,
     *   "qty": 1
     * }
     */
    private fun redeemHivePoints() {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {

            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        isLoading = true,
                        toastMsg = null,

                        )
                )

                appRepository.redeemHivePoints(
                    mapOf(
                        "merchant_id" to currentState.item.merchantId,
                        "merchant_items_id" to currentState.item.id,
                        "merchant_items_title" to currentState.item.title,
                        "point" to currentState.item.hivePoints,
                        "price" to currentState.item.price,
                        "qty" to currentState.item.qty
                    )
                ).onSuccess {
                    if (it.isSuccess()) {
                        dispatch(Intent.ShowCongratulations(true))
                    } else {
                        dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))
                    }
                }.onError {
                    Timber.d("redeemHivePoints")
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    } else dispatch(
                        Intent.ShowToast(UIStr.Str(it.message ?: ""))
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


}