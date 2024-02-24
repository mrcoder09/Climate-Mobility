package com.cityof.glendale.screens.more

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.cityof.glendale.BuildConfig
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppClickableText
import com.cityof.glendale.composables.components.ButtonWithLeadingIcon
import com.cityof.glendale.composables.components.LanguageContextDropDown
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.Endpoints
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.more.MoreContract.Intent
import com.cityof.glendale.screens.more.MoreContract.MoreItem
import com.cityof.glendale.screens.more.MoreContract.NavAction
import com.cityof.glendale.screens.more.MoreContract.State
import com.cityof.glendale.screens.more.profileSettings.HORIZONTAL_PADDING
import com.cityof.glendale.theme.MORE_DIVIDER
import com.cityof.glendale.theme.MORE_DIVIDER_GRAY
import com.cityof.glendale.theme.PROFILE_RED_BUTTON
import com.cityof.glendale.theme.RobotoFontFamily
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.LangHelper
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


private const val TAG = "MoreScreen"

@Composable
fun titleStyle() = baseStyleLarge().copy(fontSize = 21.ssp, lineHeight = 28.ssp)

@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun MoreScreenPreview() {
    MoreScreen(
        viewModel = MoreViewModel(
            appRepository = AppRepository(MockApiService()),
            preferenceManager = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}

/**
 * this is not available in Nav Graph. Keep in mind
 */
@Composable
@Preview
fun MoreScreen(
    navHostController: NavHostController? = null,
    viewModel: MoreViewModel = hiltViewModel()
) {
    val privacyStatement = stringResource(R.string.privacy_statement)
    val licenseAgreement = stringResource(R.string.license_agreement)
    val faqs = stringResource(R.string.faqs)

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.dispatch(Intent.Init)
    })

    LogoutDialog(state.showLogout, {
        viewModel.dispatch(
            Intent.LogoutClicked(false)
        )
        viewModel.dispatch(Intent.Logout)
    }, {
        viewModel.dispatch(
            Intent.LogoutClicked(false)
        )
    })

    ToastApp(
        state.msgToast
    )

    ProgressDialogApp(
        show = state.isLoading
    )

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavContactUs -> {
                navHostController?.navigate(Routes.ContactUs.name)
            }

//            NavAction.NavHelp -> {}
//            NavAction.NavLanguage -> {}
//            NavAction.NavLicense -> {}
//            NavAction.NavNotification -> {
//                navHostController?.navigate(Routes.NotificationSetting.name)
//            }

            NavAction.NavProfile -> {
//                navHostController?.currentBackStackEntry?.savedStateHandle?.set(LOAD_PROFILE, true)
                AppConstants.isLoadProfile = true
                navHostController?.navigate(Routes.ProfileSetting.name)
            }

            NavAction.NavLanguage -> {}
            NavAction.NavNotification -> {
                navHostController?.navigate(Routes.NotificationSetting.name)
            }

            NavAction.NavContactUs -> {
                navHostController?.navigate(Routes.ContactUs.name)
            }

            NavAction.NavLicense -> {
//                val title = stringResource(R.string.license_agreement)
                val encodedUrl = URLEncoder.encode(
                    Endpoints.LICENSE_AGREEMENT, StandardCharsets.UTF_8.toString()
                )
                navHostController?.navigate("WebView/$licenseAgreement/$encodedUrl")
            }

            NavAction.NavPrivacyPolicy -> {
//                val title = stringResource(R.string.privacy_statement)
                val encodedUrl =
                    URLEncoder.encode(Endpoints.PRIVACY_POLICY, StandardCharsets.UTF_8.toString())
                navHostController?.navigate("WebView/$privacyStatement/$encodedUrl")
            }

            NavAction.NavHelp -> {

                val encodedUrl = URLEncoder.encode(Endpoints.FAQ, StandardCharsets.UTF_8.toString())
                navHostController?.navigate("WebView/$faqs/$encodedUrl")
            }

            NavAction.NavLogin -> {
                navHostController?.navigate(Routes.Login.name) {
                    popUpTo(Routes.Dashboard.name) {
                        inclusive = true
                    }
                }
            }

            null -> {}
        }
    })


