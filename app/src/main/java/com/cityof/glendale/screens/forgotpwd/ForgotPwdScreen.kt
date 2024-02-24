package com.cityof.glendale.screens.forgotpwd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppTextField
import com.cityof.glendale.composables.components.EMAIL_LENGTH
import com.cityof.glendale.composables.components.MOBILE_LENGTH
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.forgotpwd.ForgotPwdContract.Intent
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.utils.InputValidatorImpl
import ir.kaaveh.sdpcompose.sdp

private const val TAG = "ForgotPwdScreen"
const val SPACE_FROM_TOOLBAR = 20




@Composable
@Preview(showSystemUi = true, showBackground = true, fontScale = 1.0f)
@Preview(showSystemUi = true, showBackground = true, fontScale = 1.0f, locale = "es-rES")
@Preview(showSystemUi = true, showBackground = true, fontScale = 1.0f, locale = "hy")
fun ForgotPasswordPreview() {
    val viewModel = ForgotPwdViewModel(
        InputValidatorImpl(), AppRepository(MockApiService())
    )
    ForgotPwdScreen(viewModel = viewModel)
}

@Composable
fun ForgotPwdScreen(
    navController: NavHostController? = null,
    viewModel: ForgotPwdViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(Unit)

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is ForgotPwdContract.NavAction.NavOtpVerify -> {
                navController?.navigate(
                    "OTP_VERIFY/" + state.email
                )
            }

            is Unit -> {}
        }
    })

    ProgressDialogApp(state.isLoading)
    ToastApp(message = state.msgToast)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_WINDOW)
    ) {
        AppBarWithBack(
            title = stringResource(id = R.string.forgot_pwd)
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
                painter = painterResource(id = R.drawable.ic_forgot_pwd), contentDescription = ""
            )
            Spacer(modifier = Modifier.height(40.sdp))
            TitleWithDesc(
                title = R.string.forgot_password,
                desc = R.string.msg_enter_email,
                titleStyle = ForgotPwdStyles.titleStyle(),
                descStyle = ForgotPwdStyles.descStyle(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            Spacer(modifier = Modifier.height(8.sdp))
            AppTextField(label = stringResource(R.string.enter_email_or_phone),
                value = state.email,
                error = state.emailErr.toStr(),
                icon = R.drawable.ic_sms,
                maxLength = if (state.isMobile) MOBILE_LENGTH else EMAIL_LENGTH,
                valueChanged = {
                    viewModel.dispatch(Intent.EmailChanged(email = it))
                })
            Spacer(modifier = Modifier.height(32.sdp))
            AppButton(title = stringResource(R.string.submit)) {
                viewModel.dispatch(Intent.Submit)
            }
            Spacer(modifier = Modifier.height(12.sdp))
        }
    }
}