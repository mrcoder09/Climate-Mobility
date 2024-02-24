package com.cityof.glendale

import android.app.Application
import android.content.Context
import com.cityof.glendale.network.GoogleEndPoints
import com.cityof.glendale.utils.LangHelper
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

private const val TAG = "BaseApp"


@HiltAndroidApp
class BaseApp : Application() {


    companion object {
        lateinit var INSTANCE: BaseApp
        var myLang = "en"
    }


    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initAppDependencies()

    }

    override fun attachBaseContext(base: Context) {
        myLang = LangHelper.getLocale(base)
        super.attachBaseContext(LangHelper.setLocale(base, myLang))
    }

    private fun initAppDependencies() {
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return String.format(
                        "[L:%s] [M:%s] [C:%s]",
                        element.lineNumber,
                        element.methodName,
                        super.createStackElementTag(element)
                    )
                }
            })
        }
        Places.initializeWithNewPlacesApiEnabled(this, GoogleEndPoints.GOOGLE_API_KEY)
    }

}