//    PhotoPickerSheet {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppBarWithGlobe(
            showImage = false, height = 42
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            MoreHeader(state = state,
                onProfileSetting = { viewModel.dispatch(Intent.ProfileClicked) },
                onImageDelete = {
                    viewModel.dispatch(Intent.ProfilePicUpdate(""))
                },
                onImageSelected = {
                    viewModel.dispatch(Intent.ProfilePicUpdate(it ?: ""))
                })
            Box(
                modifier = Modifier
            ) {
                MoreItems { index, item ->
                    when (index) {
                        0 -> {
                            viewModel.dispatch(Intent.LanguageClicked)
                        }

                        1 -> {
                            viewModel.dispatch(Intent.NotificationClicked)
                        }

                        2 -> {
                            viewModel.dispatch(Intent.HelpClicked)

                        }

                        3 -> {
                            viewModel.dispatch(Intent.ContactUsClicked)
//                        viewModel.dispatch(Intent.LicenseClicked)
                        }

                        4 -> {
                            viewModel.dispatch(Intent.LicenseClicked)
//                        viewModel.dispatch(Intent.PrivacyPolicyClicked)
                        }

                        5 -> {
                            viewModel.dispatch(Intent.PrivacyPolicyClicked)
//                        viewModel.dispatch(Intent.HelpClicked)
                        }

                        6 -> viewModel.dispatch(Intent.LogoutClicked(true))
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Version()
        }
    }
//    }


}


@Composable
fun MoreHeader(
    state: State,
    onProfileSetting: () -> Unit = {},
    onImageDelete: () -> Unit,
    onImageSelected: (String?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
//        Text(
//            text = stringResource(R.string.app_name_other), style = baseStyle2().copy(
//                color = Color(0xFF777C80), fontSize = 16.ssp
//            ), modifier = Modifier.padding(vertical = 10.dp)
//        )

        ProfileComposable(
            state, onProfileSetting = onProfileSetting, onImageDelete, onImageSelected
        )
        Spacer(modifier = Modifier.height(8.sdp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.sdp)
                .background(MORE_DIVIDER)
        )
    }
}

@Composable
fun ProfileComposable(
    state: State,
    onProfileSetting: () -> Unit = {},
    onImageDelete: () -> Unit = {},
    onImageSelected: (String?) -> Unit
) {


    var showSheet by remember {
        mutableStateOf(false)
    }

    if (showSheet) {
        PhotoPickerSheet(showDelete = state.profileUrl.isNullOrEmpty().not(), onImageDelete = {
            showSheet = false
            onImageDelete()
        }, onImageSelected = {
            showSheet = false
            it?.let {
                onImageSelected(it)
            }
        })
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.sdp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {

//            SubcomposeAsyncImage(
//                model = ImageRequest.Builder(LocalContext.current).data(state.profileUrl)
//                    .transformations(CircleCropTransformation())
//                    .placeholder(R.drawable.profile_placeholder)
//                    .error(R.drawable.profile_placeholder).crossfade(true).build(),
//                contentDescription = "profile image",
//                modifier = Modifier
//                    .size(70.sdp)
//                    .clip(
//                        CircleShape
//                    ),
//                contentScale = ContentScale.FillBounds,
//                loading = {
//                    CircularProgressIndicator()
//                }
//            )
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current).data(state.profileUrl)
//                    .transformations(CircleCropTransformation())
//                    .placeholder(R.drawable.profile_placeholder)
//                    .error(R.drawable.profile_placeholder).crossfade(true).build(),
//                contentDescription = "profile image",
//                modifier = Modifier
//                    .size(70.sdp)
//                    .clip(
//                        CircleShape
//                    ),
//                contentScale = ContentScale.FillBounds
//            )

            CircledImageWithBorder(
                state.profileUrl ?: "", size = 70, paddingTop = 0
            )
//            ProfileImage(state.profileUrl ?: "")
            Image(painter = painterResource(id = R.drawable.ic_more_edit),
                contentDescription = "Profile Edit",
                modifier = Modifier.noRippleClickable {
                    showSheet = true
                })
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(
                    top = 20.sdp, bottom = 20.sdp, end = 10.sdp,
                    start = 10.sdp
                )
                .noRippleClickable {
                    onProfileSetting()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.profile_settings),
                style = titleStyle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                state.userName, style = baseStyle().copy(
//                    color = MORE_VIEW_PROFILE
                    fontWeight = FontWeight.Normal, fontSize = 14.ssp
                )
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_next),
            contentDescription = "Profile Next",
            modifier = Modifier.noRippleClickable {
                onProfileSetting()
            }
        )
    }
}


