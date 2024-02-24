package com.cityof.glendale.screens.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.network.responses.isGroupEmission
import com.cityof.glendale.network.responses.isNoActivity
import com.cityof.glendale.utils.xt2Digit
import ir.kaaveh.sdpcompose.sdp
import java.io.ByteArrayOutputStream


private val instagramPkg = "com.instagram.android"
private val faceBookPkg = "com.facebook.katana"
private val twitterPkg = "com.twitter.android"

private val faceBookContent = UIStr.ResStr(R.string.msg_share_facebook)
private val instXContent = UIStr.ResStr(R.string.msg_share_instagram_x)


private var data: Uri? = null

@Composable
fun ShareContentScreen(
    state: HomeContract.State, onDismiss: () -> Unit
) {


    val context = LocalContext.current
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //val data: Intent? = result.data
                Toast.makeText(context,
                    context.getString(R.string.your_post_shared_successfully), Toast.LENGTH_SHORT).show()
            }
        }


    DialogApp(onDismiss = { }) {
        Surface(
            modifier = Modifier.wrapContentSize(), shape = MaterialTheme.shapes.medium
        ) {
            Column {

                Image(
                    painterResource(id = R.drawable.ic_x),
                    contentDescription = "",
                    alignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                        .padding(end = 16.dp, top = 16.dp)
                        .noRippleClickable(onDismiss)
                )

                val screenShotComposable = screenshotComposable(content = {
                    ScreenShotContent(state)
                })

                if (state.isLoading.not() && state.monthEmission?.isNoActivity()?.not() == true) EmissionComposable(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(
                            start = 16.dp, end = 16.dp, bottom = 12.dp
                        ),
                    title = UIStr.ResStr(R.string.climate_mobility),
                    value = UIStr.Str(state.monthEmission?.availablePoints?.xt2Digit() ?: ""),
                    desc = UIStr.ResStr(R.string.hive_points)
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {


                    Text(
                        text = stringResource(R.string.let_others_know),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.padding(10.dp))
                    Row {

                        Image(painter = painterResource(id = R.drawable.ic_facebook), // Replace with your SVG resource
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Transparent)
                                .clickable {
                                    val bitmap = screenShotComposable.invoke()
                                    sendData(
                                        context, faceBookPkg, UIStr.Str(
                                            state.socialMediaTemplate?.facebookPostTemplate ?: ""
                                        ), bitmap
                                    ).let {
                                        postDataToSocialMedia(context, instagramPkg) {
                                            resultLauncher.launch(it)
                                        }
                                    }
//                                    callBack.invoke(SocialType.FACEBOOK, bitmap)
                                } // Optional: Set a background color
                        )
                        Spacer(modifier = Modifier.padding(10.dp))

                        Image(painter = painterResource(id = R.drawable.ic_instagram), // Replace with your SVG resource
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Transparent)
                                .clickable {
                                    val bitmap = screenShotComposable.invoke()
                                    sendData(
                                        context, instagramPkg, UIStr.Str(
                                            state.socialMediaTemplate?.instagramPostTemplate ?: ""
                                        ), bitmap
                                    ).let {
                                        postDataToSocialMedia(context, instagramPkg) {
                                            resultLauncher.launch(it)
                                        }
                                    }
//                                    callBack.invoke(SocialType.INSTAGRAM, bitmap)
                                } // Optional: Set a background color
                        )
                        Spacer(modifier = Modifier.padding(10.dp))

                        Image(painter = painterResource(id = R.drawable.ic_twitter_x), // Replace with your SVG resource
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Transparent)
                                .clickable {
                                    val bitmap = screenShotComposable.invoke()
                                    sendData(
                                        context, twitterPkg, UIStr.Str(
                                            state.socialMediaTemplate?.twitterPostTemplate ?: ""
                                        ), bitmap
                                    ).let {
                                        postDataToSocialMedia(context, twitterPkg) {
                                            resultLauncher.launch(it)
                                        }
                                    }
//                                    callBack.invoke(SocialType.TWITTER, bitmap)
                                } // Optional: Set a background color
                        )

                    }
                }
            }
        }

    }


}

