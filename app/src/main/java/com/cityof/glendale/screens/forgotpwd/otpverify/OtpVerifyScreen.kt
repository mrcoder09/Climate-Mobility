package com.cityof.glendale.screens.forgotpwd.otpverify

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.ClickableTextWithInlinedContent
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.ErrorText
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.UnderlinedClickableText
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.components.clickSpanStyle
import com.cityof.glendale.composables.components.normalSpanStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.forgotpwd.ForgotPwdStyles
import com.cityof.glendale.screens.forgotpwd.SPACE_FROM_TOOLBAR
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyContract.Intent
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyContract.NavActions
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.InputValidatorImpl
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit


private const val TAG = "OtpVerifyScreen"
private const val TIME_OUT = 3 * 60 * 1000L   //1 * 30 * 1000L


@Preview(showSystemUi = true, showBackground = true)
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun OtpVerifyPreview() {

    OtpVerifyScreen(
        viewModel = OtpVerifyViewModel(
            InputValidatorImpl(), AppRepository(
                MockApiService()
            )
        )
    )
}


@Composable
fun OtpVerifyScreen(
    navController: NavHostController? = null,
    email: String = "satnam.singh@mobileprogramming.com",
    viewModel: OtpVerifyViewModel = hiltViewModel()
) {


    val initTick = remember { mutableStateOf(TIME_OUT) }
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.dispatch(Intent.SetEmail(email))
//        viewModel.dispatch(Intent.ShowDialog(state.isCongratulations))
    })


    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is NavActions.NavCreateNewPassword -> {
                navController?.navigate("CREATE_PWD/$email") {
                    popUpTo(
                        navController.currentBackStackEntry?.destination?.route ?: return@navigate
                    ) {
                        inclusive = true
                    }
                }
            }

            else -> {}
        }
    })

    ProgressDialogApp(show = state.isLoading)
    ToastApp(message = state.msgToast)
    DialogCheckEmail(
        showDialog = state.isCongratulations
    ) {
        viewModel.dispatch(Intent.ShowDialog(false))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBarWithBack(
            title = stringResource(id = R.string.otp_verification)
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
            TimerTicks(
                initTick = initTick
            ) { millis ->

                val hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
                )

                CircledTimer(hms = hms)

                Log.d(TAG, "OtpVerifyScreen: $millis")
                viewModel.dispatch(Intent.CanResend((millis == 0L)))
            }
            Spacer(modifier = Modifier.height(40.sdp))
            TitleWithDescComposable(
                title = stringResource(id = R.string.enter_otp), email = state.email
            ) {
                navController?.popBackStack()
            }
            Spacer(modifier = Modifier.height(32.sdp))
            OtpTextField(otpText = state.otp, onOtpTextChange = { text, isAllFilled ->
                viewModel.dispatch(Intent.OtpEdited(text))
            })
            if (state.otpError.toStr().isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.sdp))
                ErrorText(modifier = Modifier, err = state.otpError.toStr())
            }
            Spacer(modifier = Modifier.height(32.sdp))
            AppButton(title = stringResource(R.string.verify_otp)) {
                viewModel.dispatch(Intent.Submit)
            }
            Spacer(modifier = Modifier.height(14.sdp))
            UnderlinedClickableText(
                text = stringResource(id = R.string.resend), spanStyle = SpanStyle(
                    color = if (state.canResend) Purple else Color.Black,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.ssp
                )
            ) {
                if (state.canResend) {
//                    doIfNetwork {
                    initTick.value = TIME_OUT
                    viewModel.dispatch(Intent.Resend)
//                    }
                }
            }
            Spacer(modifier = Modifier.height(12.sdp))
        }
    }
}

@Composable
fun CircledTimer(hms: String) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_timer), contentDescription = ""
        )
        Text(
            text = "$hms", style = baseStyleLarge().copy(
                fontSize = 38.ssp
            )
        )
    }
}

@Composable
@Preview
fun TitleWithDescComposable(
    title: String = "", email: String = "", onClick: () -> Unit = {}
) {

    val emailWithIcon = buildAnnotatedString {
        append(email)
        append(" ")
        appendInlineContent("icon")
    }

    val annotatedString = buildAnnotatedString {
        withStyle(normalSpanStyle()) {
            append(stringResource(id = R.string.msg_digit_code_sent))
            append(" ")
        }
        withStyle(clickSpanStyle()) {
            pushStringAnnotation(
                tag = emailWithIcon.toString(), annotation = emailWithIcon.toString()
            )
            append(emailWithIcon)
            pop()
        }
    }

    val map = mapOf("icon" to InlineTextContent(
        Placeholder(
            width = 14.sp,
            height = 14.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
        )
    ) {
        Icon(painterResource(id = R.drawable.ic_edit), // Your icon goes here
            contentDescription = null, tint = Purple, modifier = Modifier.noRippleClickable {
                onClick()
            })
    })

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title, style = ForgotPwdStyles.titleStyle(), textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.sdp))
        ClickableTextWithInlinedContent(text = annotatedString,
            inlinedContent = map,
            style = TextStyle(
                textAlign = TextAlign.Center, fontSize = 16.ssp
            ),
            onClick = { onClick() })
    }
}

@Composable
fun TimerTicks(
    initTick: MutableState<Long>,
    interval: Long = 1_000L,
    content: @Composable (tickTime: Long) -> Unit
) {
    val ticks by rememberSaveable(initTick) { mutableStateOf(initTick) }
    content.invoke(ticks.value)
    LaunchedEffect(ticks.value) {
        if (ticks.value > 0) {
            val diff = ticks.value - interval
            delay(interval)
            ticks.value = diff
        }
    }
}


@Composable
@Preview
fun DialogCheckEmail(
    showDialog: Boolean = false,
    onDismiss: (Boolean) -> Unit = {},
) {


    if (showDialog) {

        DialogApp(onDismiss = { onDismiss(false) }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.sdp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(22.sdp))
                Image(
                    painter = painterResource(id = R.drawable.ic_circled_mail_purple),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(4.sdp))
                TitleWithDesc(
                    title = R.string.check_your_email,
                    titleStyle = baseStyleLarge().copy(fontSize = 18.ssp, color = Color.Black),
                    desc = R.string.msg_we_have_sent,
                    descStyle = baseStyle().copy(fontSize = 14.ssp, lineHeight = 20.ssp),
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


