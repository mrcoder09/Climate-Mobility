package com.cityof.glendale.screens.more

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.cityof.glendale.utils.BitmapUtils
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import kotlinx.coroutines.launch
import timber.log.Timber

//@Composable
//fun ImageCrop(uri: Uri) {
//
//    Timber.d("inside image cropper")
//    var imageUri by remember {
//        mutableStateOf<Uri?>(uri)
//    }
//    val context = LocalContext.current
////    val bitmap =  remember {
////        mutableStateOf<Bitmap?>(null)
////    }
//
//    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            // use the cropped image
//            imageUri = result.uriContent
//        } else {
//            // an error occurred cropping
//            val exception = result.error
//        }
//    }
//
//    val imagePickerLauncher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
//            val cropOptions = CropImageContractOptions(uri, CropImageOptions())
//            imageCropLauncher.launch(cropOptions)
//        }
//
//    LaunchedEffect(key1 = Unit, block = {
//        imagePickerLauncher.launch("image/*")
//    })
//
////    if (imageUri != null) {
//////        if (Build.VERSION.SDK_INT < 28) {
//////            bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//////        } else {
////            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
//////            bitmap.value = ImageDecoder.decodeBitmap(source)
//////        }
////        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
////            Text("Pick image to crop")
////        }
////    }
//}


@Composable
fun EasyImageCropper(uri: Uri, onResult: (String?) -> Unit) {

    Timber.d("On Easy Image Cropper")
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()

    LaunchedEffect(key1 = Unit, block = {
        coroutineScope.launch {
            val result = imageCropper.crop(uri, context)
            when (result) {
                CropResult.Cancelled -> {
                    onResult(null)
                }

                is CropError -> {
                    onResult(null)
                }

                is CropResult.Success -> {
                    result.bitmap
                    val base64 = BitmapUtils.convertImageBitmapToBase64(result.bitmap)
//                    Timber.d("Found Result")
//                    Timber.d(base64)
                    onResult(base64)
                }
            }
        }
    })

    val cropState = imageCropper.cropState
    if (cropState != null) ImageCropperDialog(state = cropState)
}