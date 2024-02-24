package com.cityof.glendale.composables

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber


//This class contain app's comman UI Constructs

private const val TAG = "AppUi"


object LayoutDimens {
    //LAYOUT CONSTANTS
    const val HORIZONTAL_PADDING = 24
    const val BOTTOM_PADDING = 24
}


@Composable
fun CircleApp(
    modifier: Modifier = Modifier,
    size: Int = 60,
    color: Color = Purple,
) {
    Canvas(modifier = modifier.then(
        Modifier.size(
            size.sdp
        )
    ), onDraw = {
        drawCircle(color = color)
    })
}

@Composable
fun CircledImageWithBorder(
    url: Any = "",
    showProgressBar: Boolean = true,
    size: Int = 50,
    borderWidth: Int = 2,
    borderColor: Color = Purple,
    paddingTop: Int = 8,
    modifier: Modifier = Modifier,
    @DrawableRes placeholder: Int = R.drawable.profile_placeholder,
    @DrawableRes errorPlaceHolder: Int = R.drawable.profile_placeholder,
) {

    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.padding(top = paddingTop.sdp), contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(model = ImageRequest.Builder(LocalContext.current).data(url)
            .transformations(CircleCropTransformation()).placeholder(placeholder)
            .error(errorPlaceHolder).crossfade(true).build(),
            contentDescription = "profile image",
            modifier = Modifier
                .then(modifier)
                .size(size.sdp)
                .clip(
                    CircleShape
                )
                .border(borderWidth.dp, borderColor, shape = CircleShape),
            contentScale = ContentScale.FillBounds,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false })

        if (showProgressBar && isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun CircledImage(
    url: String = "",
    showProgressBar: Boolean = true,
    size: Int = 50,
    modifier: Modifier = Modifier.padding(top = 8.sdp),
    @DrawableRes placeholder: Int = R.drawable.profile_placeholder,
    @DrawableRes errorPlaceHolder: Int = R.drawable.profile_placeholder,
) {


    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(model = ImageRequest.Builder(LocalContext.current).data(url)
            .transformations(CircleCropTransformation()).placeholder(placeholder)
            .error(errorPlaceHolder).crossfade(true).build(),
            contentDescription = "profile image",
            modifier = Modifier
                .size(size.sdp)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false })

        if (showProgressBar && isLoading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun cornerShape(radius: Int = 6) = RoundedCornerShape(radius.sdp)

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

/**
 * Wraps an [onClick] lambda with another one that supports debouncing. The default deboucing time
 * is 1000ms.
 *
 * @return debounced onClick
 */
@Composable
inline fun debounced(crossinline onClick: () -> Unit, debounceTime: Long = 1000L): () -> Unit {
    var lastTimeClicked by remember { mutableLongStateOf(0L) }
    val onClickLambda: () -> Unit = {
        val now = SystemClock.uptimeMillis()
        if (now - lastTimeClicked > debounceTime) {
            onClick()
        }
        lastTimeClicked = now
    }
    return onClickLambda
}

/**
 * The same as [Modifier.clickable] with support to debouncing.
 */
fun Modifier.debouncedClickable(
    debounceTime: Long = 1000L, onClick: () -> Unit
): Modifier {
    return this.composed {
        val clickable = debounced(debounceTime = debounceTime, onClick = { onClick() })
        this.clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { clickable() }
    }
}

@Composable
fun LockOrientation(isPortrait: Boolean = true) {
    val activity = LocalContext.current as FragmentActivity
    val newOrientation = if (isPortrait) {
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    activity.requestedOrientation = newOrientation
}

@Composable
fun isPortrait(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

@Composable
fun BackgroundWithLogo(
    icon: Int = R.drawable.beeling_logo_new,
    topMargin: Int = 200,
    width: Int = 220,
    height: Int = 100
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(topMargin.sdp))
        Image(
            painter = painterResource(id = icon),
            modifier = Modifier.size(width.sdp, height.sdp),
            contentDescription = ""
        )
    }
}

@Composable
@Preview
fun AppBarWithBack(
    color: Color = BG_WINDOW,
    arrowTint: Color = FF777C80,
    title: String = "",
    titleColor: Color = FF777C80,
    onBackClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.sdp)
            .background(color = color),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
//        IconButton(modifier = Modifier.debouncedClickable {
//            onBackClick()
//        }) {

        Box(
            modifier = Modifier
                .fillMaxHeight().width(32.sdp)
                .debouncedClickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = arrowTint,
            )
        }

//        }
//        Image(painter = painterResource(id = R.drawable.ic_back),
//            contentDescription = null,
//            modifier = Modifier.debouncedClickable {
//                onBackClick()
//            })
        if (title.isNotEmpty()) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = baseStyle().copy(
                    fontSize = 16.ssp, color = titleColor, fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.width(42.sdp))
        }
    }
}


