package com.cityof.glendale.screens.more.profileSettings.editprofile

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.network.responses.LoginData
import com.cityof.glendale.network.responses.School
import com.cityof.glendale.network.responses.Vehicle

interface EditProfileContract {

    data class State(

        //INCOMING DATA
        var loginData: LoginData? = null,

        //PERSONAL INFORMATION
        var firstName: String = "",
        var lastName: String = "",
        var selectedDob: Long? = null,
//        var dob: String = "",
        var genders: List<Gender> = emptyList(),
        var gender: Gender? = null,
        var genderIndex: Int = 0,
//        var mobile: String = "",
//        var maskedMobile: String = "",
        var schools: List<School> = emptyList(),
        var schoolIndex: Int = 0,
        var selectedSchool: School? = null,
        var vehicles: List<Vehicle> = emptyList(),
        var vehicleIndex: Int = 0,
        var selectedVehicle: Vehicle? = null,
        var vehiclesErr: UIStr = UIStr.Str(),
        var firstNameErr: UIStr = UIStr.Str(),
        var lastNameErr: UIStr = UIStr.Str(),
        var dobErr: UIStr = UIStr.Str(),
        var genderErr: UIStr = UIStr.Str(),
//        var mobileErr: UIStr = UIStr.Str(),
        var isBiometric: Boolean = false,


        //OTHER INFORMATION
        var streetAddress: String = "",
        var city: String = "",
        var state: String = "",
        var zipCode: String = "",
        var streetAddressErr: UIStr = UIStr.Str(),
        var cityErr: UIStr = UIStr.Str(),
        var stateErr: UIStr = UIStr.Str(),
        var zipCodeErr: UIStr = UIStr.Str(),
        var showCongratulation: Boolean = false,
        var congratulations: UIStr = UIStr.Str(),

        //COMMAN ATTRIBUTES
        var isLoading: Boolean = false,
        var msgToast: UIStr? = null,

        var isProfileUpdated: Boolean = false
    )

    sealed class Intent {

        data class SetGender(val list: List<Gender>) : Intent()

        data class FirstNameChanged(val firstName: String) : Intent()
        data class LastNameChanged(val lastName: String) : Intent()
        data class DateOfBirthChanged(val dob: Long) : Intent()
        data class GenderChanged(val index: Int, val gender: Gender) : Intent()

        //        data class MobileChanged(val mobile: String, val maskedMobile: String) : Intent()
        data class BiometricClicked(val isBiometricForLogin: Boolean) : Intent()

        data class ShowToast(val message: UIStr) : Intent()

        //        object SaveClicked : Intent()
        object NextClicked : Intent()


        //OTHER INFORMATION SCREEN
        data class StreetAddressChanged(val address: String) : Intent()
        data class CityChanged(val city: String) : Intent()
        data class StateChanged(val state: String) : Intent()
        data class ZipCodeChanged(val zipCode: String) : Intent()
        data class GroupChanged(val index: Int = 0, val group: School) : Intent()
        data class VehicleChanged(val index: Int = 0, val vehicles: Vehicle) : Intent()

        object SaveClicked : Intent()
        object CancelClicked : Intent()

        data class ShowCongratulations(
            val show: Boolean = false, val message: UIStr = UIStr.Str()
        ) : Intent()
    }

    sealed class NavAction {
        object OtherInfo : NavAction()

        object NavProfileSetting : NavAction()
        object NavLogin : NavAction()
    }
}