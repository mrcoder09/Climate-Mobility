package com.cityof.glendale.screens.more

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.cityof.glendale.BuildConfig
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.screens.createImageFile
import com.cityof.glendale.theme.ERR_RED
import com.cityof.glendale.utils.RealPathUtil
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.Objects

const val SPACING_HORIZONTAL = 26
const val CORNER_RADIUS = 16

data class PhotoPicker(
    val name: UIStr, @DrawableRes val icon: Int
)

fun PhotoPicker.isDelete() = (icon == R.drawable.ic_circled_delete)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PhotoPickerSheet(
    showDelete: Boolean = false,
    onImageSelected: (String?) -> Unit = {},
    onImageDelete: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded =true,confirmValueChange = { false })

    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }

    when (selectedIndex) {
        0 -> {
            CameraPicker {
                coroutineScope.launch {
                    sheetState.hide()
                    onImageSelected(it)
                }
            }
        }

        1 -> {
            GalleryPicker {
                coroutineScope.launch {
                    sheetState.hide()
                    onImageSelected(it)
                }
            }
        }

        2 -> {
            LaunchedEffect(key1 = Unit, block = {
                coroutineScope.launch {
                    sheetState.hide()
                    onImageDelete()
                }
            })

        }
    }

//    Scaffold(content = { innerPadding ->
//            Box(modifier = Modifier.padding(
//                PaddingValues(bottom = innerPadding.calculateBottomPadding())
//            )){
        ModalBottomSheet(
            containerColor = Color.White, onDismissRequest = {}, shape = RoundedCornerShape(
                topStart = CORNER_RADIUS.sdp, topEnd = CORNER_RADIUS.sdp
            ), sheetState = sheetState, dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(
                        start = SPACING_HORIZONTAL.sdp,
                        end = SPACING_HORIZONTAL.sdp,
                        bottom = 48.sdp
                    )
            ) {
                Row(
                    modifier = Modifier.padding(
                        top = 20.sdp, bottom = 16.sdp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.profile_picture),
                        style = baseStyleLarge().copy(color = Color.Black,
                            fontSize = 16.ssp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier.noRippleClickable {
                            onImageSelected(null)
                        })
                }

                val list = stringArrayResource(id = R.array.photo_picker_option).zip(
                    arrayOf(
                        R.drawable.ic_circled_camer,
                        R.drawable.ic_circled_gallery,
                        R.drawable.ic_circled_delete
                    )
                ) { name, icon ->
                    PhotoPicker(
                        UIStr.Str(name), icon
                    )
                }.toMutableList()

                if (showDelete.not()) {
                    list.removeAt(list.lastIndex)
                }

                list.forEachIndexed { index, photoPicker ->
                    PickerComposable(item = photoPicker) {
                        selectedIndex = index
                        Timber.d("$index")
                    }
//                    if (photoPicker.isDelete().not()) HorizontalDivider()
                    HorizontalDivider()
                }
            }
        }
//            }
//    })


}


@Composable
fun PickerComposable(
    item: PhotoPicker, onClick: (PhotoPicker) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.sdp)
            .noRippleClickable {
                onClick(item)
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = item.icon), contentDescription = null)
        Spacer(modifier = Modifier.width(12.sdp))
        Text(
            item.name.toStr(), style = baseStyle().copy(
                color = if (item.isDelete()) ERR_RED else Color.Black,
                fontWeight = FontWeight.Normal
            )
        )
    }
}


@Composable
fun GalleryPicker(onResult: (String?) -> Unit) {

    var selectedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    if (selectedImageUri != Uri.EMPTY) {
        Timber.d("onImage selected")

//        val temp = RealPathUtil.getRealPath(LocalContext.current, selectedImageUri)
        EasyImageCropper(uri = selectedImageUri) { base64 ->
            onResult(base64)
        }
    }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    selectedImageUri = uri
                }
            })

    LaunchedEffect(key1 = Unit, block = {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    })

}

@Composable
fun CameraPicker(onResult: (String?) -> Unit) {
    val context = LocalContext.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file
    )

    var capturedImageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    if (capturedImageUri != Uri.EMPTY) {
        Timber.d("onImage selected")
        EasyImageCropper(uri = capturedImageUri) { base64 ->
            onResult(base64)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        Timber.d("OnRESULT $it")
        if (it) {
            capturedImageUri = uri
            val temp = RealPathUtil.getRealPath(context, capturedImageUri)
            Timber.d("$capturedImageUri")
            Timber.d(temp)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(uri)
        } else {
//            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            Toast.makeText(
                context,
                "Camera Permission is Denied, Permission is required to use this feature.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val permissionCheckResult =
        ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)

    LaunchedEffect(key1 = Unit) {
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(uri)
        } else {
            // Request a permission
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}


fun doCompress() {

}


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


//@Composable
//fun OpenCroper {
//
//    val context = LocalContext.current
//    val cropImage = rememberLauncherForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            // Use the returned uri.
//            val uriContent = result.uriContent
//            val uriFilePath = result.getUriFilePath(context) // optional usage
//        } else {
//            // An error occurred.
//            val exception = result.error
//        }
//    }
//
//
//
//
//    LaunchedEffect(key1 = Unit, block = {
//        cropImage.launch(
//            options {
//                setImagePickerContractOptions(
//                    PickImageContractOptions(includeGallery = true, includeCamera = false)
//                )
//            }
//        )
//    })
//
//    fun startCrop() {
//        // Start picker to get image for cropping and then use the image in cropping activity.
//
//
//        // Start picker to get image for cropping from only gallery and then use the image in cropping activity.
//        cropImage.launch(
//            options {
//                setImagePickerContractOptions(
//                    PickImageContractOptions(includeGallery = true, includeCamera = false)
//                )
//            }
//        )
//
//        // Start cropping activity for pre-acquired image saved on the device and customize settings.
//        cropImage.launch(
//            options(uri = imageUri) {
//                setGuidelines(Guidelines.ON)
//                setOutputCompressFormat(CompressFormat.PNG)
//            }
//        )
//    }
//}