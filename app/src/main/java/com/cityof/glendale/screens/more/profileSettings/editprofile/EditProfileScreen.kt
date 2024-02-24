package com.cityof.glendale.screens.more.profileSettings.editprofile

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppDropDown2
import com.cityof.glendale.composables.components.AppText
import com.cityof.glendale.composables.components.AppTextField
import com.cityof.glendale.composables.components.DropDownText
import com.cityof.glendale.composables.components.FloatLabelEditProfile
import com.cityof.glendale.composables.components.NativeDatePicker
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.data.enums.getGenders
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.LoginData
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.Intent
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.NavAction
import com.cityof.glendale.screens.more.titleStyle
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.formatMillis
import com.cityof.glendale.utils.xtJson
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber
import java.util.Calendar
import java.util.Locale


@Composable
@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
fun EditProfilePreview() {
    EditProfileScreen(
        viewModel = EditProfileViewModel(
            InputValidatorImpl(), AppRepository(
                MockApiService()
            ), AppPreferencesManagerImpl(
                LocalContext.current.appDataStore
            )
        )
    )
}


@Composable
fun EditProfileScreen(
    navController: NavHostController? = null, viewModel: EditProfileViewModel = hiltViewModel()
) {

    Timber.d("EditProfile")
//    val viewModel = viewModel<EditProfileViewModel>()
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
//    val datePickerState = remember { mutableStateOf(false) }

    if (state.gender == null) viewModel.dispatch(Intent.SetGender(getGenders()))

    LaunchedEffect(key1 = Unit, block = {
        val loginData = navController?.previousBackStackEntry?.savedStateHandle?.get<LoginData>(
            AppConstants.DATA_BUNDLE
        )

        if (state.loginData == null) viewModel.setLoginData(loginData)
        Timber.d(
            loginData?.xtJson()
        )
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.OtherInfo -> {
                navController?.navigate(
                    Routes.OtherInfo.name
                )
            }

            else -> {}
        }
    })


//    if (datePickerState.value) {
//
//    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppBarWithBack(
            color = Color.White, title = stringResource(R.string.edit_profile)
        ) {
            navController?.popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .align(
                    Alignment.CenterHorizontally
                )
                .padding(
                    PaddingValues(horizontal = 24.sdp)
                )
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.sdp))
            TitleWithDesc(
                title = R.string.personal_information, titleStyle = titleStyle()
            )
            Spacer(modifier = Modifier.height(14.sdp))
            AppTextField(
                value = state.firstName,
                label = stringResource(R.string.first_name),
                icon = R.drawable.ic_profile,
                hintStyle = FloatLabelEditProfile(),
                valueChanged = {
                    viewModel.dispatch(Intent.FirstNameChanged(it))
                },
                error = state.firstNameErr.toStr()
            )
            AppTextField(
                value = state.lastName,
                label = stringResource(R.string.last_name),
                icon = R.drawable.ic_profile,
                hintStyle = FloatLabelEditProfile(),
                valueChanged = {
                    viewModel.dispatch(Intent.LastNameChanged(it))
                },
                error = state.lastNameErr.toStr()
            )
            AppText(
                icon = R.drawable.ic_dob,
                onClick = {
                    NativeDatePicker(context, Calendar.getInstance(Locale.getDefault()).let {
                        it.add(Calendar.YEAR, -13)
                        it
                    }) {
                        viewModel.dispatch(Intent.DateOfBirthChanged(it))
                    }
                },
                hintStyle = FloatLabelEditProfile(),
                value = state.selectedDob?.let { formatMillis(it, DateFormats.DATE_FORMAT) } ?: "",
                label = stringResource(id = R.string.date_of_birth),
                error = state.dobErr.toStr(),
            )
            AppDropDown2(list = state.genders,
                selectedValue = state.gender?.name ?: "",
                selectedIndex = state.genderIndex,
                error = state.genderErr.toStr(),
                leadingIcon = R.drawable.ic_profile,
                onItemSelected = { index, item ->
                    viewModel.dispatch(Intent.GenderChanged(index, item))
                }) {
                DropDownText(text = it.name)
            }
//            PhoneTextField(value = state.mobile, error = state.mobileErr.toStr(), onCrossClick = {
//                viewModel.dispatch(Intent.MobileChanged("", ""))
//            }) { mobile, maskedMobile ->
//                viewModel.dispatch(Intent.MobileChanged(mobile, maskedMobile))
//            }
            Spacer(modifier = Modifier.height(8.sdp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.msg_do_you_want), style = baseStyle().copy(
                        fontWeight = FontWeight.Normal, fontSize = 15.ssp
                    ), modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.sdp))
                Switch(checked = state.isBiometric,
                    modifier = Modifier.height(22.sdp),
                    onCheckedChange = {
                        viewModel.dispatch(Intent.BiometricClicked(it))
                    })
            }
            Spacer(modifier = Modifier.height(32.sdp))
            AppButton(
                title = stringResource(id = R.string.next),
                modifier = Modifier
                    .width(120.sdp)
                    .height(38.sdp)
            ) {
                viewModel.dispatch(Intent.NextClicked)
            }
//            AppOutlinedButton(title = stringResource(id = R.string.next), onClick = {
//                viewModel.dispatch(Intent.NextClicked)
//            })
//            TwoButtons(
//                firstClicked = { }, secondClicked = {
//                    navController?.navigate(Routes.OtherInfo.name)
//                })
            Spacer(modifier = Modifier.height(20.sdp))
        }
    }
}

