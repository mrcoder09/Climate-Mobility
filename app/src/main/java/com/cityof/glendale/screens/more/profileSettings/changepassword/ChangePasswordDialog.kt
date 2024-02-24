package com.cityof.glendale.screens.more.profileSettings.changepassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.more.profileSettings.HORIZONTAL_PADDING
import com.cityof.glendale.screens.more.profileSettings.changepassword.ChangePwdContract.Intent
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp

@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun ChangePwdPreview() {

    ChangePasswordDialog(
        viewModel = ChangePwdViewModel(
            InputValidatorImpl(),
            AppRepository(MockApiService()),
            AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}


@Composable
fun ChangePasswordDialog(
    viewModel: ChangePwdViewModel = hiltViewModel(),
    navController: NavHostController? = null,
    showDialog: Boolean = true,
    onDismiss: () -> Unit = {}
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            ChangePwdContract.NavAction.NavLogin -> {
                navController?.navigate(Routes.Login.name) {
                    popUpTo(Routes.Dashboard.name) {
                        inclusive = true
                    }
                }
            }

            null -> {}
        }
    })

    ToastApp(state.msgToast)
    ProgressDialogApp(state.isLoading)

    if (showDialog) Dialog(properties = DialogProperties(
        usePlatformDefaultWidth = false
    ), onDismissRequest = {
        viewModel.resetDialog()
        onDismiss()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = HORIZONTAL_PADDING.sdp
                )
                .verticalScroll(state = rememberScrollState()),
            shape = RoundedCornerShape(10.sdp),
            color = Color.White
        ) {
            Spacer(modifier = Modifier.height(10.sdp))
            Row(
                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    viewModel.resetDialog()
                    onDismiss()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x), contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.sdp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(18.sdp))
                Image(
                    painter = painterResource(id = R.drawable.ic_change_pwd),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(8.sdp))
                TitleWithDesc(
                    title = R.string.change_password,
                    desc = R.string.msg_change_pwd,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    titleStyle = baseStyleLarge().copy(
                        color = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(8.sdp))
                AppPasswordField(
                    label = stringResource(R.string.current_password),
                    value = state.currentPassword,
                    error = state.currentPwdErr.toStr(),
                    valueChanged = {
                        viewModel.dispatch(Intent.CurrentPwdEdited(it))
                    },
                )
                AppPasswordField(label = stringResource(R.string.new_password),
                    value = state.password,
                    error = state.newPasswordErr.toStr(),
                    valueChanged = {
                        viewModel.dispatch(Intent.NewPasswordEdited(it))
                    })
                AppPasswordField(label = stringResource(R.string.confirm_pwd),
                    value = state.passwordConfirmation,
                    error = state.confirmPasswordErr.toStr(),
                    valueChanged = {
                        viewModel.dispatch(Intent.ConfirmPwdEdited(it))
                    })
                Spacer(modifier = Modifier.height(16.sdp))
                AppButton(title = stringResource(R.string.submit), onClick = {
                    viewModel.dispatch(Intent.SubmitClicked)
                })
                Spacer(modifier = Modifier.height(12.sdp))
            }
        }
    }
}