//@Composable
//@Preview
//fun ToolbarWithImage(
//    modifier: Modifier = Modifier,
//    height: Int = 52,
//    color: Color = BG_WINDOW,
//    title: String = stringResource(id = R.string.app_name_other),
//    titleColor: Color = FF777C80,
//    paddingEnd: Int = 20,
//    showImage: Boolean = true
//) {
//
//    val appPreferencesManagerImpl = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
//    val details = appPreferencesManagerImpl.userDetails.collectAsState(null)
//
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height.sdp)
//            .background(color = color)
//            .then(modifier), verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        if (showImage) CircledImageWithBorder(
//            url = details.value?.profilePic ?: "",
//            modifier = Modifier,
//            size = 32,
//            paddingTop = 0,
//            borderWidth = 1,
//            showProgressBar = false
//        )
//
//        Text(
//            text = title,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier.fillMaxWidth(),
//            style = baseStyle().copy(
//                fontSize = 16.ssp,
//                color = titleColor,
//                fontWeight = FontWeight.Normal,
//                textAlign = TextAlign.Center
//            )
//        )
//    }
//
//
//}


@Composable
@Preview
fun AppBarWithGlobe(
    modifier: Modifier = Modifier,
    height: Int = 52,
    color: Color = BG_WINDOW,
    title: String = stringResource(id = R.string.app_name_other),
    titleColor: Color = FF777C80,
    showImage: Boolean = true
) {

    val appPreferencesManagerImpl = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
    val details = appPreferencesManagerImpl.userDetails.collectAsState(null)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.sdp)
            .background(color = color)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showImage) {
            CircledImageWithBorder(
                url = details.value?.profilePic ?: "",
                modifier = Modifier,
                size = 32,
                paddingTop = 0,
                borderWidth = 1,
                showProgressBar = false
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.sdp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {


            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier,
                style = baseStyle().copy(
                    fontSize = 16.ssp,
                    color = titleColor,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.width(2.sdp))
            Image(
                painter = painterResource(id = R.drawable.globe_with_bee),
                contentDescription = null,
                modifier = Modifier.size(50.dp)
            )
        }
    }


}


//@Composable
//@Preview
//fun ToolbarWithImage2(
//    modifier: Modifier = Modifier,
//    height: Int = 52,
//    color: Color = BG_WINDOW,
//    title: String = stringResource(id = R.string.app_name_other),
//    titleColor: Color = FF777C80,
//    paddingEnd: Int = 20,
//    showImage: Boolean = true
//) {
//
//    val appPreferencesManagerImpl = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
//    val details = appPreferencesManagerImpl.userDetails.collectAsState(null)
//
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height.sdp)
//            .background(color = color)
//            .then(modifier), verticalAlignment = Alignment.CenterVertically
//    ) {
//
//
//        if(showImage) {
//            CircledImageWithBorder(
//                url = details.value?.profilePic ?: "",
//                modifier = Modifier,
//                size = 32,
//                paddingTop = 0,
//                borderWidth = 1,
//                showProgressBar = false
//            )
//            Spacer(modifier = Modifier.width(2.sdp))
////            Image(
////                painter = painterResource(id = R.drawable.globe_with_bee),
////                contentDescription = null,
////                modifier = Modifier.size(52.dp)
////            )
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.globe_with_bee),
//                contentDescription = null,
//                modifier = Modifier.size(25.dp)
//            )
//            Text(
//                text = title,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                modifier = Modifier,
//                style = baseStyle().copy(
//                    fontSize = 16.ssp,
//                    color = titleColor,
//                    fontWeight = FontWeight.Normal,
//                    textAlign = TextAlign.Center
//                )
//            )
//        }
//
//    }
//
//
//}

//@Composable
//@Preview
//fun ToolbarWithImage3(
//    modifier: Modifier = Modifier,
//    height: Int = 52,
//    color: Color = BG_WINDOW,
//    title: String = stringResource(id = R.string.app_name_other),
//    titleColor: Color = FF777C80,
//    paddingEnd: Int = 20,
//    showImage: Boolean = true
//) {
//
//    val appPreferencesManagerImpl = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
//    val details = appPreferencesManagerImpl.userDetails.collectAsState(null)
//
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height.sdp)
//            .background(color = color)
//            .then(modifier), verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//
//        if(showImage) {
//            CircledImageWithBorder(
//                url = details.value?.profilePic ?: "",
//                modifier = Modifier,
//                size = 32,
//                paddingTop = 0,
//                borderWidth = 1,
//                showProgressBar = false
//            )
////            Spacer(modifier = Modifier.width(2.sdp))
//
//        }
//
//        Text(
//            text = title,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier,
//            style = baseStyle().copy(
//                fontSize = 16.ssp,
//                color = titleColor,
//                fontWeight = FontWeight.Normal,
//                textAlign = TextAlign.Center
//            )
//        )
//
//        Image(
//            painter = painterResource(id = R.drawable.globe_with_bee),
//            contentDescription = null,
//            modifier = Modifier.size(52.dp)
//        )
//    }
//
//
//}

