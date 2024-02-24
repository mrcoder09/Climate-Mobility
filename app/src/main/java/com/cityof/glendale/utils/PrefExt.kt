package com.cityof.glendale.utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.cityof.glendale.BaseApp

const val APP_PREFERENCE = "app_preferences"
const val PREF_USER_ID = "user_id"
const val PREF_ACCESS_TOKEN = "access_token"
const val PREF_PROFILE = "profile"
const val PREF_FCM_TOKEN = "fcm_token"

const val PREF_IS_LANGUAGE = "is_language"
const val PREF_APP_LANGUAGE = "app_language"
const val PREF_IS_BIOMETRIC = "is_biometric_enabled"


/**
 * Extension to Context for obtain DataStore object
 * for the app.
 * @author Satnam Singh
 * */
val Context.appDataStore by preferencesDataStore(name = APP_PREFERENCE)

/**
 * Function for saving a value to SharedPreference.
 * Note All Shared preference are stored in String type
 * @author Satnam Singh
 * */
fun Context.xtPutKey(key: String, value: String = "") {
    getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).apply {
        this.edit().putString(key, value).apply()
    }
}

/**
 * Function for getting value from SharedPreferences
 * @return value or null
 * @author Satnam Singh
 * */
fun Context.xtGetKey(key: String, defValue: String? = null): String? {
    return getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getString(
        key, defValue
    )
}

/**
 * Function for removing a particular key
 * @author Satnam Singh
 * */
fun Context.xtRemoveKey(key: String) {
    getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).apply {
        this.edit().remove(key).apply()
    }
}

/**
 * Function for removing all SharedPreferences
 * @author Satnam Singh
 */
fun Context.xtRemoveAllKey() {
    getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).apply {
        this.edit().clear().apply()
    }
}

object LegacyPreferences {
    var fcmToken: String
        get() {
            return BaseApp.INSTANCE.xtGetKey(PREF_FCM_TOKEN) ?: ""
        }
        set(value) {
            BaseApp.INSTANCE.xtPutKey(PREF_FCM_TOKEN, value)
        }

}