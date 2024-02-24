package com.cityof.glendale.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Log
import com.cityof.glendale.BaseApp
import com.cityof.glendale.R
import java.util.Locale

private const val TAG = "LangHelper"

data class LanguageItem(
    var displayName: String = "",
    var nativeName: String = "",
    var locale: String = "",
    var serverValue: String = "",
    var icon: Int = 0,
    var isSelected: Boolean = false
)


object LangHelper {
    fun setLocale(context: Context, localeCode: String): Context {
        Log.d(TAG, "setLocale: $localeCode")
        BaseApp.myLang = localeCode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, localeCode)
        }
        return updateResourcesLegacy(context, localeCode)
    }

    fun saveLocale(context: Context, locale: String) {
        context.xtPutKey(PREF_APP_LANGUAGE, locale)
    }

    fun getLocale(context: Context): String {
        return context.xtGetKey(PREF_APP_LANGUAGE) ?: "en"
    }

    fun localeToName(context: Context): String {
        return when (getLocale(context)) {
            "es" -> "Español"
            "hy" -> "հայերեն"
            else -> "English"
        }
    }

    fun getServerLocale(): String {
        return when (BaseApp.myLang) {
            "es" -> "es_mx"
            "hy" -> "am"
            else -> "en"
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        saveLocale(context = context, locale = language)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        saveLocale(context = context, locale = language)
        return context
    }

    fun appLanguages(context: Context): List<LanguageItem> {
        return listOf(
            LanguageItem(
                "English", "English", "en", "en", R.drawable.ic_us, getLocale(context) == "en"
            ),LanguageItem(
                "Armenian", "հայերեն", "hy", "am", R.drawable.ic_armenia, getLocale(context) == "hy"
            ), LanguageItem(
                "Spanish",
                "Español",
                "es",
                "es_mx",
                R.drawable.ic_spanish,
                getLocale(context) == "es"
            ),
        )
    }


}