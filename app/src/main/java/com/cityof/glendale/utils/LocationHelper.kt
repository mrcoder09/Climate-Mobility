package com.cityof.glendale.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit


/**
 * Manages all location related tasks for the app.
 */

const val LOCATION_TAG = "LocationHelper"

//A callback for receiving notifications from the FusedLocationProviderClient.
lateinit var locationCallback: LocationCallback

//The main entry point for interacting with the Fused Location Provider
lateinit var locationProvider: FusedLocationProviderClient

@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(
    onAskPermission: () -> Unit
): LatLng {

    val context = LocalContext.current
    locationProvider = LocationServices.getFusedLocationProviderClient(context)
    var currentUserLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    DisposableEffect(key1 = locationProvider) {
        locationCallback = object : LocationCallback() {
            //1
            override fun onLocationResult(result: LocationResult) {
                /**
                 * Option 1
                 * This option returns the locations computed, ordered from oldest to newest.
                 * */
                for (location in result.locations) {
                    // Update data class with location data
                    currentUserLocation = LatLng(location.latitude, location.longitude)
                    Log.d(LOCATION_TAG, "${location.latitude},${location.longitude}")
                }

            }
        }

        //2
        if (context.xtHavePermissions(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            locationUpdate()
        } else {
            onAskPermission()
        }
        //3
        onDispose {
            stopLocationUpdate()
        }
    }
    //4
    return currentUserLocation
}


fun stopLocationUpdate() {
    try {
        //Removes all location updates for the given callback.
        val removeTask = locationProvider.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(LOCATION_TAG, "Location Callback removed.")
            } else {
                Log.d(LOCATION_TAG, "Failed to remove Location Callback.")
            }
        }
    } catch (se: SecurityException) {
        Log.e(LOCATION_TAG, "Failed to remove Location Callback.. $se")
    }
}

@SuppressLint("MissingPermission")
fun locationUpdate() {
    locationCallback.let {
        //An encapsulation of various parameters for requesting
        // location through FusedLocationProviderClient.
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //use FusedLocationProviderClient to request location update
        locationProvider.requestLocationUpdates(
            locationRequest, it, Looper.getMainLooper()
        )
    }
}


fun getReadableLocation(latitude: Double, longitude: Double, context: Context): String {
    var addressText = ""
    val geocoder = Geocoder(context, Locale.getDefault())

    try {

        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            addressText = "${address.getAddressLine(0)}, ${address.locality}"
            // Use the addressText in your app
            Log.d("geolocation", addressText)
        }

    } catch (e: IOException) {
        Log.d("geolocation", e.message.toString())

    }

    return addressText

}

//object LocationHelper {
//
//
//    lateinit var fusedLocationClient: FusedLocationProviderClient
//
//
//    fun startLocationUpdate(
//        onLocation: () -> Unit
//    ){
//
//
//    }
//
//    fun stopLocationUpdate(){
//        fusedLocationClient.removeLocationUpdates()
//    }
//
//    fun getLastLocation(){
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                // Got last known location. In some rare situations this can be null.
//            }
//    }
//
//    fun singleLocation(context: Context){
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        fusedLocationClient.getCurrentLocation()
//            .addOnSuccessListener { location : Location? ->
//
//
//            }
//    }
//
//    fun openLocationSetting(){
//
//    }
//
//    fun checkLocationPermission(){
//
//    }
//
//    fun askLocationPermission(){
//
//    }
//}


//class LocationService @Inject constructor(
//    private val context: Context,
//    private val locationClient: FusedLocationProviderClient
//): ILocationService {
//    @SuppressLint("MissingPermission")
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun requestLocationUpdates(): Flow<LatLng?> = callbackFlow {
//
//        if (!context.hasLocationPermission()) {
//            trySend(null)
//            return@callbackFlow
//        }
//
//        val request = LocationRequest.Builder(10000L)
//            .setIntervalMillis(10000L)
//            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            .build()
//
//        val locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.locations.lastOrNull()?.let {
//                    trySend(LatLng(it.latitude, it.longitude))
//                }
//            }
//        }
//
//        locationClient.requestLocationUpdates(
//            request,
//            locationCallback,
//            Looper.getMainLooper()
//        )
//
//        awaitClose {
//            locationClient.removeLocationUpdates(locationCallback)
//        }
//    }
//
//    override fun requestCurrentLocation(): Flow<LatLng?> {
//        TODO("Not yet implemented")
//    }
//
//}