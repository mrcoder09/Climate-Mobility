package com.cityof.glendale.screens.more.notifications

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.screens.more.notifications.NotificationContract.Intent
import com.cityof.glendale.screens.more.notifications.NotificationContract.NotificationItem
import com.cityof.glendale.screens.more.titleStyle
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun NotificationSettings(
    navController: NavHostController? = null, viewModel: NotificationViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    //    val navigation by viewModel.navigation.collectAsState(initial = null)


    ProgressDialogApp(state.isLoading)
    ToastApp(state.toastMsg)
    DoUnauthorization(isAuthErr = state.isAuthErr, navController = navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBarWithBack(
            color = Color.White, title = stringResource(R.string.notifications)
        ) {
            navController?.popBackStack()
        }

//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = 24.sdp, end = 44.sdp
//            )
//        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.sdp)
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.sdp))
            TitleWithDesc(
                title = R.string.notification_setting, titleStyle = titleStyle()
            )
            Spacer(modifier = Modifier.height(22.sdp))
            NotificationSettingItem(item = state.serviceDelay, onCheckedChange = {
                viewModel.dispatch(
                    Intent.ServiceDelayEdited(it)
                )
            })
            HorizontalDivider()
            NotificationSettingItem(item = state.detour, onCheckedChange = {
                viewModel.dispatch(
                    Intent.DetoursEdited(it)
                )
            })

        }
    }
}

@Composable
fun NotificationSettingItem(item: NotificationItem, onCheckedChange: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.sdp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = item.title),
            style = baseStyle().copy(
                color = Color.Black,
                fontSize = 14.ssp,
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.width(16.sdp))
        Switch(
            checked = item.isOn,
            modifier = Modifier.height(22.sdp),
            onCheckedChange = onCheckedChange
        )
    }
}