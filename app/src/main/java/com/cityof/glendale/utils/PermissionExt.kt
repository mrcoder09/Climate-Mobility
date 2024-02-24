package com.cityof.glendale.utils


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.cityof.glendale.R


fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Method for reducing permission related status
 *
 * e.q. android.Manifest.permission.CAMERA
 * @return True if a permission is Granted else false
 * @author Satnam Singh
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Context.xtHasPermission(
    permission: String, sdkInt: Int = Build.VERSION_CODES.M
): Boolean {
    return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) && isAboveSDK(
        sdkInt
    )
}

/**
 * Method for checking multiple permissions grant status
 * @return True if all permissions are permitted else false
 * @author Satnam Singh
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Context.xtHavePermissions(
    vararg permission: String, sdkInt: Int = Build.VERSION_CODES.M
): Boolean {
    var count = 0
    permission.forEach {
        if (xtHasPermission(it, sdkInt)) count++
    }
    return count == permission.size
}

/**
 * Method for checking if all permissions are granted by user
 * @return True if every permission is granted else false
 * @author Satnam Singh
 */
fun xtIsAllGranted(grantResults: IntArray): Boolean {
    var count = 0
    grantResults.forEach {
        if (it == PackageManager.PERMISSION_GRANTED) count++
    }
    return grantResults.size == count
}

/**
 * Method for showing explanation alert
 * @author Satnam Singh
 */
fun Context.xtSettingAlert(@StringRes msg: Int) {
    AlertDialog.Builder(this)
        .setTitle(R.string.app_name)
        .setMessage(getString(msg))
        .setPositiveButton("Settings") { dialog, _ ->
            xtOpenSettings()
            dialog.dismiss()
        }.setNegativeButton("Dismiss", null)
        .create()
        .show()
}


fun Context.xtOpenSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}

fun isAboveSDK(version: Int = Build.VERSION_CODES.O) = Build.VERSION.SDK_INT >= version
fun isAboveOreo() = isAboveSDK()