@Composable
@Preview
fun ProfileImage(profileUrl: String = "") {


    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(model = ImageRequest.Builder(LocalContext.current).data(profileUrl)
            .transformations(CircleCropTransformation()).placeholder(R.drawable.profile_placeholder)
            .error(R.drawable.profile_placeholder).crossfade(true).build(),
            contentDescription = "profile image",
            modifier = Modifier
                .size(70.sdp)
                .clip(
                    CircleShape
                ),
            contentScale = ContentScale.FillBounds,
            onLoading = { isLoading = true },
            onError = { isLoading = false },
            onSuccess = { isLoading = false })
        if (isLoading) {
            CircularProgressIndicator()
        }
    }

}

@Composable
fun MoreItems(onClick: (Int, MoreItem) -> Unit) {

    val titles = stringArrayResource(id = R.array.arr_settings)
    Column {
        val list = arrayOf(
            R.drawable.ic_setting_language,
            R.drawable.ic_setting_notification,
            R.drawable.ic_setting_help,
            R.drawable.ic_setting_contactus,
            R.drawable.ic_setting_license,
            R.drawable.ic_setting_privacy,
            R.drawable.ic_setting_logout
        ).zip(titles) { icon, title ->
            MoreItem(title = title, route = "", id = painterResource(id = icon))
        }

        list.forEachIndexed { index, item ->
            when (index) {
                0 -> {
                    LanguageItem(item = item)
                    HorizontalDivider(
                        color = MORE_DIVIDER_GRAY,
                        modifier = Modifier.padding(horizontal = 16.sdp),
                        thickness = 0.5.dp
                    )
                }

                list.lastIndex -> LogoutItem(item) {
                    onClick(index, item)
                }

                else -> {
                    MoreItem(item = item) { item ->
                        onClick(index, item)
                    }
                    HorizontalDivider(
                        color = MORE_DIVIDER_GRAY,
                        modifier = Modifier.padding(horizontal = 16.sdp),
                        thickness = 0.5.dp
                    )
                }

            }


        }
    }
}

@Composable
fun LanguageItem(item: MoreItem) {

    val context = LocalContext.current
    var isPopupVisible by rememberSaveable { mutableStateOf(false) }
    val lang = LangHelper.localeToName(LocalContext.current)
    val selectedLang by remember { mutableStateOf(lang) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.sdp, vertical = 12.sdp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = item.id, contentDescription = item.title
        )
        Text(
            item.title, modifier = Modifier
                .weight(3f)
                .padding(
                    horizontal = 8.sdp
                ), textAlign = TextAlign.Start, style = baseStyle2().copy(
                fontWeight = FontWeight.W400, fontSize = 16.ssp
            )
        )

        Column {
            AppClickableText(
                modifier = Modifier, value = selectedLang, onClick = {
                    isPopupVisible = true
                }, spanStyle = SpanStyle(
                    fontWeight = FontWeight.W400, fontSize = 14.ssp, fontFamily = RobotoFontFamily
                )
            )
            if (isPopupVisible) {
                LanguageContextDropDown(isPopupVisible) { locale ->
                    isPopupVisible = false
                    locale?.let {
                        LangHelper.setLocale(context, locale)
                        (context as Activity).recreate()
                    }
                }
            }
        }

    }
}


