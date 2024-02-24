package com.cityof.glendale.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AnnotatedClickableText
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.AppTextField
import com.cityof.glendale.composables.components.EMAIL_LENGTH
import com.cityof.glendale.composables.components.LabelledCheckbox
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.login.LoginContract.Intent
import com.cityof.glendale.screens.login.LoginContract.NavAction
import com.cityof.glendale.theme.FF434343
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.LaunchBiometricFlow
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

private const val TAG = "LoginScreen"


@Composable
@Preview(showSystemUi = true)
@Preview(showSystemUi = true, locale = "es-rES")
@Preview(showSystemUi = true, locale = "hy")
fun LoginPreview() {
    LoginScreen(
        viewModel = LoginViewModel(
            InputValidatorImpl(),
            AppPreferencesManagerImpl(LocalContext.current.appDataStore),
            AppRepository(MockApiService())
        )
    )
}

@Composable
fun LoginScreen(
    navController: NavHostController? = null, viewModel: LoginViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
//    val biometricCredentials by viewModel.biometricCredentials.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

//    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = Unit, block = {
        viewModel.dispatch(Intent.SetData)
    })

//    LaunchedEffect(Unit) {
//        scrollState.animateScrollTo(0)
//    }

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavDashboard -> {
                navController?.navigate(Routes.Dashboard.name) {
                    if (AppConstants.isFromLogout) {
                        popUpTo(Routes.Login.name) {
                            inclusive = true
                        }
                    } else {
                        popUpTo(Routes.Video.name) {
                            inclusive = true
                        }
                    }
                }
            }

            NavAction.NavForgotPassword -> navController?.navigate(Routes.ForgotPwd.name)
            NavAction.NavSignup -> navController?.navigate(Routes.SignUp.name)
            null -> {}
        }
    })

    ToastApp(state.msgToast)
    ProgressDialogApp(state.isLoading)


    LaunchBiometricFlow(
        launchFlow = state.biometricLogin,
        credentials = state.biometricCredentials,
        success = {
            viewModel.dispatch(Intent.FingerSensorClicked())
            viewModel.dispatch(Intent.BiometricLogin(it))
        }, failed = {
            viewModel.dispatch(Intent.FingerSensorClicked())
        })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        BackgroundWithLogo(topMargin = 30)
        Spacer(modifier = Modifier.height(24.sdp))
        Image(painter = painterResource(id = R.drawable.globe_with_bee), contentDescription = null)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.sdp)
        ) {
            Spacer(modifier = Modifier.height(30.sdp))
            TitleWithDesc(title = R.string.welcome_back, desc = R.string.please_login)
            Spacer(modifier = Modifier.height(12.sdp))
            AppTextField(value = state.email,
                label = stringResource(R.string.msg_email),
                error = state.emailErr.toStr(),
                maxLength = EMAIL_LENGTH,
                valueChanged = {
                    viewModel.dispatch(Intent.EmailEdited(it))
                })
            PasswordWithThumb(value = state.password, err = state.pwdErr.toStr(), onValueChanged = {
                viewModel.dispatch(Intent.PwdEdited(it))
            }, onThumbClick = {
                Log.d(TAG, "LoginScreen: onThumbClick")
                viewModel.dispatch(Intent.FingerSensorClicked(true))
            })

            Spacer(modifier = Modifier.height(4.sdp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelledCheckbox(
                    value = state.isRememberMe,
                    label = stringResource(R.string.remember_me),
                    textStyle = baseStyle().copy(
                        fontSize = 13.ssp, lineHeight = 18.ssp, color = FF434343
                    )
                ) {
                    viewModel.dispatch(Intent.RememberMe(it))
                }
                Spacer(modifier = Modifier.width(8.sdp))
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    modifier = Modifier.noRippleClickable {
                        viewModel.dispatch(Intent.ForgotPwdClicked)
                    },
                    style = baseStyle().copy(color = Purple),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(32.sdp))
            AppButton(title = stringResource(R.string.signin)) {
                viewModel.dispatch(Intent.LoginClicked)
            }
            Spacer(modifier = Modifier.height(12.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                AnnotatedClickableText(
                    text = stringResource(R.string.dont_have_account),
                    clickableText = stringResource(id = R.string.create_account)
                ) {
                    viewModel.dispatch(Intent.SignUpClicked)
                }
            }
            Spacer(modifier = Modifier.height(12.sdp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.beeline_road),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}


@Composable
//@Preview
fun PasswordWithThumb(
    value: String = "",
    err: String = "",
    onValueChanged: (String) -> Unit = {},
    onThumbClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        AppPasswordField(
            label = stringResource(R.string.msg_password),
            value = value,
            modifier = Modifier.weight(3f),
            valueChanged = onValueChanged,
            error = err
        )
        Spacer(modifier = Modifier.width(4.sdp))
        Image(
            painter = painterResource(id = R.drawable.ic_fingersensor),
            contentDescription = "Image",
            modifier = Modifier
                .padding(top = 2.sdp)
                .height(58.sdp)
                .clickable {
                    Log.d(TAG, "PasswordWithThumb: onClick")
                    onThumbClick()
                },
            contentScale = ContentScale.Fit
        )
    }
}


//@Composable
//@Preview
//fun FingerSensorDialog(showDialog: Boolean = true, onDismissRequest: () -> Unit = {}) {
//    if (showDialog) Dialog(onDismissRequest = onDismissRequest) {
//        Surface(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(10.sdp),
//            color = Color.White
//        ) {
//            Spacer(modifier = Modifier.height(10.sdp))
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        horizontal = 16.sdp
//                    ),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Spacer(modifier = Modifier.height(28.sdp))
//                TitleWithDesc(
//                    title = R.string.beeline_login,
//                    desc = R.string.msg_login_finger,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_thumb_lines),
//                    contentDescription = ""
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                AppClickableText(
//                    stringRes = R.string.use_account_password, onClick = {
//                        onDismissRequest()
//                    }, spanStyle = SpanStyle(
//                        color = Green,
//                        fontWeight = FontWeight.W500,
//                        fontSize = 12.ssp,
//                        fontFamily = RobotoFontFamily
//                    )
//                )
//                Spacer(modifier = Modifier.height(28.sdp))
//            }
//        }
//    }
//}
//
//
//@Composable
//@Preview
//fun FaceIdDialog() {
//    if (true) Dialog(onDismissRequest = {}) {
//        Surface(
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(10.sdp),
//            color = Color.White
//        ) {
//            Spacer(modifier = Modifier.height(10.sdp))
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        horizontal = 16.sdp
//                    ),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Spacer(modifier = Modifier.height(28.sdp))
//                TitleWithDesc(
//                    title = R.string.beeline_login,
//                    desc = R.string.msg_face_id,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_faceid), contentDescription = ""
//                )
//                Spacer(modifier = Modifier.height(12.sdp))
//                AppClickableText(
//                    stringRes = R.string.use_account_password, onClick = {
//
//                    }, spanStyle = SpanStyle(
//                        color = Green,
//                        fontWeight = FontWeight.W500,
//                        fontSize = 12.ssp,
//                        fontFamily = RobotoFontFamily
//                    )
//                )
//                Spacer(modifier = Modifier.height(28.sdp))
//            }
//        }
//    }
//}