@Composable
fun ScreenShotContent(state: HomeContract.State) {

    Box(
        modifier = Modifier
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()

        ) {

            Image(
                painter = painterResource(id = R.drawable.globe_with_bee), // Replace with your SVG resource
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.Transparent) // Optional: Set a background color
            )

            Text(
                text = stringResource(R.string.emissions_reduced_this_month),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.sdp))
            ThisMonthComposable2(state = state)
//            Spacer(modifier = Modifier.height(12.sdp))
        }
    }

}

@Composable
fun ThisMonthComposable2(state: HomeContract.State) {

    val emission = state.monthEmission

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = 100.sdp
            )
    ) {


        if (state.isLoading.not()){
            if (emission?.isNoActivity() == true){
                NoActivityComposable()
            } else{
                Column {
                    EmissionComposable(
                        title = UIStr.Str(state.userName),
                        value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    if (emission?.isGroupEmission() == true) EmissionComposable(
                        title = UIStr.Str(state.group),
                        value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    EmissionComposable(
                        title = UIStr.ResStr(R.string.community),
                        value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                }
            }
        }

//        if (state.isLoading.not()) Column {
//            EmissionComposable(
//                title = UIStr.Str(state.userName),
//                value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            if (emission?.isGroupEmission() == true) EmissionComposable(
//                title = UIStr.Str(state.group),
//                value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            EmissionComposable(
//                title = UIStr.ResStr(R.string.community),
//                value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//        }

        if (state.isLoading) CircularProgressIndicator()
    }
}


@Composable
fun screenshotComposable(content: @Composable () -> Unit): () -> Bitmap {
    val context = LocalContext.current
    val composeView = remember { ComposeView(context = context) }
    fun captureBitmap(): Bitmap = composeView.drawToBitmap()
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content()
                }
            }
        },
        modifier = Modifier.wrapContentSize(unbounded = false)  //  Make sure to set unbounded true to draw beyond screen area
    )

    return ::captureBitmap
}


fun sendData(context: Context, pkg: String, description: UIStr, bitmap: Bitmap): Intent {

    if (data == null) data = getUriFromBitmap(context, bitmap)
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_TEXT, description.toStr(context))
    intent.putExtra(Intent.EXTRA_STREAM, data)
    intent.setPackage(pkg)
    // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    return intent

}

fun postDataToSocialMedia(context: Context, pkg: String, launch: () -> Unit) {
    try {
        launch()
    } catch (e: ActivityNotFoundException) {
        // Package name for Google Play Store
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")
                )
            )
        } catch (e: ActivityNotFoundException) {
            // If the Play Store app is not installed, open the Play Store website
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                )
            )
        }

    } catch (e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}


//fun sendData(context: Context, pkg: String, description: String, bitmap: Bitmap): Intent? {
//
////    if (data == null) {
//    val data = getUriFromBitmap(context, bitmap)
////    }
//    return try {
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_TEXT, description)
//        intent.putExtra(Intent.EXTRA_STREAM, data)
//        intent.setPackage(pkg)
//        // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
////        resultLauncher.launch(intent)
//
//    } catch (e: ActivityNotFoundException) {
//        // Package name for Google Play Store
//        try {
//            context.startActivity(
//                Intent(
//                    Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")
//                )
//            )
//        } catch (e: ActivityNotFoundException) {
//            // If the Play Store app is not installed, open the Play Store website
//            context.startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
//                )
//            )
//        }
//        null
//
//    } catch (e: Exception) {
//        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//        null
//    }
//}


private fun getUriFromBitmap(context: Context, bitmap: Bitmap): Uri {

    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path: String = MediaStore.Images.Media.insertImage(
        context.contentResolver, bitmap, "Title", null
    )
    return Uri.parse(path)


//        val bytes = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//
//        val contentValues = ContentValues().apply {
//            put(MediaStore.Images.Media.DISPLAY_NAME, "Title")
//            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            // Add other metadata if needed
//        }
//
//        val resolver = contentResolver
//        val uris = resolver.insert(MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), contentValues)
//
//        uris?.let { uri ->
//            resolver.openOutputStream(uri)?.use { outputStream ->
//                outputStream.write(bytes.toByteArray())
//            }
//        }
//        data = uris
}