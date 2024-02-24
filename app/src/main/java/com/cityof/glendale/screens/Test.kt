package com.cityof.glendale.screens

import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cityof.glendale.BuildConfig
import com.cityof.glendale.utils.RealPathUtil
import timber.log.Timber
import java.io.File
import java.util.Calendar
import java.util.Locale
import java.util.Objects

data class NavItem(val title: String, val icon: Painter)

//@Composable
//@Preview
//fun HomeScreenWithTab() {
//
//
//    val datePicker = remember {
//        mutableStateOf(false)
//    }
//
//    if (datePicker.value)
//        MyDatePicker()
//
//
//    Column {
//        Button(onClick = {
//            datePicker.value = true
//        }) {
//            Text(text = "Click me here")
//        }
//    }
//
//}


//@Composable
//fun MyDatePicker() {
//
//    AndroidView(factory = {
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val month = c.get(Calendar.MONTH)
//        val day = c.get(Calendar.DAY_OF_MONTH)
//        val datePickerDialog = DatePickerDialog(it, { view, year, month, dayOfMonth ->
//
//        }, year, month, day)
//
//        datePickerDialog.show()
//
//        datePickerDialog.datePicker.rootView
//    })
//
//}

fun Context.createImageFile(): File {
    // Create an image file name
//    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "myimage_JPEG_"    // + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}


@Composable
@Preview
fun HomeScreenWithTab() {
    val context = LocalContext.current
    val datePicker = remember { mutableStateOf(false) }

//    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    if (capturedImageUri!=Uri.EMPTY){
        Timber.d("onImage selected")
//        ImageCrop(uri = capturedImageUri)
//        EasyImageCropper(uri = capturedImageUri)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            capturedImageUri = uri

            val temp = RealPathUtil.getRealPath(context, capturedImageUri)
            Timber.d("$capturedImageUri")
            Timber.d(temp)
        }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }


    Button(onClick = {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            // Request a permission
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }) {
        Text(text = "Click me here")
    }
}

fun showDatePicker(
    context: Context,
    calendar: Calendar = Calendar.getInstance(Locale.getDefault()),
    onTimeChanged: (Long) -> Unit
) {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = DatePickerDialog(
        context,
        com.cityof.glendale.R.style.app_date_picker_theme,
        { d, yearOfYear, monthOfYear, dayOfMonth ->
            // Do something with the date
            val time = Calendar.getInstance().also {
                it.set(Calendar.YEAR, yearOfYear)
                it.set(Calendar.MONTH, monthOfYear)
                it.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            Timber.d("$time")
            onTimeChanged(time)
        },
        year,
        month,
        day
    )
    datePickerDialog.datePicker.maxDate = calendar.timeInMillis
    datePickerDialog.show()
}
