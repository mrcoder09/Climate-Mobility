package com.cityof.glendale.screens.more.profileSettings.editprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppDropDown2
import com.cityof.glendale.composables.components.AppOutlinedButton
import com.cityof.glendale.composables.components.AppTextField
import com.cityof.glendale.composables.components.DropDownText
import com.cityof.glendale.composables.components.FloatLabelEditProfile
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StreetAddressComposable
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.ZipCodeComposable
import com.cityof.glendale.navigation.navLogin
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.Intent
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileContract.NavAction
import com.cityof.glendale.screens.more.titleStyle
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.InputValidatorImpl
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import timber.log.Timber

@Composable
@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, locale = "es-rES")
@Preview(showBackground = true, showSystemUi = true, locale = "hy")
fun OtherInfoPreview() {
    OtherInfoScreen(
        viewModel = EditProfileViewModel(
            InputValidatorImpl(), AppRepository(MockApiService()), AppPreferencesManagerImpl(
                LocalContext.current.appDataStore
            )
        )
    )
}


@Composable
fun OtherInfoScreen(
    navController: NavHostController? = null, viewModel: EditProfileViewModel = viewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.setDropDownTitles(
            context.getString(R.string.select_group),
            context.getString(R.string.personal_vehicle_type)
        )
        viewModel.schoolsAndVehicles()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavProfileSetting -> {
                Timber.d("${state.isProfileUpdated}")
                navController?.popBackStack()
                navController?.popBackStack()
            }

            NavAction.NavLogin -> navController?.navLogin()
            else -> {}
        }
    })

    ToastApp(
        state.msgToast
    )

    ProgressDialogApp(state.isLoading)

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
                title = R.string.other_information, titleStyle = titleStyle()
            )
            Spacer(modifier = Modifier.height(14.sdp))
            StreetAddressComposable(label = stringResource(id = R.string.street_addr),
                value = state.streetAddress,
                error = state.streetAddressErr.toStr(),
                hintStyle = FloatLabelEditProfile(),
                valueChanged = {
                    viewModel.dispatch(Intent.StreetAddressChanged(it))
                })
            StreetAddressComposable(label = stringResource(id = R.string.city),
                icon = R.drawable.ic_buliding,
                value = state.city,
                error = state.cityErr.toStr(),
                hintStyle = FloatLabelEditProfile(),
                valueChanged = {
                    viewModel.dispatch(Intent.CityChanged(it))
                })
            AppTextField(label = stringResource(id = R.string.state),
                icon = R.drawable.ic_buliding,
                value = stringResource(id = R.string.california),
                hintStyle = FloatLabelEditProfile(),
                valueChanged = {
                    viewModel.dispatch(Intent.StateChanged(it))
                })
            Spacer(modifier = Modifier.height(6.sdp))
            ZipCodeComposable(value = state.zipCode,
                error = state.zipCodeErr.toStr(),
                hintStyle = FloatLabelEditProfile(),
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

            Spacer(modifier = Modifier.height(48.sdp))

            TwoButtons(title2 = R.string.cancel, firstClicked = {
                viewModel.dispatch(
                    Intent.SaveClicked
                )
            }, secondClicked = {
                viewModel.dispatch(
                    Intent.CancelClicked
                )
            })
            Spacer(modifier = Modifier.height(20.sdp))
        }
    }
}

@Preview
@Composable
fun TwoButtons(
    title: Int = R.string.save,
    title2: Int = R.string.next,
    firstClicked: () -> Unit = {},
    secondClicked: () -> Unit = {},
    horizontalPadding: Int = 16
) {
    Row(
        horizontalArrangement = Arrangement.Start, modifier = Modifier.padding(
            horizontal = horizontalPadding.sdp
        )
    ) {
        AppButton(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 38.sdp),
            title = stringResource(id = title),
            onClick = firstClicked
        )
        Spacer(modifier = Modifier.width(8.sdp))
        AppOutlinedButton(
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = 38.sdp),
            title = stringResource(id = title2),
            onClick = secondClicked
        )
    }
}

