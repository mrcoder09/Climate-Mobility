package com.cityof.glendale.utils


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.LoginData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface AppPreferenceManager {

    companion object {
        val PREF_IS_LANGUAGES_SHOWN = booleanPreferencesKey("is_languages_shown")
        val PREF_IS_REMEMBER_ME = booleanPreferencesKey("is_remember_me")
        val PREF_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val PREF_IS_HOME_DIALOG = booleanPreferencesKey("is_home_dialog")

        val PREF_EMAIL = stringPreferencesKey("email")
        val PREF_PASSWORD = stringPreferencesKey("password")
        val PREF_IS_BIOMETRIC = booleanPreferencesKey("is_biometric")

        val PREF_BIO_EMAIL = stringPreferencesKey("biometric_email")
        val PREF_BIO_PASSWORD = stringPreferencesKey("biometric_password")

        val PREF_USER_DETAILS = stringPreferencesKey("user_details")
        val PREF_TOKEN = stringPreferencesKey("token")

        val PREF_HIVE_POINTS = stringPreferencesKey("hive_points")

        val PREF_IS_911_DIALOG = booleanPreferencesKey("is_911_dialog")

        val RECENT_LOCATION_SEARCH = stringPreferencesKey("recent_locations")

//        val PREF_ONGOING_TRIP = stringPreferencesKey("ongoing_trip")
    }

    val isLanguageShown: Flow<Boolean?>

    val userDetails: Flow<LoginData?>
    val isLoggedIn: Flow<Boolean?>
    val token: Flow<String?>

    val rememberDetail: Flow<Triple<String?, String?, Boolean?>>
    val biometricDetails: Flow<Triple<String?, String?, Boolean?>?>

    val isHomeDialog: Flow<Boolean?>

    val hivePoints: Flow<HivePoints?>

    val is911Dialog: Flow<Boolean?>

    val recentLocationSearches: Flow<List<LocationSearched>>

//    val isTripDone: Flow<Boolean?>
//    suspend fun setTripDone(value: Boolean?)


    suspend fun set911Dialog(value: Boolean)
    suspend fun setIsHomeDialog(value: Boolean)

    suspend fun setIsLanguageShown(value: Boolean)

    //    suspend fun setIsBiometricEnabled(value: Boolean)
    suspend fun setIsRememberMe(value: Boolean)
    suspend fun setToken(value: String?)
    suspend fun setUserDetail(value: LoginData?)
    suspend fun setRememberDetail(
        email: String = "", password: String = "", isRememberMe: Boolean = false
    )

    suspend fun setBiometricDetail(
        email: String?, password: String?, isBiometricEnabled: Boolean = false
    )

    suspend fun reset()

    //    suspend fun <T> resetExcept(vararg keys: Preferences.Key<T>)
    suspend fun doLogout()
    suspend fun <T> removeKey(key: Preferences.Key<T>)
    suspend fun setIsLoggedIn(value: Boolean)

    suspend fun deleteAccount()

    suspend fun setHivePoint(value: HivePoints?)

    suspend fun updateLocationSearch(placePrediction: LocationSearched)
}


class AppPreferencesManagerImpl(
    private val dataStore: DataStore<Preferences>, private val gson: Gson = Gson()
) : AppPreferenceManager {

//    companion object {
//        private val PREF_IS_LANGUAGES_SHOWN = booleanPreferencesKey("is_languages_shown")
//        private val PREF_IS_REMEMBER_ME = booleanPreferencesKey("is_remember_me")
//        private val PREF_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
//        private val PREF_IS_HOME_DIALOG = booleanPreferencesKey("is_home_dialog")
//
//        private val PREF_EMAIL = stringPreferencesKey("email")
//        private val PREF_PASSWORD = stringPreferencesKey("password")
//        private val PREF_IS_BIOMETRIC = booleanPreferencesKey("is_biometric")
//
//        private val PREF_BIO_EMAIL = stringPreferencesKey("biometric_email")
//        private val PREF_BIO_PASSWORD = stringPreferencesKey("biometric_password")
//
//        private val PREF_USER_DETAILS = stringPreferencesKey("user_details")
//        private val PREF_TOKEN = stringPreferencesKey("token")
//    }

    override val isLanguageShown: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_IS_LANGUAGES_SHOWN] ?: false
    }


    override val userDetails: Flow<LoginData?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_USER_DETAILS]?.let {
            gson.fromJson(it, LoginData::class.java)
        }
    }
    override val isLoggedIn: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_IS_LOGGED_IN]
    }

    override val token: Flow<String?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_TOKEN]
    }

    override val rememberDetail: Flow<Triple<String?, String?, Boolean?>> =
        dataStore.data.map { preferences ->
            Triple(
                preferences[AppPreferenceManager.PREF_EMAIL],
                preferences[AppPreferenceManager.PREF_PASSWORD],
                preferences[AppPreferenceManager.PREF_IS_REMEMBER_ME]
            )
        }

    override val biometricDetails: Flow<Triple<String?, String?, Boolean?>?> =
        dataStore.data.map { preferences ->
            val email = preferences[AppPreferenceManager.PREF_BIO_EMAIL]
            val pwd = preferences[AppPreferenceManager.PREF_BIO_PASSWORD]
            val isBio = preferences[AppPreferenceManager.PREF_IS_BIOMETRIC]
            Timber.d("$email $pwd $isBio")
            if (email != null && pwd != null && isBio != null) Triple(
                email, pwd, isBio
            )
            else null

        }
    override val isHomeDialog: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_IS_HOME_DIALOG]
    }
    override val hivePoints: Flow<HivePoints?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_HIVE_POINTS]?.let {
            gson.fromJson(it, HivePoints::class.java)
        } ?: kotlin.run {
            null
        }
    }
    override val is911Dialog: Flow<Boolean?> = dataStore.data.map { preferences ->
        preferences[AppPreferenceManager.PREF_IS_911_DIALOG]
    }
    override val recentLocationSearches: Flow<List<LocationSearched>> =
        dataStore.data.map { preferences ->
            preferences[AppPreferenceManager.RECENT_LOCATION_SEARCH]?.let {
                val tokenType = object : TypeToken<List<LocationSearched>?>() {}.type
                gson.fromJson(it, tokenType)
            } ?: run {
                emptyList()
            }
        }

