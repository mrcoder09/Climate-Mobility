package com.cityof.glendale.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.content.ContextCompat
import com.cityof.glendale.BaseApp
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.gson.Gson
import org.apache.commons.lang3.StringUtils
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties


fun <T> mutableListWithCapacity(capacity: Int): MutableList<T> =
    ArrayList(capacity)

fun Any.xtJson() = Gson().toJson(this)

fun String.capitalizeWords(): String =
    split(" ").map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } }.joinToString(" ")


fun String.containsSpecialCharacterAndNumber() = isSpecialCharOnly() && isNumberOnly()

fun String.isSpecialCharOnly() = StringUtils.containsAny(this, "!@#$%^&*()_+-=[]{};':\"\\|,.<>?/")

fun String.isNumberOnly() = StringUtils.containsAny(this, "0123456789")


fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable!!.setBounds(
        0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight
    )
    val bitmap = Bitmap.createBitmap(
        vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

//fun doSomething(context: Context, vectorResId: Int, color: String): BitmapDescriptor{
//    val vectorDrawableCompat = VectorDrawableCompat.create(context.resources, vectorResId, null)
//    val color = Color.fromHex(color)
////    DrawableCompat.setTint(vectorDrawableCompat!!, color.value)
//    val markerIcon = BitmapDescriptorFactory.fromBitmap(vectorDrawableCompat?.toBitmap())
//    return markerIcon
//}

fun jsonFromRaw(
    fileId: Int
): String {
    val `is`: InputStream = BaseApp.INSTANCE.resources.openRawResource(fileId)
    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    try {
        val reader: Reader = BufferedReader(InputStreamReader(`is`, "UTF-8"))
        var n: Int
        while (reader.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    } finally {
        `is`.close()
    }
    return writer.toString()
}

fun Long.toTimeFormat() {
    String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)
    )
}


/**
 * @author Satnam Singh
 * Extension method for returning 2 or > 2 digit string value
 */
fun Int.xt2Digit(): String {
    return let {
        if (this < 10) "0$this" else "$this"
    }
}

/**
 * @author Satnam Singh
 * Extension method for returning 2 or > 2 digit string value
 */
fun Long.xt2Digit(): String {
    return let {
        if (this < 10) "0$this" else "$this"
    }
}


fun Double.xt2Digit(): String {
    if (this == 0.0){
        return "0"
    }
    return String.format("%.2f", this)
}


@Composable
fun DetectOrientation() {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // Switch Layout or modify some properties for landscape mode
        }

        Configuration.ORIENTATION_PORTRAIT -> {
            // Switch Layout or modify some properties for portrait mode
        }
    }
}

fun Color.Companion.fromHex(colorString: String) =
    Color(android.graphics.Color.parseColor("#$colorString"))


inline fun <T> Iterable<T>.only(predicate: (T) -> Boolean): Boolean {
    if (this is Collection && isEmpty()) return false
    for (element in this) if (!predicate(element)) return false
    return true
}


/**
 * @author Satnam Singh
 * Extension method to browse for Context.
 */
fun Context.xtIntentBrowse(url: String, newTask: Boolean = false): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    if (newTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    return fireIntent(intent)
}

fun Context.fireIntent(intent: Intent): Boolean {
    return try {
        startActivity(Intent.createChooser(intent, null))
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}

fun Context.xtIntentDialer(mobileNumber: String): Boolean {
    return try {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL // Action for what intent called for
        intent.data =
            Uri.parse("tel: $mobileNumber") // Data with intent respective action on intent
        startActivity(intent)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Exclude

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ParamName(val alternateName: String)

/**
 * Extension for mapping any class to Map<String,Any?>
 * @author Satnam Singh
 * @return Map<String,Any?>
 */
fun <T : Any> T.asMap(): Map<String, Any?> {
    val properties = this::class.memberProperties.filterIsInstance<KProperty1<T, *>>()
    val map = mutableMapOf<String, Any?>()
    for (property in properties) {
        val excludeAnnotation = property.findAnnotation<Exclude>()
        if (excludeAnnotation == null) {
            val name = property.findAnnotation<ParamName>()?.alternateName ?: property.name
            val value = property.get(this)
            map[name] = value
        }
    }
    return map
}


