package com.cityof.glendale.screens.landing

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppOutlinedButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.navigation.Routes
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

private const val TAG = "LandingScreen"

@Composable
@Preview(
    apiLevel = 34,
    showSystemUi = true,
    showBackground = true
)
fun LandingPreview() {
    Landing(
        viewModel = LandingViewModel()
    )
}

@Composable
fun Landing(
    navController: NavController? = null, viewModel: LandingViewModel = hiltViewModel()
) {


    Log.d(TAG, "Landing: ")
//    LockOrientation(isPortrait = false)
    val navigation by viewModel.navigation.collectAsState(initial = NavAction.None)
    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavLogin -> navController?.navigate(Routes.Login.name)
            NavAction.NavSignUp -> navController?.navigate(Routes.SignUp.name)
            NavAction.None -> {}
        }
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .align(
                    Alignment.CenterHorizontally
                )
                .padding(
                    PaddingValues(
                        start = 24.sdp, end = 24.sdp, top = 40.sdp
                    )
                )
        ) {

            Image(painter = painterResource(id = R.drawable.globe_with_bee), contentDescription = null)
            Spacer(modifier = Modifier.height(40.sdp))
            Text(
                text = stringResource(id = R.string.msg_be_the_solution),
                style = baseStyleLarge().copy(
                    fontSize = 15.ssp, textAlign = TextAlign.Center, lineHeight = 22.ssp
                )
            )
            Spacer(modifier = Modifier.height(44.sdp))
            TitleWithDesc(
                title = R.string.msg_join,
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            Spacer(modifier = Modifier.height(12.sdp))
            AppButton(title = stringResource(id = R.string.login)){
                viewModel.dispatch(Intent.LoginClicked)
            }
            Spacer(modifier = Modifier.height(12.sdp))
            AppOutlinedButton(
                title = stringResource(id = R.string.create_account)
            ) {
                viewModel.dispatch(Intent.SignUpClicked)
            }
            Spacer(modifier = Modifier.height(80.sdp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.beeline_road),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}


