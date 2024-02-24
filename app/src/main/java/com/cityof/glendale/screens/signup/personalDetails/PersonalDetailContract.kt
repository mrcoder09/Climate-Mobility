package com.cityof.glendale.screens.signup.personalDetails

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.network.responses.School
import com.cityof.glendale.network.responses.Vehicle

interface PersonalDetailContract {


//    sealed class UiMessages {
//        data class NetworkMessage(var message: UIStr = UIStr.Str("")) : UiMessages()
//        data class PlainMessage(var message: UIStr = UIStr.Str("")) : UiMessages()
//        object None : UiMessages()
//    }

    data class State(
        var dateOfBirth: Long? = null,
        var genders: List<Gender> = emptyList(),
        var genderIndex: Int = 0,
        var gender: Gender? = null,
        var streetAddress: String = "",
        var city: String = "",
        var state: String = "",
        var zipCode: String = "",
        var cb13YearOld: Boolean = false,
        var cbTermCondition: Boolean = false,

        var schools: List<School> = emptyList(),
        var schoolIndex: Int = 0,
        var selectedSchool: School? = null,

        var vehicles: List<Vehicle> = emptyList(),
        var vehicleIndex: Int = 0,
        var selectedVehicle: Vehicle? = null,

        var dateOfBirthErr: UIStr = UIStr.Str(),
        var genderErr: UIStr = UIStr.Str(),
        var streetAddressErr: UIStr = UIStr.Str(),
        var cityErr: UIStr = UIStr.Str(),
        var stateErr: UIStr = UIStr.Str(),
        var zipCodeErr: UIStr = UIStr.Str(),
        var groupErr: String = "",
        var vehiclesErr: UIStr = UIStr.Str(),
        var cb13YearOldErr: UIStr = UIStr.Str(),
        var cbTermConditionErr: UIStr = UIStr.Str(),

        var msgToast: UIStr? = null,
        var isLoading: Boolean = false,
        var showCongratulation: Boolean = false,
//        var congratulations: UIStr = UIStr.Str()
    )


    sealed class Intent {

        data class SetGender(val list: List<Gender>) : Intent()
        data class DateOfBirthChanged(val date: Long) : Intent()
        data class GenderChanged(val index: Int = 0, val gender: Gender) : Intent()
        data class StreetAddressChanged(val address: String) : Intent()
        data class CityChanged(val city: String) : Intent()
        data class StateChanged(val state: String) : Intent()
        data class ZipCodeChanged(val zipCode: String) : Intent()
        data class GroupChanged(val index: Int = 0, val group: School) : Intent()
        data class VehicleChanged(val index: Int = 0, val vehicles: Vehicle) : Intent()

        data class Cb13YearOldChanged(val isChecked: Boolean) : Intent()
        data class CbTermConditionChanged(val isChecked: Boolean) : Intent()

        data class ShowToast(val message: UIStr) : Intent()

        data class ShowCongratulations(
            val show: Boolean = false, val message: UIStr = UIStr.Str()
        ) : Intent()

        object SubmitClicked : Intent()


        object TermConditionClicked : Intent()
    }


    sealed class NavAction {
        object NavLogin : NavAction()
        object NavTermCondition : NavAction()
    }


}