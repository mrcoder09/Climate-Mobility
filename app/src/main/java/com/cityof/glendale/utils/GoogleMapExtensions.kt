package com.cityof.glendale.utils

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.view.animation.LinearInterpolator
import com.cityof.glendale.BaseApp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.maps.android.SphericalUtil
import timber.log.Timber
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToInt


fun GoogleMap.moveCameraToBounds(
    list: List<LatLng>, padding: Int = 200 // offset from edges of the map in pixels
) {
    val builder = LatLngBounds.Builder()
    list.forEach {
        builder.include(it)
    }
    val bounds = builder.build()
    val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
    moveCamera(cu)
}


fun MarkerOptions.rotateMarker(
    oldPosition: LatLng, newLatLng: LatLng
): MarkerOptions {
    val bearing = getBearing(oldPosition, newLatLng)
    return this.rotation(bearing * (180.0f / Math.PI.toFloat()))
}

fun GoogleMap.animateWithTilt(oldPosition: LatLng, newPosition: LatLng){
    val bearing = getBearing(oldPosition, newPosition)
    val cameraPosition = CameraPosition.Builder()
        .target(newPosition)
        .zoom(15f)
        .bearing(bearing) // bearing is a Float in Kotlin
        .tilt(40f) // viewingAngle is represented by tilt in Google Maps Android API
        .build()
    animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
}


fun Marker.rotateMarker(
    newLatLng: LatLng
) {
    val bearing = getBearing(position, newLatLng)
    rotation = bearing * (180.0f / Math.PI.toFloat())
}

fun Marker.rotateMarker(
    newLatLng: LatLng,
    destinationLatLng: LatLng,
) {
    val bearing = getBearing(destinationLatLng, newLatLng)
    rotation = bearing * (180.0f / Math.PI.toFloat())
}


fun LatLng.angleInDegrees(to: LatLng): Int {
    return ((SphericalUtil.computeHeading(this, to) + 360) % 360).roundToInt()
}

fun LatLng.isInDistance(to: LatLng, distance: Double = 100.0): Boolean {
    val distanceInMtr = SphericalUtil.computeDistanceBetween(this, to)
    Timber.d("$distanceInMtr")
    return distanceInMtr >= distance
}


fun getBearing(oldPosition: LatLng, newPosition: LatLng): Float {
    val deltaLongitude = newPosition.longitude - oldPosition.longitude
    val deltaLatitude = newPosition.latitude - oldPosition.latitude
    val angle = Math.PI * .5f - Math.atan(deltaLatitude / deltaLongitude)
    if (deltaLongitude > 0) {
        return angle.toFloat()
    } else if (deltaLongitude < 0) {
        return (angle + Math.PI).toFloat()
    } else if (deltaLatitude < 0) {
        return Math.PI.toFloat()
    }
    return 0.0f
}


//fun isDistanceBetween(
//    source: LatLng, newLatLng: LatLng, distanceInMeter: Float = 500.0f
//): Boolean {
//    val startPoint = Location("locationA")
//    startPoint.latitude = source.latitude
//    startPoint.longitude = source.longitude
//    val endPoint = Location("locationA")
//    endPoint.latitude = newLatLng.latitude
//    endPoint.longitude = newLatLng.longitude
//    val distance = startPoint.distanceTo(endPoint).toDouble()
//    return distance > distanceInMeter
//}

private fun calculateDistance(
    startLat: Double, startLng: Double, endLat: Double, endLng: Double
): String? {
    val startPoint = Location("locationA")
    startPoint.latitude = startLat
    startPoint.longitude = startLng
    val endPoint = Location("locationA")
    endPoint.latitude = endLat
    endPoint.longitude = endLng
    val distance = startPoint.distanceTo(endPoint).toDouble()
    val df = DecimalFormat("#0.00")
    return df.format(distance / 1000)
}

