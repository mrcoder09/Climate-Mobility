package com.cityof.glendale.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream


object BitmapUtils{

    fun imageUriToBase64(context: Context, imageUri: Uri): String? {
        // Step 1: Get the real path from the content URI
        val filePath = RealPathUtil.getRealPath(context, imageUri)

        // Step 2: Read the file into a ByteArray
        val file = File(filePath)
        val byteArray = readBytesFromFile(file)

        // Step 3: Encode ByteArray to Base64
        return byteArray?.let {
            Base64.encodeToString(it, Base64.NO_WRAP)
        }
    }

    private fun readBytesFromFile(file: File): ByteArray? {
        return try {
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(8192)
            val output = ByteArrayOutputStream()
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            val result = output.toByteArray()
            Timber.d("${result.size}")
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun convertImageBitmapToBase64(imageBitmap: ImageBitmap): String {
        val bitmap = imageBitmap.asAndroidBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        val byteArray: ByteArray = stream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return base64String  //.trimEnd('=')
    }

//    fun convertImageBitmapToBase64(imageBitmap: ImageBitmap): String {
//        val bitmap = imageBitmap.asAndroidBitmap()
//        val temp = Bitmap.createScaledBitmap(bitmap, 256,5,false)
//        val stream = ByteArrayOutputStream()
//        temp.compress(Bitmap.CompressFormat.JPEG, 10, stream)
//        val byteArray: ByteArray = stream.toByteArray()
//        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
//        return base64String  //.trimEnd('=')
//    }


}
