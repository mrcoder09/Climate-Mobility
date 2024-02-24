package com.cityof.glendale.screens.signup.personalDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.data.enums.toId
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.School
import com.cityof.glendale.network.responses.Vehicle
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailContract.Intent
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailContract.NavAction
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailContract.State
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.ResourceProvider
import com.cityof.glendale.utils.capitalizeWords
import com.cityof.glendale.utils.formatMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "PersonalDetailViewModel"

@HiltViewModel
class PersonalDetailViewModel @Inject constructor(
    private val validator: InputValidator,
    private val repository: AppRepository,
    private val preferenceManager: AppPreferenceManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {


    private var bundledData: Map<String, Any> = mapOf()

    private var selectGroup = ""
    private var selectVehicle = ""


    private val _state = MutableStateFlow(
        State(
            state = resourceProvider.getString(R.string.california), msgToast = null
        )
    )
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation


//    init {
//        Timber.d("on Init")
//        combine(schoolList, vehicleList) { it ->
//            val schools = it[0] as MutableList<School>
//            val vehicles = it[1] as MutableList<Vehicle>
//
//
//            Timber.d("inside combine ")
//            Timber.d("schools: $schools")
//            Timber.d("vehicles: $vehicles")
//
//            val schoolList = schools.also {
//                it.add(
//                    0, School(name = resourceProvider.getString(R.string.select_group))
//                )
//            }
//            val vehicleList = vehicles.also {
//                it.add(
//                    0, Vehicle(name = resourceProvider.getString(R.string.personal_vehicle_type))
//                )
//            }
//
////            updateState(
////                currentState.copy(
////                    schools = schoolList,
////                    selectedSchool = schoolList[0],
////                    vehicles = vehicleList,
////                    selectedVehicle = vehicleList[0]
////                )
////            )
//        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
//    }

    /**
     *
     */
    fun setDataBundle(bundledData: Map<String, Any>?) {
        bundledData?.let {
            this.bundledData = it
        }
    }

    /**
     * Method for getting Schools and Vehicles list from Beeline Server
     */
    fun schoolsAndVehicles() {

        if (currentState.schools.isEmpty() && currentState.vehicles.isEmpty()) doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {

            viewModelScope.launch(
                Dispatchers.IO
            ) {
                delay(100L)
                updateState(
                    currentState.copy(
                        isLoading = true,
                    )
                )

                repository.getSchoolList(0, 0).onSuccess {
                    it.data?.let { schools ->
                        val list = (schools as MutableList).let { list ->
                            list.add(
                                0, School(name = selectGroup)
                            )
                            list
                        }
                        updateState(
                            currentState.copy(
                                schools = list, selectedSchool = list[0]
                            )
                        )
//                        schoolList.emit(schools)
                        Timber.d("School ")
                    }
                }.onError {
                    it.message?.let { message ->
                        dispatch(Intent.ShowToast(UIStr.Str(message)))
                    }
                }

                repository.getVehicleList().onSuccess {
                    it.data?.let { vehicles ->
                        val list = (vehicles as MutableList).let { list ->
                            list.add(
                                0, Vehicle(name = selectVehicle)
                            )
                            list
                        }
                        updateState(
                            currentState.copy(
                                vehicles = list, selectedVehicle = list[0]
                            )
                        )
//                        vehicleList.emit(vehicles)
                        Timber.d("Vehicles")
                    }
                }.onError {
                    it.message?.let { message ->
                        dispatch(Intent.ShowToast(UIStr.Str(message)))
                    }
                }

            }.invokeOnCompletion {
                it?.printStackTrace()
                updateState(
                    currentState.copy(
                        isLoading = false
                    )
                )
            }
        }
    }


    /**
     * Method for handling USER's intent and other UI events
     */
    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.CityChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    city = intent.city.capitalizeWords(),
                    cityErr = validator.validateCity(
                        intent.city
                    ).err,
                    msgToast = null
                )

            )

            is Intent.DateOfBirthChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    dateOfBirth = intent.date,
                    dateOfBirthErr = validator.validateDob(intent.date).err,
                    msgToast = null
                )
            )

            is Intent.GenderChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    gender = intent.gender,
                    genderIndex = intent.index,
                    genderErr = validator.validateGender(intent.gender, R.string.err_gender).err
                )
            )

            is Intent.GroupChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    selectedSchool = intent.group,
                    schoolIndex = intent.index,
                    msgToast = null
                )
            )

            is Intent.StateChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    state = intent.state,
                    msgToast = null
                )
            )

            is Intent.StreetAddressChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    streetAddress = intent.address.capitalizeWords(),
                    streetAddressErr = validator.validateStreetAddress(
                        intent.address
                    ).err,
                    msgToast = null
                )
            )

            is Intent.VehicleChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    selectedVehicle = intent.vehicles,
                    vehicleIndex = intent.index,
                    vehiclesErr = validator.validateNull(
                        intent.vehicles.id, R.string.err_peronsal_vehicle
                    ).err,
                    msgToast = null
                )
            )

            is Intent.ZipCodeChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    zipCode = intent.zipCode,
                    zipCodeErr = validator.validZip(intent.zipCode).err,
                    msgToast = null
                )
            )

            is Intent.Cb13YearOldChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    cb13YearOld = intent.isChecked,
                    msgToast = null
                )
            )

            is Intent.CbTermConditionChanged -> updateState(
                currentState.copy(
                    isLoading = false,
                    cbTermCondition = intent.isChecked,
                    msgToast = null
                )
            )

            Intent.SubmitClicked -> {
                validate(
                    currentState.dateOfBirth,
                    currentState.gender,
                    currentState.streetAddress,
                    currentState.city,
                    currentState.zipCode,
                    currentState.selectedVehicle
                ).let {
                    if (it) {
                        val cb13Validated = validator.validateBool(
                            currentState.cb13YearOld, R.string.err_age_requirement
                        )
                        val cbTcValidated = validator.validateBool(
                            currentState.cbTermCondition, R.string.err_term_conditions
                        )

                        if (cb13Validated.isValid.not()) {
                            updateState(currentState.copy(cb13YearOldErr = cb13Validated.err))
                            dispatch(Intent.ShowToast(cb13Validated.err))
                            return
                        }

                        if (cbTcValidated.isValid.not()) {
                            updateState(currentState.copy(cbTermConditionErr = cbTcValidated.err))
                            dispatch(Intent.ShowToast(cbTcValidated.err))
                            return
                        }

                        doSignUp()
                    }
                }
            }

            is Intent.ShowCongratulations -> {
                updateState(
                    currentState.copy(
                        showCongratulation = intent.show,
                        msgToast = null
                    )
                )
                if (intent.show.not()) {
                    sendNavAction(NavAction.NavLogin)
                }
            }

            Intent.TermConditionClicked -> {
                sendNavAction(NavAction.NavTermCondition)
            }

            is Intent.ShowToast -> {
                updateState(
                    currentState.copy(msgToast = intent.message)
                )
            }

            is Intent.SetGender -> {
                updateState(
                    currentState.copy(
                        genders = intent.list,
                        gender = intent.list[0],
                        msgToast = null
                    )
                )
            }
        }
    }


    /**
     * Method for updating UI state
     */
    private fun updateState(newState: State) {
        currentState = newState
    }


    /**
     * Method for sending navigation event
     */
    private fun sendNavAction(action: NavAction) {
        viewModelScope.launch {
            _navigation.emit(action)
        }
    }


