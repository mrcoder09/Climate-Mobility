package com.cityof.glendale.screens.feedback.myfeedback

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
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackContract.Intent
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackContract.NavActions
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackContract.State
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.asMap
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
class MyFeedbackViewModel @Inject constructor(
    private val appRepository: AppRepository, private val inputValidator: InputValidator
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

    fun initUi(myFeedbackIn: MyFeedbackIn?) {
        updateState(
            currentState.copy(
                busNumber = myFeedbackIn?.busNumber ?: "",
                from = myFeedbackIn?.from ?: "",
                to = myFeedbackIn?.to ?: "",
                isFromRouteTracking = myFeedbackIn?.isFromRouteTracking ?: false
            )
        )
        AppConstants.myFeedbackIn = null
    }

    fun dispatch(intent: Intent) {

        when (intent) {
            Intent.CreateFeedback -> {

//                val safetyValidation = inputValidator.validateEmpty(
//                    currentState.safety, R.string.err_safety
//                )
//                val servicePerformanceValidation = inputValidator.validateEmpty(
//                    currentState.servicePerformance, R.string.err_service_performance
//                )
//                val driverBehaviour = inputValidator.validateEmpty(
//                    currentState.driverBehaviour, R.string.err_drive_behaviour
//                )
//                val vehicleMaintenanceValidation = inputValidator.validateEmpty(
//                    currentState.vehicleMaintenance, R.string.err_vehicle_maintenance
//                )
//
//
//                if (!safetyValidation.isValid) {
//                    dispatch(Intent.ShowToast(safetyValidation.err))
//                    return
//                }
//                if (!servicePerformanceValidation.isValid) {
//                    dispatch(Intent.ShowToast(servicePerformanceValidation.err))
//                    return
//                }
//                if (!driverBehaviour.isValid) {
//                    dispatch(Intent.ShowToast(driverBehaviour.err))
//                    return
//                }
//                if (!vehicleMaintenanceValidation.isValid) {
//                    dispatch(Intent.ShowToast(vehicleMaintenanceValidation.err))
//                    return
//                }


                if (currentState.isUnSafe() && currentState.isValidReasonForUnsafe().not()) {
                    dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_valid_reason)))
                    return
                }

                createFeedback()
            }

            Intent.NavFeedbackList -> sendNavAction(NavActions.NavFeedbackList)
            is Intent.ShowToast -> updateState(
                currentState.copy(
                    toastMsg = intent.msg
                )
            )

            is Intent.CommentChanged -> updateState(
                currentState.copy(
                    comment = intent.value
                )
            )

            is Intent.DriverBehaviourChanged -> updateState(
                currentState.copy(
                    driverBehaviour = intent.value
                )
            )

            is Intent.RatingChanged -> updateState(
                currentState.copy(
                    rating = intent.value
                )
            )

            is Intent.ReasonChanged -> updateState(
                currentState.copy(
                    reasonSelected = intent.value, reason = intent.value
                )
            )

            is Intent.SafetyChanged -> updateState(
                currentState.copy(
                    safety = intent.value, isReasonForSafety = intent.value.equals("Unsafe", false)
                )
            )

            is Intent.ServicePerformanceChanged -> updateState(
                currentState.copy(
                    servicePerformance = intent.value
                )
            )

            is Intent.VehicleMaintenanceChanged -> updateState(
                currentState.copy(
                    vehicleMaintenance = intent.value,
                    isReasonForDirty = intent.value.equals("Dirty", true),
                )
            )

            is Intent.ShowSuccess -> updateState(
                currentState.copy(
                    isSuccess = intent.show
                )
            )

            is Intent.DirtyReasonChanged -> {
                updateState(
                    currentState.copy(
                        dirtyReason = intent.value
                    )
                )
            }
        }
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

    fun validate(): Boolean {
        return inputValidator.isFormValid(inputValidator.validateEmpty(
            currentState.safety, R.string.err_safety
        ).let {
            updateState(
                currentState.copy(
                    toastMsg = it.err, isLoading = false, isAuthErr = false
                )
            )
            it
        }, inputValidator.validateEmpty(
            currentState.servicePerformance, R.string.err_service_performance
        ).let {
            updateState(
                currentState.copy(
                    toastMsg = it.err, isLoading = false, isAuthErr = false
                )
            )
            it
        }, inputValidator.validateEmpty(
            currentState.driverBehaviour, R.string.err_drive_behaviour
        ).let {
            updateState(
                currentState.copy(
                    toastMsg = it.err, isLoading = false, isAuthErr = false
                )
            )
            it
        }, inputValidator.validateEmpty(
            currentState.vehicleMaintenance, R.string.err_vehicle_maintenance
        ).let {
            updateState(
                currentState.copy(
                    toastMsg = it.err, isLoading = false, isAuthErr = false
                )
            )
            it
        }).isValid
    }


    /**
     * {
     *   "from": "Starting Point",
     *   "to": "Destination",
     *   "bus_number": "B84",
     *   "date_time": 1703272534592,
     *   "route": "Route Information",
     *   "safety": "Very Safe",
     *   "reason": "Unruly behavior",
     *   "service_performance": "On Time",
     *   "driver_behaviour_conduct": "Courteous",
     *   "vehicle_maintenance": "Clean",
     *   "rating": 3.5,
     *   "comment": "Sample comment for testing",
     *   "vehicle_id": 1
     * }
     */
    private fun createFeedback() {
        doIfNetwork {
            viewModelScope.launch {
                updateState(
                    currentState.copy(
                        isLoading = true, toastMsg = null, isAuthErr = false
                    )
                )

                val map = mutableMapOf<String, Any?>()
                val valueMap = currentState.asMap()


                for ((key, value) in valueMap) {
                    map[key] = value
                }

                if (currentState.vehicleMaintenance.equals("Dirty", true)) {
                    map["dirty_reason"] = currentState.dirtyReason
                }

                map["date_time"] = System.currentTimeMillis()

                Timber.d(map.xtJson())
                appRepository.feedbackCreate(map).onSuccess {
                    if (isSuccess(it.customCode)) {
                        dispatch(
                            intent = Intent.ShowSuccess(true)
                        )
                    } else {
                        if (currentState.isFromRouteTracking) {
                            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.server_not_available)))
                            sendNavAction(NavActions.NavDashboard)
                        } else {
                            dispatch(
                                Intent.ShowToast(UIStr.Str(it.message ?: ""))
                            )
                        }
                    }
                }.onError {
                    if (it is AuthorizationErr) {
                        updateState(
                            currentState.copy(
                                isAuthErr = true
                            )
                        )
                    } else {
                        dispatch(
                            Intent.ShowToast(UIStr.Str(it.message ?: ""))
                        )
                    }
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