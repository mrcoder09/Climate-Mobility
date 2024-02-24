package com.cityof.glendale.screens.more.profileSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.ButtonWithLeadingIcon
import com.cityof.glendale.composables.components.ProfileTextField
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.navigation.navLogin
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.getGender
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingContract.Intent
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingContract.NavAction
import com.cityof.glendale.screens.more.profileSettings.changepassword.ChangePasswordDialog
import com.cityof.glendale.screens.more.titleStyle
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.PROFILE_RED_BUTTON
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

const val LOAD_PROFILE = "LOAD_PROFILE"



@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun ProfileSettingPreview() {
    ProfileSettingScreen(
        viewModel = ProfileViewModel(
            AppRepository(MockApiService()),
            AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}

@Composable
fun ProfileSettingScreen(
    navController: NavHostController? = null, viewModel: ProfileViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)



    LaunchedEffect(Unit) {
        if (AppConstants.isLoadProfile) {
            viewModel.getUserProfile()
            AppConstants.isLoadProfile = false
        }
    }

    DeleteAccountDialog(state.showDeleteAccount, onDelete = {
        viewModel.dispatch(Intent.DeleteAccount)
    }) {
        viewModel.dispatch(Intent.ShowDeleteAccount(false))
    }


    ChangePasswordDialog(showDialog = state.showChangePassword, navController = navController) {
        viewModel.dispatch(Intent.ShowChangePassword(false))
    }

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavEditProfile -> {
                navController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, state.loginData
                )
                navController?.navigate(Routes.EditProfile.name)
            }

            NavAction.NavLanding -> {
                navController?.navigate(Routes.Landing.name) {
                    popUpTo(Routes.Dashboard.name) {
                        inclusive = true
                    }
                }
            }

            NavAction.NavLogin -> {
                navController?.navLogin()
            }

            null -> {}

        }
    })

    ProgressDialogApp(state.isLoading)
    ToastApp(state.msgToast)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToolbarWithIcons(onBack = {
            navController?.popBackStack()
        }, onEditClick = {
            viewModel.dispatch(Intent.EditProfileClicked)
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.sdp)
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.sdp))
            TitleWithDesc(
                title = R.string.profile_settings, titleStyle = titleStyle()
            )
            Spacer(modifier = Modifier.height(22.sdp))
            ProfileTextField(
                value = state.loginData?.firstName ?: "",
                label = stringResource(id = R.string.first_name)
            )
            ProfileTextField(
                value = state.loginData?.lastName ?: "",
                label = stringResource(id = R.string.last_name)
            )
            ProfileTextField(
                value = state.loginData?.email ?: "", label = stringResource(id = R.string.email)
            )
//            ProfileTextField(
//                value = formatMobile(state.mobile),
//                label = stringResource(id = R.string.phone_number)
//            )
            ProfileTextField(
                value = state.loginData?.getGender()?.name ?: "",
                label = stringResource(id = R.string.sex)
            )
            ProfileTextField(
                value = state.loginData?.streetAddress ?: "",
                label = stringResource(id = R.string.street_addr)
            )
            ProfileTextField(
                value = state.loginData?.school?.name ?: "",
                label = stringResource(id = R.string.group_joined)
            )
            ProfileTextField(
                value = state.loginData?.vehicle?.name ?: "",
                label = stringResource(id = R.string.personal_vehicle_type)
            )
            Spacer(modifier = Modifier.height(8.sdp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.biometric_is_activated),
                    style = baseStyle().copy(
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.sdp))
                Switch(checked = state.loginData?.isBiometric ?: false,
                    modifier = Modifier.height(22.sdp),
                    onCheckedChange = {
//                        viewModel.dispatch(Intent.BioMetricClicked(it))
                    })
            }
            Spacer(modifier = Modifier.height(40.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 4.sdp
                    ), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppButton(
                    modifier = Modifier
                        .height(39.sdp)
                        .weight(1f),
                    title = stringResource(id = R.string.change_password),
                ) {
                    viewModel.dispatch(Intent.ShowChangePassword(true))
                }
                Spacer(modifier = Modifier.width(4.sdp))
                ButtonWithLeadingIcon(
                    modifier = Modifier
                        .height(39.sdp)
                        .weight(1f),
//                    style = StyleButton().copy(fontSize = 11.ssp),
                    title = stringResource(R.string.delete_account),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = PROFILE_RED_BUTTON
                    )
                ) {
                    viewModel.dispatch(Intent.ShowDeleteAccount(true))
                }
            }
            Spacer(modifier = Modifier.height(24.sdp))
        }
    }
}

@Composable
fun ToolbarWithIcons(onBack: () -> Unit, onEditClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.sdp)
            .background(color = Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = FF777C80
            )
        }

        Text(
            text = stringResource(id = R.string.profile),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = baseStyle().copy(
                fontSize = 16.ssp, color = FF777C80, fontWeight = FontWeight.Normal
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        AppButton(
            title = stringResource(R.string.edit),
            modifier = Modifier
                .height(28.sdp)
                .width(52.sdp),
            style = StyleButton().copy(
                fontSize = 12.ssp,
                fontWeight = FontWeight.W500,
                lineHeight = TextUnit(27f, TextUnitType.Sp)
            ),
            shape = RoundedCornerShape(5.sdp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Purple, containerColor = Color(0xFFEFE8FB)
            )
        ) {
            onEditClick()
        }
        Spacer(modifier = Modifier.width(8.sdp))

    }
}