@Composable
fun ToolbarWithIcons(
    leftClick: () -> Unit, rightClick: () -> Unit, leftComposable: @Composable () -> Unit = {
        IconButton(onClick = leftClick) {
            Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
        }
    }, rightComposable: @Composable () -> Unit = {
        AppButton(
            title = "Edit",
            modifier = Modifier
                .width(78.sdp)
                .height(33.sdp),
            style = StyleButton().copy(
                fontSize = 12.ssp,
                fontWeight = FontWeight.W400,
                lineHeight = TextUnit(27f, TextUnitType.Sp)
            ),
            colors = ButtonDefaults.buttonColors(
                contentColor = Purple, containerColor = Color(0xFFEFE8FB)
            )
        ) {
            rightClick()
        }
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.sdp)
            .background(color = Color.White)
            .padding(horizontal = 8.sdp, vertical = 4.sdp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftComposable()
        Text(
            text = stringResource(id = R.string.profile),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = baseStyle().copy(fontSize = 16.ssp),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        rightComposable()
    }
}

@Composable
fun PreviewOnly() {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        AppPasswordField(label = stringResource(R.string.msg_password),
            modifier = Modifier.height(44.sdp),
            valueChanged = {

            })
        Spacer(modifier = Modifier.width(4.sdp))
        Image(painter = painterResource(id = R.drawable.ic_fingersensor),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(44.sdp)
                .clickable {

                })
    }
}


@Composable
fun SetStatusBarColor(color: Color = BG_WINDOW) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(color)
    }
}


sealed class UIStr {
    data class Str(var value: String = "") : UIStr()
    class ResStr(
        @StringRes var id: Int, vararg var args: Any
    ) : UIStr()

    @Composable
    fun toStr(): String {
        return when (this) {
            is Str -> value
            is ResStr -> stringResource(id = id, *args)
        }
    }

    fun toStr(context: Context): String {
        return when (this) {
            is Str -> value
            is ResStr -> context.getString(id, *args)
        }
    }

    fun unAuthorisedMsg(): UIStr = ResStr(R.string.msg_session_expired)
}

fun NetworkUnavailableMessage() = UIStr.ResStr(R.string.err_network)


@Composable
fun SampleForResizedText() {

    Column {
        Spacer(modifier = Modifier.height(40.sdp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 4.sdp
                ), horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .height(32.sdp)
                    .weight(1f),
                shape = RoundedCornerShape(6.sdp)
            ) {
                AutoResizedText(text = stringResource(id = R.string.change_password))
            }
            Spacer(modifier = Modifier.width(4.sdp))
            Button(
                onClick = { },
                modifier = Modifier
                    .height(32.sdp)
                    .weight(1f),
                shape = RoundedCornerShape(6.sdp)
            ) {
                Image(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "")
                AutoResizedText(text = stringResource(id = R.string.delete_account))
            }
        }
    }

}

@Composable
fun AutoResizedText(
    text: String,
    style: TextStyle = MaterialTheme.typography.headlineLarge,
    modifier: Modifier = Modifier,
    color: Color = style.color
) {

    var resizedTextStyle by remember {
        mutableStateOf(style)
    }
    var shouldDraw by remember {
        mutableStateOf(false)
    }

    val defaultFontSize = MaterialTheme.typography.headlineLarge.fontSize

    Text(text = text, color = color, modifier = modifier.drawWithContent {
        if (shouldDraw) drawContent()
    }, softWrap = false, style = resizedTextStyle, onTextLayout = { result ->
        if (result.didOverflowWidth) {
            if (style.fontSize.isUnspecified) {
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = defaultFontSize
                )
            }
            resizedTextStyle = resizedTextStyle.copy(
                fontSize = resizedTextStyle.fontSize * 0.95
            )
        } else {
            shouldDraw = true
        }
    })
}


@Composable
fun DoUnauthorization(isAuthErr: Boolean = false, navController: NavHostController? = null) {
    Timber.d("DoUnauthorization $isAuthErr")
    if (isAuthErr) {
        val context = LocalContext.current
        ToastApp(UIStr.ResStr(R.string.msg_session_expired))
        LaunchedEffect(key1 = Unit, block = {
            AppPreferencesManagerImpl(context.appDataStore).doLogout()
            navController?.navigate(Routes.Login.name) {
                popUpTo(Routes.Dashboard.name) {
                    inclusive = true
                }
                this.launchSingleTop
            }
        })
    }
}


@Composable
fun PreviewOther() {


}