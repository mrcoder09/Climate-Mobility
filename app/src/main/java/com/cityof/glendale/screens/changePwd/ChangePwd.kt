package com.cityof.glendale.screens.changePwd

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyleLarge
import ir.kaaveh.sdpcompose.sdp

@Composable
fun ChangePwdDialog(
    showDialog: MutableState<Boolean>, onDismiss: () -> Unit = {

    }
) {

    val viewModel = viewModel<ChangePwdViewModel>()
    val state by viewModel.state.collectAsState()

    if (showDialog.value)
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.sdp),
                color = Color.White
            ) {
                Spacer(modifier = Modifier.height(10.sdp))
                Row(
                    horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        viewModel.dispatch(ChangePwdContract.Intent.Close)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_x),
                            contentDescription = null
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
                    Spacer(modifier = Modifier.height(40.sdp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_change_pwd),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.height(40.sdp))
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
                        label = stringResource(R.string.msg_password), valueChanged = {
                            viewModel.dispatch(ChangePwdContract.Intent.PwdEdited(it))
                        }, error = state.pwdErr.toStr()
                    )
                    AppPasswordField(label = stringResource(R.string.confirm_pwd), valueChanged = {
                        viewModel.dispatch(ChangePwdContract.Intent.ConfirmPwdEdited(it))
                    }, error = state.confirmPwdErr.toStr())
                    Spacer(modifier = Modifier.height(32.sdp))
                    AppButton(title = stringResource(R.string.create)) {
                        viewModel.dispatch(ChangePwdContract.Intent.Submit)
                    }
                    Spacer(modifier = Modifier.height(16.sdp))
                }
            }
        }
}

@Preview
@Composable
fun ShowChangePwd() {

    val showDialog = remember { mutableStateOf(true) }
    ChangePwdDialog(showDialog = showDialog) {

    }
}