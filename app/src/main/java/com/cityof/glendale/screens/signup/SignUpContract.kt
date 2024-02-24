package com.cityof.glendale.screens.signup

import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.utils.Exclude

interface SignUpContract {

    data class State(
        var firstName: String = "",
        var lastName: String = "",
        var email: String = "",
        var password: String = "",
        var isBiometric: Boolean = false,

        @Exclude var confirmPassword: String = "",
        @Exclude var mobile: String = "",
        @Exclude var maskedMobile: String = "",

        @Exclude var firstNameErr: UIStr = UIStr.Str(),
        @Exclude var lastNameErr: UIStr = UIStr.Str(),
        @Exclude var emailErr: UIStr = UIStr.Str(),
        @Exclude var passwordErr: UIStr = UIStr.Str(),
        @Exclude var confirmPasswordErr: UIStr = UIStr.Str(),
        @Exclude var mobileErr: UIStr = UIStr.Str(),
        @Exclude var isLoading: Boolean = false
    )

    sealed class Intent {
        data class FirstNameEdited(val firstName: String) : Intent()
        data class LastNameEdited(val lastName: String) : Intent()
        data class EmailEdited(val email: String) : Intent()
        data class PasswordEdited(val password: String) : Intent()
        data class ConfirmPasswordEdited(val confirmPassword: String) : Intent()

        //        data class MobileEdited(val mobile: String, val maskedMobile: String) : Intent()
        data class BiometricClicked(val isBiometricForLogin: Boolean) : Intent()
        object SignupClicked : Intent()
        object PersonalDetailClicked : Intent()
    }

    sealed class NavAction {
        object NavPersonalDetails : NavAction()
    }
}