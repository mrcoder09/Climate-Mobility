package com.cityof.glendale

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.cityof.glendale.composables.SetStatusBarColor
import com.cityof.glendale.navigation.AppNav
import com.cityof.glendale.theme.AppTheme
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.utils.LangHelper
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ComposeMainActivity : FragmentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LangHelper.setLocale(this, BaseApp.myLang)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        setContent {

            AppTheme {
                SetStatusBarColor()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BG_WINDOW)
                ) {
                    val navController = rememberNavController()
                    AppNav.SetUpNavigation(navHostController = navController)
                    navController.addOnDestinationChangedListener{_, destination, _ ->
                        logEvents(destination.route)
                    }
                }
            }
        }
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data

        Timber.d("COMPOSE_MAIN_ACTIVITY: $appLinkAction $appLinkData")
        askNotificationPermission()


    }

    fun logEvents(route: String?) {
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.SCREEN_NAME,route)
        params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, route)
        Timber.d("LOG-EVENTS: $route")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }


    override fun attachBaseContext(newBase: Context) {
        val newContext = LangHelper.setLocale(newBase, BaseApp.myLang)
        super.attachBaseContext(newContext)
    }


    // Declare the launcher at the top of your Activity/Fragment:


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                rationaleForNotification()
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun rationaleForNotification() {
        AlertDialog.Builder(this).setTitle(R.string.app_name)
            .setMessage(getString(R.string.msg_permission_ration_post_notification))
            .setPositiveButton(
                R.string.ok
            ) { dialog, which ->
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.no_thanks)) { dialog, which ->
                dialog.dismiss()
            }.create().show()
    }


}