fun getAddressFromPlaceId(placeId: String) {
    val fields = listOf(Place.Field.VIEWPORT)
    val request = FetchPlaceRequest.newInstance(placeId, fields)
    val placesClient = Places.createClient(BaseApp.INSTANCE)
    placesClient.fetchPlace(request).addOnSuccessListener { response ->
        val place = response.place
        val viewport = place.viewport
        val northeast = viewport?.northeast
        val southwest = viewport?.southwest
    }.addOnFailureListener { exception ->
        // Handle any errors that occur during the request
    }
}

fun getAddressFromLatLng(latLng: LatLng,
                         defaultMessage: String = "Location not found"): String {
    val geocoder = Geocoder(BaseApp.INSTANCE, Locale.getDefault())
    val address: Address?
    var fulladdress = ""
    try {
        val addresses: List<Address>? =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            address = addresses[0]
            fulladdress =
                address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
//        var city = address.locality;
//        var state = address.adminArea;
//        var country = address.countryName;
//        var postalCode = address.postalCode;
//        var knownName = address.featureName; // Only if available else return NULL
        } else {
            fulladdress = defaultMessage
        }
    } catch (e: Exception) {
        e.printStackTrace()
        fulladdress = defaultMessage
    }


    return fulladdress
}

fun addressWithCityStateCountry(
    latLng: LatLng,
    defaultMessage: String = "Location not found"
): String {
    val geocoder = Geocoder(BaseApp.INSTANCE, Locale.getDefault())
    val address: Address?
    val fullAddress = StringBuilder()
    try {
        val addresses: List<Address>? =
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            address = addresses[0]

            fullAddress.append(
                address.getAddressLine(0)
            )
            val city = address.locality
            fullAddress.append(", ")
            fullAddress.append(city)
            val state = address.adminArea
            fullAddress.append(", ")
            fullAddress.append(state)
            val country = address.countryName
            fullAddress.append(", ")
            fullAddress.append(country)
//        var postalCode = address.postalCode;
//        var knownName = address.featureName; // Only if available else return NULL
        } else {
            fullAddress.append(defaultMessage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        fullAddress.append(defaultMessage)
    }


    return fullAddress.toString()
}


fun GoogleMap.zoomRoute(list: List<LatLng?>, padding: Int = 8) {
    try {
        val boundBuilder = LatLngBounds.Builder()
        list.forEach {
            if (it != null) boundBuilder.include(it)
        }
        val latLngBounds = boundBuilder.build()
        animateCamera(
            CameraUpdateFactory.newLatLngBounds(latLngBounds, padding), 50, null
        )
        Timber.d("after camer move")
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}



fun animateMarker(marker: Marker, previousPosition: LatLng, newPosition: LatLng) {
    // Create a value animator that will smoothly move the marker
    val valueAnimator = ObjectAnimator.ofObject(LatLngEvaluator(), previousPosition, newPosition)
    valueAnimator.setDuration(2000) // Duration of the animation in milliseconds
    valueAnimator.interpolator = LinearInterpolator()

    // Update the marker's position during the animation
    valueAnimator.addUpdateListener { animation: ValueAnimator ->
        val animatedPosition = animation.animatedValue as LatLng
        marker.position = animatedPosition
    }

    // Start the animation
    valueAnimator.start()
}

// Custom LatLng evaluator for animation
private class LatLngEvaluator : TypeEvaluator<LatLng> {
    override fun evaluate(fraction: Float, startValue: LatLng, endValue: LatLng): LatLng {
        val lat = (endValue.latitude - startValue.latitude) * fraction + startValue.latitude
        var lngDelta = endValue.longitude - startValue.longitude

        // If the marker crosses the 180th meridian, correct the interpolation
        if (Math.abs(lngDelta) > 180) {
            lngDelta -= Math.signum(lngDelta) * 360
        }
        val lng = lngDelta * fraction + startValue.longitude
        return LatLng(lat, lng)
    }
}
