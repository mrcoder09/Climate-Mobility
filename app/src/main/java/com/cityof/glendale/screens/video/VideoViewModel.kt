package com.cityof.glendale.screens.video

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cityof.glendale.utils.AppPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VideoViewModel"

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : ViewModel() {


    var isLanguageShown: Boolean = false
    var isLoggedIn: Boolean = false

    init {
        isLanguageShown()
    }

    private fun isLanguageShown() {
        viewModelScope.launch {
            val isShown = appPreferenceManager.isLanguageShown.firstOrNull() ?: false
            isLanguageShown = isShown
            Log.d(TAG, "isLanguage: $isShown")
            isLoggedIn = appPreferenceManager.isLoggedIn.firstOrNull() ?: false
            appPreferenceManager.setIsHomeDialog(true)
            appPreferenceManager.set911Dialog(true)
        }
    }


}