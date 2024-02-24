package com.cityof.glendale.utils

import android.util.Patterns
import androidx.annotation.StringRes
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.NUMBERS
import com.cityof.glendale.data.enums.Gender
import com.cityof.glendale.data.enums.toId
import org.apache.commons.lang3.StringUtils
import java.util.Calendar

private const val TAG = "InputValidator"
const val FORM_NOT_VALID = "Form is not valid"


data class ValidationResult(val isValid: Boolean, val err: UIStr)

interface InputValidator {
    fun validateBool(value: Boolean, message: Int = R.string.app_name): ValidationResult
    fun validateDob(dob: Long?): ValidationResult
    fun validateLength(
        value: String = "", message: Int = R.string.app_name, length: Int = 2
    ): ValidationResult

    fun validateOtp(value: String = ""): ValidationResult
    fun validateFirstName(firstName: String): ValidationResult
    fun validateLastName(lastName: String): ValidationResult
    fun validateEmail(email: String): ValidationResult
    fun validateEmailAndPhone(emailPhoneNumber: String): ValidationResult
    fun validatePassword(password: String): ValidationResult
    fun validateNewPassword(password: String): ValidationResult
    fun validateNewPassword2(password: String, currentPassword: String): ValidationResult
    fun validatePasswordForLogin(password: String): ValidationResult

    fun validateCurrentPassword(password: String): ValidationResult

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationResult
    fun validateConfirmPassword2(password: String, confirmPassword: String): ValidationResult
    fun validateMobile(mobile: String): ValidationResult
    fun isFormValid(vararg results: ValidationResult): ValidationResult
    fun validZip(zipCode: String): ValidationResult

    fun validateCity(city: String): ValidationResult

    fun validateStreetAddress(state: String): ValidationResult
    fun validateNull(any: Any?, message: Int = R.string.app_name): ValidationResult
    fun validateEmpty(any: String, message: Int = R.string.app_name): ValidationResult
    fun validateGender(gender: Gender?, errGender: Int): ValidationResult
}


class InputValidatorImpl : InputValidator {


    private val passwordPattern =
        "^(?=[^0-9])(?!.*\\s)(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9\\s]).{8,20}\$"

    override fun validateBool(value: Boolean, message: Int): ValidationResult {
        return ValidationResult(isValid = value, getUIStr(message))
    }


    override fun validateDob(dob: Long?): ValidationResult {
        if (dob == null) {
            return ValidationResult(isValid = false, getUIStr(R.string.err_dob))
        }
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.YEAR, -13)
        if (dob > calendar.timeInMillis) {
            return ValidationResult(isValid = false, getUIStr(R.string.err_dob13years))
        }