//    /**
//     * Method for checking if Submit Button can be enabled or not
//     */
//    fun submitEnabled() {
//        updateState(
//            currentState.copy(
//                isValidated = isValidated()
//            )
//        )
//    }

//    private fun isValidated(): Boolean {
//        val temp = validate(
//            date = currentState.dateOfBirth,
//            gender = currentState.gender,
//            streetAddress = currentState.streetAddress,
//            city = currentState.city,
//            zipCode = currentState.zipCode,
//            selectedVehicle = currentState.selectedVehicle
//        ) && currentState.cb13YearOld && currentState.cbTermCondition
//        return temp
//    }

    /**
     *  Method for validating entered data
     */
    private fun validate(
        date: Long?,
        gender: Gender?,
        streetAddress: String,
        city: String,
        zipCode: String,
        selectedVehicle: Vehicle?
    ): Boolean {

        return validator.isFormValid(validator.validateDob(date).let {
            updateState(
                currentState.copy(
                    dateOfBirthErr = it.err
                )
            )
            it
        }, validator.validateGender(
            gender, R.string.err_gender
        ).let {
            updateState(
                currentState.copy(
                    genderErr = it.err
                )
            )
            it
        }, validator.validateStreetAddress(
            streetAddress
        ).let {
            updateState(
                currentState.copy(
                    streetAddressErr = it.err
                )
            )
            it
        }, validator.validateCity(
            city
        ).let {
            updateState(
                currentState.copy(
                    cityErr = it.err
                )
            )
            it
        }, validator.validZip(
            zipCode
        ).let {
            updateState(
                currentState.copy(
                    zipCodeErr = it.err
                )
            )
            it
        }, validator.validateNull(
            selectedVehicle?.id, R.string.err_peronsal_vehicle
        ).let {
            updateState(
                currentState.copy(
                    vehiclesErr = it.err
                )
            )
            it
        }).isValid
    }


    /**
     * Method for doing SignUp with beeline
     */
    private fun doSignUp() {


        doIfNetwork(noNet = { dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network))) }) {
            viewModelScope.launch(Dispatchers.IO) {

                val map = mutableMapOf<String, Any>()

                for ((key, value) in bundledData) {
                    map[key] = value
                }

                map["dateOfBirth"] =
                    currentState.dateOfBirth?.let { formatMillis(it, DateFormats.DATE_FORMAT_3) }
                        ?: ""
                map["gender"] = currentState.gender?.toId() ?: ""
                map["streetAddress"] = currentState.streetAddress
                map["city"] = currentState.city
                map["state"] = currentState.state
                map["zip"] = currentState.zipCode

                map["schoolId"] = currentState.selectedSchool?.id ?: ""
                map["vehicleId"] = currentState.selectedVehicle?.id ?: ""
                map["isOver13"] = currentState.cb13YearOld
                map["acceptTerms"] = currentState.cbTermCondition

                map["deviceId"] = resourceProvider.deviceId()
                map["deviceType"] = AppConstants.ANDROID
                map["deviceName"] = resourceProvider.deviceName()

                Timber.d(map.toString())

                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )

                repository.signUp(map).onSuccess {
                    if (it.isSuccess()) {
                        dispatch(Intent.ShowCongratulations(true, UIStr.Str(it.message ?: "")))
//                        preferenceManager.reset()
                    } else {
                        it.message?.let { message ->
                            dispatch(Intent.ShowToast(UIStr.Str(message)))
                        }
                    }
                }.onError {
                    it.printStackTrace()
                    it.message?.let { message ->
                        dispatch(Intent.ShowToast(UIStr.Str(message)))
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

    fun setDropDownTitles(string: String, string1: String) {
        selectVehicle = string1
        selectGroup = string
    }


}