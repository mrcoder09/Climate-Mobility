package com.cityof.glendale.screens.feedback.feedbacklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.network.umoresponses.UmoVehicle
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListContract.Intent
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListContract.NavActions
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListContract.State
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
class FeedbackListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {


    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavActions>()
    val navigation: Flow<NavActions> = _navigation


    fun initUI(temp: UmoVehicle?) {
//        if (currentState.list.isEmpty())
        updateState(
            currentState.copy(
                isLoading = true, isAuthErr = false, toastMsg = null,
                vehicle = temp ?: UmoVehicle()
            )
        )
        feedbacks(temp?.id ?: "")
    }


    fun dispatch(intent: Intent) {

        when (intent) {
            is Intent.SortChange -> {
                sortList(intent.feedbackSort)
            }

            Intent.RefreshFeedbacks -> {
                updateState(
                    currentState.copy(
                        selectedSort = currentState.filters[0], filterIndex = 0
                    )
                )
                feedbacks(currentState.vehicle.id ?: "")
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(
                    toastMsg = intent.msg
                )
            )

            Intent.NavMyFeedback -> sendNavAction(NavActions.NavMyFeedback)
        }
    }


    private fun sortList(
        sort: FeedbackSort
    ) {
        Timber.d("Sort List ${sort.xtJson()}")
        val list = when (sort.type) {
            SortTypes.DATE_ASC -> currentState.list.sortedBy { it.dateTime?.toLong() }
            SortTypes.DATE_DESC -> currentState.list.sortedByDescending { it.dateTime?.toLong() }
            SortTypes.RATING_ASC -> currentState.list.sortedBy { it.rating }
            SortTypes.RATING_DESC -> currentState.list.sortedByDescending { it.rating }
            else -> currentState.list
        }

        updateState(
            currentState.copy(
                list = list, selectedSort = sort
            )
        )

    }

    fun updateState(newState: State) {
        currentState = newState
    }

    fun sendNavAction(navAction: NavActions) {
        viewModelScope.launch {
            _navigation.emit(
                navAction
            )
        }
    }


    private fun feedbacks(id: String) {
        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch {

                updateState(
                    currentState.copy(
                        toastMsg = null
                    )
                )

                appRepository.feedbackList(
                    id
                ).onSuccess { res ->
                    res.data?.let { feedbacks ->
                        updateState(
                            currentState.copy(
                                list = feedbacks,
                                listBackUp = feedbacks
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
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }


}