        return ValidationResult(isValid = true, UIStr.Str())
    }


    override fun validateLength(value: String, message: Int, length: Int): ValidationResult {
        if (value.isEmpty() || value.length < length) {
            return ValidationResult(false, getUIStr(message))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateOtp(value: String): ValidationResult {
        if (value.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_otp))
        }

        return validateLength(
            value = value, message = R.string.err_otp_length, length = 4
        )
    }

    override fun validateFirstName(firstName: String): ValidationResult {
        if (firstName.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_first_name_empty))
        }
        if (StringUtils.containsAny(firstName, NUMBERS)) {
            return ValidationResult(false, UIStr.ResStr(R.string.err_first_name))
        }
        return validateLength(
            firstName, R.string.err_first_name
        )
    }

    override fun validateLastName(lastName: String): ValidationResult {
        if (lastName.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_last_name_empty))
        }
        if (StringUtils.containsAny(lastName, NUMBERS)) {
            return ValidationResult(false, UIStr.ResStr(R.string.err_last_name))
        }
        return validateLength(
            lastName, R.string.err_last_name
        )
    }

    override fun validateEmail(email: String): ValidationResult {

        if (email.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_email_not_empty))
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(false, getUIStr(R.string.err_email))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateEmailAndPhone(emailPhoneNumber: String): ValidationResult {
        return if (emailPhoneNumber.isEmpty()) {
            return ValidationResult(false, UIStr.ResStr(R.string.err_email_phone_empty))
        } else if (emailPhoneNumber.toLongOrNull() != null) {
            validateMobile(emailPhoneNumber)
        } else {
            validateEmail(emailPhoneNumber)
        }
    }

    override fun validatePassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_pwd_not_empty))
        }
        if (password.length < 8 || !password.matches(passwordPattern.toRegex())) {
            return ValidationResult(false, getUIStr(R.string.err_password_required))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateNewPassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_new_pwd_empty))
        }
        if (password.length < 8 || !password.matches(passwordPattern.toRegex())) {
            return ValidationResult(false, getUIStr(R.string.err_new_password_required))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateNewPassword2(password: String, currentPassword: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_new_pwd_empty))
        }
        if (password.length < 8 || !password.matches(passwordPattern.toRegex())) {
            return ValidationResult(false, getUIStr(R.string.err_new_password_required))
        }
        if (password == currentPassword) {
            return ValidationResult(false, getUIStr(R.string.err_new_password_current_password))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validatePasswordForLogin(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_pwd_not_empty))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateCurrentPassword(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_current_pwd_not_empty))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateConfirmPassword(
        password: String, confirmPassword: String
    ): ValidationResult {

        if (confirmPassword.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_confirm_pwd_empty))
        }
        if (password != confirmPassword) {
            return ValidationResult(false, getUIStr(R.string.err_confirm_pwd_not_match))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateConfirmPassword2(
        password: String, confirmPassword: String
    ): ValidationResult {

        if (confirmPassword.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_confirm_pwd_empty))
        }
        if (password != confirmPassword) {
            return ValidationResult(false, getUIStr(R.string.err_confirm_pwd_match))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateMobile(mobile: String): ValidationResult {
        val mobilePattern = "\\d{10}"
        if (mobile.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_pwd_empty))
        }
        if (!mobile.matches(mobilePattern.toRegex())) {
            return ValidationResult(false, getUIStr(R.string.err_phone_required))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun isFormValid(vararg results: ValidationResult): ValidationResult {
        for (result in results) {
            if (!result.isValid) {
                return ValidationResult(false, result.err)
            }
        }
        return ValidationResult(true, UIStr.Str())
    }

    private fun getUIStr(@StringRes resId: Int) = UIStr.ResStr(resId)


    override fun validZip(zipCode: String): ValidationResult {
        val zipPattern = "^\\d{5}(?:[-\\s]\\d{4})?\$"
        if (zipCode.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_zipcode))
        }
        if (zipCode.matches(zipPattern.toRegex()).not()) {
            return ValidationResult(false, getUIStr(R.string.please_enter_a_valid_zip_code))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateCity(city: String): ValidationResult {
        if (city.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_city))
        }

        if (city.containsSpecialCharacterAndNumber()) {
            return ValidationResult(false, getUIStr(R.string.err_valid_city))
        }
//
        if (city.isNumberOnly()) {
            return ValidationResult(false, getUIStr(R.string.err_valid_city))
        }

        if (city.isSpecialCharOnly()) {
            return ValidationResult(false, getUIStr(R.string.err_valid_city))
        }

        return validateLength(value = city, message = R.string.err_valid_city)
    }

    override fun validateStreetAddress(state: String): ValidationResult {

        if (state.isEmpty()) {
            return ValidationResult(false, getUIStr(R.string.err_street_address))
        }

//        if (state.isNumberOnly()) {
//            return ValidationResult(false, getUIStr(R.string.err_valid_street_address))
//        }
//
//        if (state.isSpecialCharOnly()) {
//            return ValidationResult(false, getUIStr(R.string.err_valid_street_address))
//        }

        return validateLength(value = state, message = R.string.err_valid_street_address)
    }

    override fun validateNull(any: Any?, message: Int): ValidationResult {
        if (any == null) {
            return ValidationResult(false, getUIStr(message))
        }
        return ValidationResult(true, UIStr.Str())
    }

    override fun validateEmpty(any: String, message: Int): ValidationResult {
        return ValidationResult(
            any.isEmpty().not(), getUIStr(message)
        )
    }


    override fun validateGender(gender: Gender?, errGender: Int): ValidationResult {
        if (gender == null) {
            return ValidationResult(false, getUIStr(errGender))
        }
        if (gender.toId() == -1) {
            return ValidationResult(false, getUIStr(errGender))
        }
        return ValidationResult(true, UIStr.Str())
    }

}


