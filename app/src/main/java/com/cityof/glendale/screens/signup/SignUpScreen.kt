package com.cityof.glendale.screens.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AnnotatedClickableText
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppPasswordField
import com.cityof.glendale.composables.components.AppTextField
import com.cityof.glendale.composables.components.EMAIL_LENGTH
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.screens.signup.SignUpContract.Intent
import com.cityof.glendale.screens.signup.SignUpContract.NavAction
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.asMap
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Composable
fun titleStyle() = baseStyleLarge().copy(fontSize = 21.ssp, lineHeight = 28.ssp)

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SignUpPreview() {
    SignUpScreen(
        viewModel = SignUpViewModel(
            InputValidatorImpl(), AppPreferencesManagerImpl(
                LocalContext.current.appDataStore
            )
        )
    )
}


@Composable
fun SignUpScreen(
    navController: NavHostController? = null, viewModel: SignUpViewModel = hiltViewModel()
) {


    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavPersonalDetails -> {
                navController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, state.asMap()
                )
                navController?.navigate(Routes.PersonalDetails.name)
            }
            
            else -> {}
        }
    })

//    LaunchedEffect(Unit) { viewModel.isBiometricForLogin() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_WINDOW)
    ) {
        AppBarWithBack {
            navController?.popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(
                    Alignment.CenterHorizontally
                )
                .background(
                    Color.White
                )
                .padding(
                    PaddingValues(horizontal = 24.sdp)
                )
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.sdp))
            TitleWithDesc(
                title = R.string.create_account,
                titleStyle = titleStyle().copy(),
                desc = R.string.msg_sign_up_reduce
            )
            Spacer(modifier = Modifier.height(8.sdp))
            AppTextField(
                value = state.firstName,
                label = stringResource(R.string.first_name),
                icon = R.drawable.ic_profile,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words
                ),
                valueChanged = {
                    viewModel.dispatch(Intent.FirstNameEdited(it))
                },
                error = state.firstNameErr.toStr()
            )
            AppTextField(
                value = state.lastName,
                label = stringResource(R.string.last_name),
                icon = R.drawable.ic_profile,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words
                ),
                valueChanged = {
                    viewModel.dispatch(Intent.LastNameEdited(it))
                },
                error = state.lastNameErr.toStr()
            )
            AppTextField(
                value = state.email,
                label = stringResource(R.string.msg_email),
                maxLength = EMAIL_LENGTH,
                valueChanged = {
                    viewModel.dispatch(Intent.EmailEdited(it))
                },
                error = state.emailErr.toStr()
            )
            AppPasswordField(
                value = state.password,
                label = stringResource(R.string.msg_password),
                valueChanged = {
                    viewModel.dispatch(Intent.PasswordEdited(it))
                },
                error = state.passwordErr.toStr()
            )
            AppPasswordField(
                value = state.confirmPassword,
                label = stringResource(R.string.confirm_pwd),
                valueChanged = {
                    viewModel.dispatch(Intent.ConfirmPasswordEdited(it))
                },
                error = state.confirmPasswordErr.toStr()
            )
//            PhoneTextField(value = state.mobile, error = state.mobileErr.toStr(), onCrossClick = {
//                viewModel.dispatch(Intent.MobileEdited("", ""))
//            }) { mobile, maskedMobile ->
//                viewModel.dispatch(Intent.MobileEdited(mobile, maskedMobile))
//            }
            Spacer(modifier = Modifier.height(8.sdp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.msg_do_you_want), style = baseStyle().copy(
                        fontSize = 12.ssp
                    ), modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.sdp))
                Switch(checked = state.isBiometric,
                    modifier = Modifier.height(22.sdp),
                    onCheckedChange = {
                        viewModel.dispatch(Intent.BiometricClicked(it))
                    })
            }
            Spacer(modifier = Modifier.height(52.sdp))
            AppButton(title = stringResource(R.string.msg_continue)) {
                viewModel.dispatch(Intent.SignupClicked)
            }
            Spacer(modifier = Modifier.height(16.sdp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                AnnotatedClickableText(
                    text = stringResource(R.string.next_step),
                    clickableText = stringResource(R.string.personal_details)
                ) {
                    viewModel.dispatch(Intent.PersonalDetailClicked)
                }
            }
            Spacer(modifier = Modifier.height(40.sdp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bars_one),
                    contentDescription = "bars"
                )
            }
            Spacer(modifier = Modifier.height(20.sdp))
        }
    }
}