//    override val isTripDone: Flow<Boolean?> = dataStore.data.map { preferences ->
//        preferences[AppPreferenceManager.PREF_ONGOING_TRIP]
//    }
//
//    override suspend fun setTripDone(value: Boolean?) {
//        dataStore.edit { preferences ->
//            preferences[AppPreferenceManager.PREF_ONGOING_TRIP] = value ?: false
//        }
//    }

    override suspend fun set911Dialog(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_IS_911_DIALOG] = value
        }
    }

    override suspend fun setIsHomeDialog(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_IS_HOME_DIALOG] = value
        }
    }


    override suspend fun setIsLanguageShown(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_IS_LANGUAGES_SHOWN] = value
        }
    }

    override suspend fun setIsRememberMe(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_IS_REMEMBER_ME] = value
        }
    }

    override suspend fun setToken(value: String?) {
        value?.let {
            dataStore.edit { preferences ->
                preferences[AppPreferenceManager.PREF_TOKEN] = it
            }
        }
    }

    override suspend fun setUserDetail(value: LoginData?) {
        value?.let {
            dataStore.edit { preferences ->
                preferences[AppPreferenceManager.PREF_USER_DETAILS] = gson.toJson(value)
            }
        }
    }


    override suspend fun setBiometricDetail(
        email: String?, password: String?, isBiometricEnabled: Boolean
    ) {
        if (email != null && password != null) dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_BIO_EMAIL] = email
            preferences[AppPreferenceManager.PREF_BIO_PASSWORD] = password
            preferences[AppPreferenceManager.PREF_IS_BIOMETRIC] = isBiometricEnabled
        }
    }

    override suspend fun setRememberDetail(
        email: String, password: String, isRememberMe: Boolean
    ) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_EMAIL] = email
            preferences[AppPreferenceManager.PREF_PASSWORD] = password
            preferences[AppPreferenceManager.PREF_IS_REMEMBER_ME] = isRememberMe
        }
    }


    override suspend fun reset() {
        dataStore.edit {
            it.clear()
        }
    }

    override suspend fun doLogout() {
        AppConstants.isFromLogout = true
        AppConstants.isBiometricAfterLogout = true
        removeKey(AppPreferenceManager.PREF_TOKEN)
        removeKey(AppPreferenceManager.PREF_HIVE_POINTS)
//        removeKey(AppPreferenceManager.PREF_ONGOING_TRIP)
        setIsLoggedIn(false)
        setIsHomeDialog(true)
    }

    override suspend fun <T> removeKey(key: Preferences.Key<T>) {
        dataStore.edit {
            it.remove(key)
        }
    }

    override suspend fun setIsLoggedIn(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AppPreferenceManager.PREF_IS_LOGGED_IN] = value
        }
    }

    override suspend fun deleteAccount() {
        AppConstants.isFromLogout = true
        AppConstants.isBiometricAfterLogout = true
        setIsLoggedIn(false)
//        removeKey(AppPreferenceManager.PREF_ONGOING_TRIP)
        removeKey(AppPreferenceManager.PREF_PASSWORD)
        removeKey(AppPreferenceManager.PREF_IS_REMEMBER_ME)
        removeKey(AppPreferenceManager.PREF_EMAIL)
        removeKey(AppPreferenceManager.PREF_TOKEN)
        removeKey(AppPreferenceManager.PREF_BIO_EMAIL)
        removeKey(AppPreferenceManager.PREF_BIO_PASSWORD)
        removeKey(AppPreferenceManager.PREF_IS_BIOMETRIC)
    }

    override suspend fun setHivePoint(value: HivePoints?) {
        value?.let {
            dataStore.edit { preferences ->
                preferences[AppPreferenceManager.PREF_HIVE_POINTS] = it.xtJson()
            }
        }
    }

    override suspend fun updateLocationSearch(placePrediction: LocationSearched) {

        Timber.d(placePrediction.xtJson())
        placePrediction.let {
            dataStore.edit { preferences ->
                val savedList = recentLocationSearches.firstOrNull() ?: emptyList()
                val mutableList = savedList.toMutableList()
                if (mutableList.contains(placePrediction).not()) {
                    if (mutableList.size > 4) {
                        val startIndex = 4
                        while (mutableList.size > startIndex) {
                            mutableList.removeAt(startIndex)
                        }
                    }
                    mutableList.add(0, placePrediction)
                }
                Timber.d(mutableList.xtJson())
                preferences[AppPreferenceManager.RECENT_LOCATION_SEARCH] = mutableList.xtJson()
            }
        }
    }


}