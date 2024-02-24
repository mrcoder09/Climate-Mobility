package com.cityof.glendale.composables.components

import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.cityof.glendale.R
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.cornerShape
import com.cityof.glendale.composables.isPortrait
import com.cityof.glendale.theme.FF333333
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

const val PADDING_AROUND = 18
const val DIALOG_BUTTON_HEIGHT = 40

@Composable
fun DialogApp(
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ), onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = if (isPortrait().not()) (PADDING_AROUND * 3).sdp else PADDING_AROUND.sdp,
                    vertical = PADDING_AROUND.sdp
                ), shape = RoundedCornerShape(10.sdp), color = Color.White
        ) {
            content()
        }
    }
}


@Composable
fun DialogOk(
    title: UIStr = UIStr.ResStr(R.string.app_name),
    message: UIStr = UIStr.Str(""),
    buttonText: UIStr = UIStr.ResStr(R.string.ok),
    onDismiss: () -> Unit = {}
) {

    DialogApp(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.sdp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(22.sdp))
            TitleWithDesc(
                title = title.toStr(),
                titleStyle = baseStyleLarge().copy(
                    color = FF333333, fontSize = 16.ssp
                ),
                desc = message.toStr(),
                height = 8,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            Spacer(modifier = Modifier.height(16.sdp))
            AppButton(
                modifier = Modifier.defaultMinSize(120.sdp, DIALOG_BUTTON_HEIGHT.sdp),
                title = buttonText.toStr()
            ) {
                onDismiss()
            }
            Spacer(modifier = Modifier.height(22.sdp))
        }
    }
}


@Composable
@Preview
fun DialogNoNetwork(
    show: Boolean = false,
    title: UIStr = UIStr.ResStr(R.string.app_name),
    message: UIStr = UIStr.ResStr(R.string.err_network),
    buttonText: UIStr = UIStr.ResStr(R.string.dismiss),
    onDismiss: () -> Unit = {}
) {

    if (show) {
        DialogApp(onDismiss = { }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(22.sdp))
                Spacer(modifier = Modifier.height(4.sdp))
                TitleWithDesc(
                    title = title.toStr(),
                    titleStyle = baseStyleLarge().copy(
                        color = FF333333, fontSize = 16.ssp
                    ),
                    desc = message.toStr(),
                    height = 8,
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                Spacer(modifier = Modifier.height(16.sdp))
                AppButton(
                    modifier = Modifier.defaultMinSize(120.sdp, DIALOG_BUTTON_HEIGHT.sdp),
                    title = buttonText.toStr()
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.height(22.sdp))
            }
        }
    }
}

@Composable
fun SnackBarApp() {

//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//    scope.launch {
//        val result = snackbarHostState.showSnackbar(
//            message = "Snackbar Example",
//            actionLabel = "Action",
//            withDismissAction = true,
//            duration = SnackbarDuration.Indefinite
//        )
//        when (result) {
//            SnackbarResult.ActionPerformed -> {
//                //Do Something
//            }
//            SnackbarResult.Dismissed -> {
//                //Do Something
//            }
//
//            else -> {}
//        }
//    }
//    SnackbarHost(
//        hostState = snackbarHostState,
//        modifier = Modifier.align(Alignment.BottomCenter)
//    )

}

@Composable
fun ToastApp(message: UIStr? = null, length: Int = Toast.LENGTH_SHORT) {
    message?.let {
        val msg = it.toStr()
        if (msg.isNotEmpty()) {
            val context = LocalContext.current
            Toast.makeText(context, msg, length).show()
        }
    }
}


@Composable
fun NativeAlert(message: UIStr? = null, title: UIStr = UIStr.ResStr(R.string.app_name)) {
    message?.let {
        val alert = AlertDialog.Builder(LocalContext.current)
            .setTitle(stringResource(id = R.string.app_name)).setMessage(message.toStr())
            .setPositiveButton(
                stringResource(id = R.string.ok)
            ) { dialog, which ->
                dialog.dismiss()
            }.create()
        alert.show()
    }
}


@Composable
fun NetworkErr(show: Boolean = false, uiStr: UIStr = UIStr.ResStr(R.string.err_network)) {
    if (show) ToastApp(uiStr)
}


@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun CongratulationDialog(
    showDialog: Boolean = true,
    title: UIStr = UIStr.ResStr(R.string.congratulations),
    message: UIStr = UIStr.ResStr(R.string.msg_signup_success),
    buttonText: UIStr = UIStr.ResStr(R.string.ok),
    onDismiss: (Boolean) -> Unit = {}
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
                    title = title.toStr(), titleStyle = baseStyleLarge().copy(
                        color = FF333333, fontSize = 16.ssp
                    ), desc = message.toStr(),
                    descStyle = baseStyle2().copy(
                        fontWeight = FontWeight.Medium
                    ), horizontalAlignment = Alignment.CenterHorizontally
                )
                Spacer(modifier = Modifier.height(16.sdp))
                AppButton(
                    modifier = Modifier.defaultMinSize(120.sdp, DIALOG_BUTTON_HEIGHT.sdp),
                    title = buttonText.toStr()
                ) {
                    onDismiss(true)
                }
                Spacer(modifier = Modifier.height(22.sdp))
            }
        }
    }
}

@Composable
@Preview
fun ProgressDialogApp(show: Boolean = false) {
    if (show) Dialog(onDismissRequest = {}) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .size(80.sdp)
                .background(
                    color = Color.White, shape = cornerShape(16)
                )
        ) {
            CircularProgressIndicator()
        }
    }
}


