package com.cityof.glendale.screens.signup.personalDetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppDropDown2
import com.cityof.glendale.composables.components.AppText
import com.cityof.glendale.composables.components.CongratulationDialog
import com.cityof.glendale.composables.components.DropDownText
import com.cityof.glendale.composables.components.LabelledCheckbox
import com.cityof.glendale.composables.components.NativeDatePicker
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StreetAddressComposable
import com.cityof.glendale.composables.components.TEXT_MAX_LENGTH
import com.cityof.glendale.composables.components.TF_HEIGHT
import com.cityof.glendale.composables.components.TOP_HEIGHT
import com.cityof.glendale.composables.components.TextFieldColor
import com.cityof.glendale.composables.components.TextInputStyle
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.ZipCodeComposable
import com.cityof.glendale.composables.components.clickSpanStyle
import com.cityof.glendale.composables.components.normalSpanStyle
import com.cityof.glendale.composables.cornerShape
import com.cityof.glendale.data.enums.getGenders
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailContract.Intent
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailContract.NavAction
import com.cityof.glendale.screens.signup.titleStyle
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.DateFormats
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.ResourceProviderImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.capitalizeWords
import com.cityof.glendale.utils.formatMillis
import ir.kaaveh.sdpcompose.sdp
import timber.log.Timber
import java.util.Calendar
import java.util.Locale


private const val TAG = "PersonalDetailScreen"


@Preview(showSystemUi = true, showBackground = true)
@Preview(showSystemUi = true, showBackground = true, locale = "es-rES")
@Preview(showSystemUi = true, showBackground = true, locale = "hy")
@Composable
fun DetailPreview() {
    PersonalDetailScreen(
        viewModel = PersonalDetailViewModel(
            InputValidatorImpl(),
            AppRepository(MockApiService()),
            AppPreferencesManagerImpl(LocalContext.current.appDataStore),
            ResourceProviderImpl(LocalContext.current)
        )
    )
}


@Composable
fun PersonalDetailScreen(
    navController: NavHostController? = null, viewModel: PersonalDetailViewModel = hiltViewModel()
) {

    Timber.d("PersonalDetails")
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    if (state.gender == null) viewModel.dispatch(Intent.SetGender(getGenders()))

    LaunchedEffect(key1 = Unit, block = {

        viewModel.setDataBundle(
            navController?.previousBackStackEntry?.savedStateHandle?.get<Map<String, Any>>(
                AppConstants.DATA_BUNDLE
            )
        )
        viewModel.setDropDownTitles(
            context.getString(R.string.select_group),
            context.getString(R.string.personal_vehicle_type)
        )
        viewModel.schoolsAndVehicles()
    })

    ProgressDialogApp(state.isLoading)
    ToastApp(state.msgToast)
    CongratulationDialog(
        state.showCongratulation
    ) {
        viewModel.dispatch(Intent.ShowCongratulations())
    }

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavLogin -> {
                navController?.navigate(Routes.Login.name) {
                    popUpTo(Routes.Login.name) {
                        inclusive = true
                    }
                }
            }

            NavAction.NavTermCondition -> {
                navController?.navigate(Routes.DummyTC.name)
            }

            null -> {}

        }
    })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppBarWithBack {
            navController?.popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .align(
                    Alignment.CenterHorizontally
                )
                .padding(
                    PaddingValues(horizontal = 24.sdp)
                )

        ) {

            Spacer(modifier = Modifier.height(10.sdp))
            TitleWithDesc(
                title = R.string.personal_details, titleStyle = titleStyle()
            )
            Spacer(modifier = Modifier.height(10.sdp))
            AppText(icon = R.drawable.ic_dob,
                onClick = {
//                    dialogState.value = true
                    NativeDatePicker(context, Calendar.getInstance(Locale.getDefault()).let {
                        it.add(Calendar.YEAR, -13)
                        it
                    }) {
                        viewModel.dispatch(Intent.DateOfBirthChanged(it))
                    }
                },
                value = state.dateOfBirth?.let {
                    val temp = formatMillis(it, DateFormats.DATE_FORMAT)
                    temp.capitalizeWords()
                } ?: "",
                label = stringResource(id = R.string.date_of_birth),
                error = state.dateOfBirthErr.toStr())

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

            StreetAddressComposable(value = state.streetAddress,
                icon = R.drawable.ic_location_minus,
                label = stringResource(R.string.street_addr),
                error = state.streetAddressErr.toStr(),
                maxLength = TEXT_MAX_LENGTH,
                valueChanged = {
                    viewModel.dispatch(Intent.StreetAddressChanged(it))
                })
            StreetAddressComposable(
                value = state.city,
                icon = R.drawable.ic_buliding,
                label = stringResource(R.string.city),
                valueChanged = {
                    viewModel.dispatch(Intent.CityChanged(it))
                },
                error = state.cityErr.toStr()
            )
            Spacer(modifier = Modifier.height(TOP_HEIGHT.sdp))
            ZipCode(state = state.state,
                value = state.zipCode,
                error = state.zipCodeErr.toStr(),
                valueChanged = {
                    viewModel.dispatch(Intent.ZipCodeChanged(it))
                })

            AppDropDown2(list = state.schools,
                selectedValue = state.selectedSchool?.name ?: "",
                selectedIndex = state.schoolIndex,
                leadingIcon = R.drawable.ic_group,
                onItemSelected = { index, item ->
                    viewModel.dispatch(
                        Intent.GroupChanged(
                            index, item
                        )
                    )
                }) { DropDownText(text = it.name ?: "") }

            AppDropDown2(list = state.vehicles,
                selectedValue = state.selectedVehicle?.name ?: "",
                selectedIndex = state.vehicleIndex,
                error = state.vehiclesErr.toStr(),
                leadingIcon = R.drawable.ic_profile,
                onItemSelected = { index, item ->
                    viewModel.dispatch(
                        Intent.VehicleChanged(index, item)
                    )
                }) { DropDownText(text = it.name ?: "") }

            Spacer(modifier = Modifier.height(16.sdp))
            LabelledCheckbox(value = state.cb13YearOld,
                label = stringResource(R.string.msg_certify),
                onChecked = {
                    viewModel.dispatch(Intent.Cb13YearOldChanged(it))
                })
            Spacer(modifier = Modifier.height(6.sdp))

            TermConditionComposable(value = state.cbTermCondition, onChecked = {
                viewModel.dispatch(Intent.CbTermConditionChanged(it))
            }, onTermConditions = {
                Timber.d("onTermConditions clicked")
                viewModel.dispatch(Intent.TermConditionClicked)
            })

            Spacer(modifier = Modifier.height(22.sdp))
            AppButton(title = stringResource(R.string.submit)) {
                viewModel.dispatch(Intent.SubmitClicked)
            }
            Spacer(modifier = Modifier.height(12.sdp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_bars),
                    contentDescription = stringResource(R.string.bars)
                )
            }
            Spacer(modifier = Modifier.height(40.sdp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(locale = "es-rES")
fun TermConditionComposable(
    value: Boolean = false, onChecked: (Boolean) -> Unit = {}, onTermConditions: () -> Unit = {}
) {

    val text = stringResource(R.string.i_accept_terms)
    val underlined = stringResource(id = R.string.term_conditions)
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Row(
            modifier = Modifier.defaultMinSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = value, onCheckedChange = {
                    onChecked(it)
                }, enabled = true, colors = CheckboxDefaults.colors(Purple)
            )
            Spacer(modifier = Modifier.width(2.sdp))
            val annotatedText = buildAnnotatedString {

                withStyle(
                    style = normalSpanStyle().copy(
                        color = Color.Black
                    )
                ) {
                    append(text)
                    append(" ")
                }

                pushStringAnnotation(
                    tag = underlined,// provide tag which will then be provided when you click the text
                    annotation = underlined
                )


                withStyle(
                    style = clickSpanStyle().copy(
                        textDecoration = TextDecoration.Underline, color = Purple
                    )
                ) {
                    append(underlined)
//                    addStringAnnotation(
//                        tag = "clickable",
//                        annotation = "true", start = text.length, end =
//                    )
                }
                pop()
            }

            ClickableText(text = annotatedText) { offset ->
                annotatedText.getStringAnnotations(tag = underlined, offset, offset).firstOrNull()
                    ?.let {
                        Timber.d("onClick")
                        onTermConditions()
                    }
            }
        }
    }
}


