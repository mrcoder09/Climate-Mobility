package com.cityof.glendale.utils

import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber


const val TAG_FOR = "LOCATION_SERVICE"

interface ILocationService {

    fun requestLocationUpdates(): Flow<LatLng?>
    fun requestLocationMoreFrequent(): Flow<LatLng?>
    fun requestCurrentLocation(): Flow<LatLng?>
    fun requestLastLocation(): Flow<LatLng?>
    fun requestSingleLocation(): Flow<LatLng?>
    fun removeLocationUpdates(): Boolean

    fun haveLocationPermission(): Boolean
}

class LocationService(
    private val context: Context, private val locationClient: FusedLocationProviderClient
) : ILocationService {


    var locationCallback: LocationCallback? = null


    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun requestLocationUpdates(): Flow<LatLng?> = callbackFlow {

        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }


        val request = LocationRequest.Builder(10000L).setIntervalMillis(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let {
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        }

        locationClient.requestLocationUpdates(
            request, locationCallback as LocationCallback, Looper.getMainLooper()
        )
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun requestLocationMoreFrequent(): Flow<LatLng?> = callbackFlow {
        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }


        val request = LocationRequest.Builder(10000).setIntervalMillis(5000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    trySend(latLng)
                }
            }
        }
        locationClient.requestLocationUpdates(
            request, locationCallback as LocationCallback, Looper.getMainLooper()
        )
        awaitClose {

        }
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun requestCurrentLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun requestLastLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun requestSingleLocation(): Flow<LatLng?> = callbackFlow {

        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }

        val request = LocationRequest.Builder(10000L).setIntervalMillis(10000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let {
                    Timber.d("$TAG_FOR :: ${it.xtJson()}")
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        }

        locationClient.requestLocationUpdates(
            request, locationCallback, Looper.getMainLooper()
        )

        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun removeLocationUpdates(): Boolean {
        return locationCallback?.let {
            locationClient.removeLocationUpdates(it)
            true
        } ?: run {
            false
        }
    }

    override fun haveLocationPermission() = context.hasLocationPermission()

}


class MockLocationService : ILocationService {
    override fun requestLocationUpdates(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun requestLocationMoreFrequent(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun requestCurrentLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun requestLastLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun requestSingleLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun removeLocationUpdates(): Boolean {
        TODO("Not yet implemented")
    }

    override fun haveLocationPermission(): Boolean {
        TODO("Not yet implemented")
    }

}


