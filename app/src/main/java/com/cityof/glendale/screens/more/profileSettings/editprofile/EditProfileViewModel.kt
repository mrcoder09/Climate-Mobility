package com.cityof.glendale.screens.more.profileSettings.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.data.enums.toId
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.doIfNetwork
import com.cityof.glendale.network.responses.LoginData
import com.cityof.glendale.network.responses.School
import com.cityof.glendale.network.responses.Vehicle
import com.cityof.glendale.network.responses.getGender
import com.cityof.glendale.network.responses.isAuthorizationErr
import com.cityof.glendale.network.responses.isSuccess
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.Intent
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.NavAction
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.State
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.capitalizeWords
import com.cityof.glendale.utils.formatMillis
import com.cityof.glendale.utils.xtJson
import com.cityof.glendale.utils.xtParseDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "EditProfileViewModel"


/**
 * This is a SHARED VIEW-MODEL
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val validator: InputValidator,
    private val repository: AppRepository,
    private val preferenceManager: AppPreferenceManager
) : ViewModel() {


    private var selectGroup = ""
    private var selectVehicle = ""

    private val _state = MutableStateFlow(State())
    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }
    val state = _state.asStateFlow()

    private val _navigation = MutableSharedFlow<NavAction>()
    val navigation: Flow<NavAction> = _navigation

    fun setDropDownTitles(string: String, string1: String) {
        selectVehicle = string1
        selectGroup = string
    }


    /**
     *
     * "dateOfBirth":"09-29-2010","gender":1
     */

    fun setLoginData(loginData: LoginData?) {
        loginData?.let {

            currentState.loginData = it
            //Personal Info
            currentState.firstName = it.firstName ?: ""
            currentState.lastName = it.lastName ?: ""
            currentState.selectedDob =
                it.dateOfBirth.xtParseDate(DateFormats.DATE_FORMAT_3)?.time ?: 0
            currentState.isBiometric = it.isBiometric ?: false

            currentState.gender = it.getGender()
            val genderIndex = currentState.genders.indexOf(currentState.gender)
            currentState.genderIndex = if (genderIndex == -1) 0 else genderIndex


            //Other info
            currentState.streetAddress = it.streetAddress ?: ""
            currentState.city = it.city ?: ""
            currentState.state = it.state ?: ""
            currentState.zipCode = it.zip ?: ""
            currentState.selectedSchool = it.school
            currentState.selectedVehicle = it.vehicle
        }
    }

    fun dispatch(intent: Intent) {
        when (intent) {
            is Intent.FirstNameChanged -> updateState(
                currentState.copy(
                    firstName = StringUtils.capitalize(intent.firstName),
                    isLoading = false,
                    firstNameErr = validator.validateFirstName(intent.firstName).err,
                )
            )

            is Intent.LastNameChanged -> updateState(
                currentState.copy(
                    lastName = StringUtils.capitalize(intent.lastName),
                    isLoading = false,
                    lastNameErr = validator.validateLastName(intent.lastName).err
                )
            )

            is Intent.DateOfBirthChanged -> updateState(
                currentState.copy(
                    selectedDob = intent.dob,
                    isLoading = false,
                    dobErr = validator.validateDob(intent.dob).err
                )
            )

            is Intent.GenderChanged -> updateState(
                currentState.copy(
                    gender = intent.gender,
                    isLoading = false,
                    genderErr = validator.validateGender(intent.gender, R.string.err_gender).err
                )
            )

//            is Intent.MobileChanged -> updateState(
//                currentState.copy(
//                    mobile = intent.mobile,
//                    maskedMobile = intent.maskedMobile,
//                    isLoading = false,
//                    mobileErr = validator.validateMobile(intent.mobile).err,
//                )
//            )

            is Intent.BiometricClicked -> updateState(
                currentState.copy(
                    isLoading = false, isBiometric = intent.isBiometricForLogin
                )
            )


            Intent.NextClicked -> {
                if (validate(
                        currentState.firstName,
                        currentState.lastName,
                        currentState.selectedDob,
                        currentState.gender
                    )
                ) {
                    sendNavAction(NavAction.OtherInfo)
                }
            }

            is Intent.SetGender -> {
                updateState(
                    currentState.copy(
                        genders = intent.list,
                        gender = intent.list[0],
                    )
                )
            }

            is Intent.ShowToast -> updateState(
                currentState.copy(msgToast = intent.message)
            )

            is Intent.CityChanged -> {
                updateState(
                    currentState.copy(
                        isLoading = false,
                        city = intent.city.capitalizeWords(),
                        cityErr = validator.validateCity(
                            intent.city
                        ).err,
                        msgToast = null
                    )
                )
            }

            is Intent.GroupChanged -> {
                updateState(
                    currentState.copy(
                        isLoading = false,
                        selectedSchool = intent.group,
                        schoolIndex = intent.index,
                        msgToast = null
                    )
                )
            }

            is Intent.StateChanged -> {
                updateState(
                    currentState.copy(
                        isLoading = false, state = intent.state, msgToast = null
                    )
                )
            }

            is Intent.StreetAddressChanged -> {
                updateState(
                    currentState.copy(
                        isLoading = false,
                        streetAddress = intent.address.capitalizeWords(),
                        streetAddressErr = validator.validateStreetAddress(
                            intent.address
                        ).err,
                        msgToast = null
                    )
                )
            }

            is Intent.VehicleChanged -> {
                updateState(
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
            }

            is Intent.ZipCodeChanged -> {
                updateState(
                    currentState.copy(
                        isLoading = false,
                        zipCode = intent.zipCode,
                        zipCodeErr = validator.validZip(intent.zipCode).err,
                        msgToast = null
                    )
                )
            }

            Intent.CancelClicked -> {
                updateState(currentState.copy(isProfileUpdated = false))
                sendNavAction(NavAction.NavProfileSetting)
            }

            Intent.SaveClicked -> {
                if (validateOtherInfo(
                        currentState.streetAddress,
                        currentState.city,
                        currentState.zipCode,
                        currentState.selectedVehicle
                    )
                ) {
                    profileUpdate()
                }
            }

            is Intent.ShowCongratulations -> {
//                updateState(
//                    currentState.copy(
//                        showCongratulation = intent.show,
//                        congratulations = intent.message,
//                        msgToast = null
//                    )
//                )
//                if (intent.show.not()) {
//                    updateState(
//                        currentState.copy(
//                            isProfileUpdated = true
//                        )
//                    )
//                    sendNavAction(NavAction.NavProfileSetting)
//                }
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


    private fun validate(
        firstName: String, lastName: String, dob: Long?, gender: Gender?
    ): Boolean {
        return validator.isFormValid(
            validator.validateFirstName(firstName).let {
                updateState(
                    currentState.copy(
                        firstNameErr = it.err
                    )
                )
                it
            },
            validator.validateLastName(lastName).let {
                updateState(
                    currentState.copy(
                        lastNameErr = it.err
                    )
                )
                it
            },
            validator.validateDob(dob).let {
                updateState(
                    currentState.copy(
                        dobErr = it.err
                    )
                )
                it
            },
            validator.validateGender(
                gender, R.string.err_gender
            ).let {
                updateState(
                    currentState.copy(
                        genderErr = it.err
                    )
                )
                it
            },
//            validator.validateMobile(mobile).let {
//            updateState(
//                currentState.copy(
//                    mobileErr = it.err
//                )
//            )
//            it
//        }
        ).isValid

    }


    /***
     *
     * Method for validating OtherInformation Screen's Data
     */
    fun validateOtherInfo(
        streetAddress: String, city: String, zipCode: String, selectedVehicle: Vehicle?
    ): Boolean {
        return validator.isFormValid(validator.validateStreetAddress(
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
                            list.add(0, School(name = selectGroup))
                            list
                        }

                        val index = list.indexOf(currentState.selectedSchool)
                        updateState(
                            currentState.copy(
                                schools = list,
                                selectedSchool = list[if (index == -1) 0 else index],
                                schoolIndex = if (index == -1) 0 else index
                            )
                        )
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
                        val index = list.indexOf(currentState.selectedVehicle)
                        updateState(
                            currentState.copy(
                                vehicles = list,
                                selectedVehicle = list[if (index == -1) 0 else index],
                                vehicleIndex = if (index == -1) 0 else index
                            )
                        )
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
     * Method for updating user profile.
     */
    private fun profileUpdate() {
        doIfNetwork(noNet = {
            dispatch(Intent.ShowToast(UIStr.ResStr(R.string.err_network)))
        }) {

            viewModelScope.launch {
                val map = mutableMapOf<String, Any>()

                map["firstName"] = currentState.firstName
                map["lastName"] = currentState.lastName
                map["dateOfBirth"] =
                    currentState.selectedDob?.let { formatMillis(it, DateFormats.DATE_FORMAT_3) }
                        ?: ""
                map["gender"] = currentState.gender?.toId() ?: ""
                map["streetAddress"] = currentState.streetAddress
                map["city"] = currentState.city
                map["state"] = currentState.state
                map["zip"] = currentState.zipCode

                map["schoolId"] = currentState.selectedSchool?.id ?: ""
                map["vehicleId"] = currentState.selectedVehicle?.id ?: ""
                map["isBiometric"] = currentState.isBiometric

                Timber.d(map.xtJson())

                updateState(
                    currentState.copy(
                        isLoading = true, msgToast = null
                    )
                )

                repository.profileUpdate(map).onSuccess {


                    if (isAuthorizationErr(it.customCode)) {
                        dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_session_expired)))
                        preferenceManager.doLogout()
                        sendNavAction(NavAction.NavLogin)
                        return@launch
                    }

                    dispatch(Intent.ShowToast(UIStr.Str(it.message ?: "")))

                    if (it.isSuccess()) {
//                        dispatch(
//                            Intent.ShowToast(UIStr.ResStr(R.string.msg_profile_update_success))
//                        )
                        AppConstants.isLoadProfile = true
                        sendNavAction(NavAction.NavProfileSetting)
                    }
//                    else {
//                        dispatch(
//                            Intent.ShowToast(UIStr.Str(it.message ?: ""))
//                        )
//                    }
                }.onError {
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


}