@Composable
fun ZipCode(
    state: String = "",
    value: String = "",
    error: String = "",
    valueChanged: (String) -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            modifier = Modifier
                .width(120.sdp)
                .height(TF_HEIGHT.sdp),
            enabled = false,
            value = stringResource(id = R.string.california),
            onValueChange = {},
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_buliding),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            shape = cornerShape(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = TextFieldColor(),
            textStyle = TextInputStyle()


//            TextStyle(
//                color = Color.Black,
//                fontSize = 14.ssp,
//                fontWeight = FontWeight.W400,
//                fontFamily = RobotoFontFamily,
//                letterSpacing = TextUnit(0.2f, TextUnitType.Sp)
//
//            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        ZipCodeComposable(
            value = value, error = error, valueChanged = valueChanged
        )
    }
}

//@Composable
//@Preview
//fun CongratulationDialog(
//    showDialog: Boolean = true, onDismiss: (Boolean) -> Unit = {}
//) {
//
//    if (showDialog) {
//        DialogApp(onDismiss = { }) {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        horizontal = 16.sdp
//                    ),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Spacer(modifier = Modifier.height(22.sdp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_congratulations),
//                    contentDescription = ""
//                )
//                Spacer(modifier = Modifier.height(4.sdp))
//                TitleWithDesc(
//                    title = stringResource(id = R.string.congratulations),
//                    titleStyle = baseStyleLarge().copy(
//                        color = FF333333, fontSize = 16.ssp
//                    ),
//                    desc = stringResource(id = R.string.msg_signup_success),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                )
//                Spacer(modifier = Modifier.height(16.sdp))
//                AppButton(
//                    modifier = Modifier.defaultMinSize(120.sdp, DIALOG_BUTTON_HEIGHT.sdp),
//                    title = stringResource(id = R.string.ok)
//                ) {
//                    onDismiss(true)
//                }
//                Spacer(modifier = Modifier.height(22.sdp))
//            }
//        }
//    }
//}