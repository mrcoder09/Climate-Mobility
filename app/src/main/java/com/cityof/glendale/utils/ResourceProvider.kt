package com.cityof.glendale.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.apache.commons.lang3.StringUtils

interface ResourceProvider {
    fun getString(@StringRes resId: Int): String
    fun getColor(resId: Int): Int

    fun getDrawable(resId: Int): Drawable?

    fun getDimenPixelSize(resId: Int): Int

    fun deviceName(): String

    fun deviceId(): String
    fun getArr(genderArray: Int): Array<String>
}


class ResourceProviderImpl(val context: Context) : ResourceProvider {

    override fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    override fun getColor(resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    override fun getDrawable(resId: Int): Drawable? {
        return ContextCompat.getDrawable(context, resId)
    }

    override fun getDimenPixelSize(resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }

    override fun deviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val device = Build.DEVICE
        return if (model.startsWith(manufacturer)) {
            StringUtils.capitalize(model)
        } else StringUtils.capitalize(manufacturer) + " " + model + " " + device
    }

    @SuppressLint("HardwareIds")
    override fun deviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    override fun getArr(resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }
}
