package com.cityof.glendale.screens.forgotpwd.createnewpwd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.forgotpwd.ForgotPwdStyles
import com.cityof.glendale.screens.forgotpwd.SPACE_FROM_TOOLBAR
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdContract.Intent
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdContract.NavAction
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

private const val TAG = "CreatePwd"

@Preview(showSystemUi = true, showBackground = true)
@Preview(showSystemUi = true, showBackground = true, locale = "es-rES")
@Preview(showSystemUi = true, showBackground = true, locale = "hy")
@Composable
fun CreatePwdPreview() {

    CreatePwdScreen(
        viewModel = CreatePwdViewModel(
            InputValidatorImpl(),
            AppRepository(MockApiService()),
            AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}


@Composable
fun CreatePwdScreen(
    navController: NavHostController? = null,
    email: String = "",
    viewModel: CreatePwdViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)


    LaunchedEffect(key1 = Unit, block = {
        viewModel.dispatch(Intent.SetData(email))
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavLogin -> {
                navController?.navigate(Routes.Login.name) {
                    popUpTo(Routes.Login.name) {
                        inclusive = true
                    }
                }
            }

            null -> {}
        }
    })

    CongratulationDialog(showDialog = state.isCongratulations) { canNavigate ->
        viewModel.dispatch(Intent.ShowCongratulations(false))
        if (canNavigate) {
            viewModel.dispatch(Intent.NavLogin)
        }
    }
    ProgressDialogApp(state.isLoading)
//    NativeAlert(message = state.msgToast)
    ToastApp(state.msgToast)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_WINDOW)
    ) {
        AppBarWithBack(
            title = stringResource(id = R.string.create_new_password)
        ) {
            navController?.popBackStack()
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(horizontal = 24.sdp)
                )
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(SPACE_FROM_TOOLBAR.sdp))
            Image(
                painter = painterResource(id = R.drawable.ic_create_pwd), contentDescription = ""
            )
            Spacer(modifier = Modifier.height(40.sdp))
            TitleWithDesc(
                title = R.string.create_new_password,
                titleStyle = ForgotPwdStyles.titleStyle(),
                desc = R.string.msg_create_new_pwd,
                descStyle = ForgotPwdStyles.descStyle(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            Spacer(modifier = Modifier.height(8.sdp))
            AppPasswordField(value = state.password,
                label = stringResource(R.string.new_password),
                error = state.pwdErr.toStr(),
                valueChanged = {
                    viewModel.dispatch(Intent.PwdEdited(it))
                })
            AppPasswordField(value = state.passwordConfirmation,
                label = stringResource(R.string.confirm_pwd),
                error = state.confirmPwdErr.toStr(),
                valueChanged = {
                    viewModel.dispatch(Intent.ConfirmPwdEdited(it))
                })
            Spacer(modifier = Modifier.height(32.sdp))
            AppButton(title = stringResource(R.string.create)) {
                viewModel.dispatch(Intent.Submit)
            }
            Spacer(modifier = Modifier.height(12.sdp))
        }
    }
}


@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun CongratulationDialog(
    showDialog: Boolean = true, onDismiss: (Boolean) -> Unit = {}
) {

    if (showDialog) {
        DialogApp(onDismiss = { }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(22.sdp))
                Image(
                    painter = painterResource(id = R.drawable.ic_congratulations),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.sdp))
                TitleWithDesc(
                    title = R.string.congratulations,
                    titleStyle = baseStyleLarge().copy(
                        color = Color.Black, fontSize = 16.ssp
                    ),
                    desc = R.string.msg_congratulations,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                Spacer(modifier = Modifier.height(16.sdp))
                AppButton(
                    modifier = Modifier.defaultMinSize(120.sdp, DIALOG_BUTTON_HEIGHT.sdp),
                    title = stringResource(id = R.string.ok)
                ) {
                    onDismiss(true)
                }
                Spacer(modifier = Modifier.height(22.sdp))
            }
        }
    }
}