@Composable
fun LogoutItem(item: MoreItem, onClick: (MoreItem) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .noRippleClickable {
            onClick(item)
        }
        .padding(
            horizontal = 16.sdp, vertical = 12.sdp
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = item.id, contentDescription = item.title
        )
        Text(
            item.title, modifier = Modifier
                .weight(3f)
                .padding(
                    horizontal = 8.sdp
                ), textAlign = TextAlign.Start, style = baseStyle2()
        )
    }
}


@Composable
fun MoreItem(item: MoreItem, onClick: (MoreItem) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .noRippleClickable {
            onClick(item)
        }
        .padding(
            horizontal = 16.sdp, vertical = 12.sdp
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = item.id, contentDescription = item.title
        )
        Text(
            item.title, modifier = Modifier
                .weight(3f)
                .padding(
                    horizontal = 8.sdp
                ), textAlign = TextAlign.Start, style = baseStyle2()
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_next),
            contentDescription = "${item.title} next"
        )
    }
}

@Composable
fun Version() {

    val fontStyle = baseStyle().copy(
        fontSize = 12.ssp, lineHeight = 14.ssp, fontWeight = FontWeight.Normal
    )

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.sdp))
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
            style = fontStyle
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            modifier = Modifier.padding(
                horizontal = 14.sdp
            ),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.copyright_2023_beeline_all_rights_reserved),
            style = fontStyle
        )
        Spacer(modifier = Modifier.height(20.sdp))
    }
}


@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun LogoutDialog(
    show: Boolean = true, onClick: () -> Unit = {}, onDismiss: () -> Unit = {}
) {

    if (show) Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ), onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    all = HORIZONTAL_PADDING.sdp
                ), shape = RoundedCornerShape(10.sdp), color = Color.White
        ) {
            Spacer(modifier = Modifier.height(10.sdp))
            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = null,
                    modifier = Modifier
                        .noRippleClickable(onDismiss)
                        .padding(
                            all = 10.sdp
                        )
                )
//                IconButton(onClick = onDismiss) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_x), contentDescription = null
//                    )
//                }
            }
            Spacer(modifier = Modifier.height(10.sdp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.sdp
                    )
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(40.sdp))
                Image(
                    painter = painterResource(id = R.drawable.ic_logout_circled),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(20.sdp))
                TitleWithDesc(
                    title = R.string.title_are_you_sure,
                    desc = R.string.msg_want_logout,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    titleStyle = baseStyleLarge().copy(
                        color = Color.Black
                    ),
                    descStyle = baseStyle2().copy(
                        fontSize = 14.ssp, lineHeight = 20.ssp
                    )
                )
                Spacer(modifier = Modifier.height(32.sdp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 5.sdp
                        ), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ButtonWithLeadingIcon(
                        modifier = Modifier
                            .height(46.sdp)
                            .weight(1f),
                        style = StyleButton().copy(
                            lineHeight = TextUnit(15f, TextUnitType.Sp)
                        ),
                        title = stringResource(R.string.logout),
                        icon = R.drawable.ic_logout,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White, containerColor = PROFILE_RED_BUTTON
                        ),
                        onClick = onClick
                    )
                    Spacer(modifier = Modifier.width(12.sdp))
                    AppButton(
                        modifier = Modifier
                            .height(46.sdp)
                            .weight(1f), style = StyleButton().copy(
                            lineHeight = TextUnit(15f, TextUnitType.Sp)
                        ), title = stringResource(id = R.string.cancel), onClick = onDismiss
                    )
                }
                Spacer(modifier = Modifier.height(24.sdp))
            }
        }
    }
}
