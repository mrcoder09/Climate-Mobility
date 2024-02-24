package com.cityof.glendale.screens.trips.fareinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.CardComposable
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.UnderLinedWithClick
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.Endpoints
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.FareInfo
import com.cityof.glendale.network.responses.getRegularPrice
import com.cityof.glendale.network.responses.getSeniorCitizenPrice
import com.cityof.glendale.network.responses.getStudentPrice
import com.cityof.glendale.theme.FFDADADA
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.utils.xtIntentBrowse
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Composable
@Preview
fun FareInfoPreview() {
    FareInfoScreen(
        viewModel = FareInfoViewModel(
            AppRepository(MockApiService())
        )
    )
}

@Composable
fun FareInfoScreen(
    navHostController: NavHostController? = null, viewModel: FareInfoViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    val title = stringResource(id = R.string.fare)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            FareInfoContract.NavAction.NavTapStore -> {
                context.xtIntentBrowse(
                    url = Endpoints.TAP_STORE
                )

            }

            null, FareInfoContract.NavAction.NavNone -> return@LaunchedEffect
        }
        viewModel.dispatch(FareInfoContract.Intent.ResetNav)
    })


    ToastApp(state.toastMsg)
    ProgressDialogApp(state.isLoading)
    DoUnauthorization(state.isAuthErr, navHostController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
    ) {

        AppBarWithBack(
            title = title
        ) {
            navHostController?.popBackStack()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 22.sdp
                )
        ) {
            item {
                Spacer(modifier = Modifier.height(18.sdp))
                Text(
                    text = stringResource(R.string.beeline_fare), style = baseStyle2().copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(14.sdp))
            }

            items(state.routes.size) {
                val item = state.routes[it]
                FareInfoCard(item) {

                }
                Spacer(modifier = Modifier.height(8.sdp))
            }

            if (state.isLoading.not()) item {
                Spacer(modifier = Modifier.height(10.sdp))
                TapStoreComposable {
                    viewModel.dispatch(FareInfoContract.Intent.TapStoreClicked)
                }
                Spacer(modifier = Modifier.height(24.sdp))
            }

        }
    }

}

@Composable
fun TapStoreComposable(click: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(click),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            modifier = Modifier.weight(1f)
        ) {
            UnderLinedWithClick(
                text = stringResource(R.string.to_purchase_tap_stored_value), underlined = stringResource(
                    R.string.tap_here
                ), onClick = click
            )
        }
        Spacer(modifier = Modifier.width(1.sdp))
        Image(
            painter = painterResource(id = R.drawable.tap_logo_trans),
            contentDescription = null,
            modifier = Modifier
                .background(
                    color = Color(0xFF0096D6), shape = RoundedCornerShape(4.sdp)
                )
                .padding(
                    all = 8.sdp
                )
                .size(32.sdp)
        )
    }
}


@Composable
@Preview
fun FareInfoCard(item: FareInfo = FareInfo(), onClick: (FareInfo) -> Unit = {}) {

    CardComposable(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick(item) }, cornerRadius = 8
    ) {
        Spacer(modifier = Modifier.height(12.sdp))
        BasicText(
            text = item.beelineFareMedia ?: "", style = baseStyle().copy(
                fontSize = 14.ssp
            ), modifier = Modifier.padding(
                horizontal = 18.sdp
            )
        )
        Spacer(modifier = Modifier.height(6.sdp))
        BasicText(
            text = item.description ?: "",
            style = baseStyle().copy(fontWeight = FontWeight.Normal),
            modifier = Modifier.padding(
                horizontal = 18.sdp
            )
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.sdp, horizontal = 16.sdp
                ), color = FFDADADA
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp),
        ) {
            FarePriceComposable(
                price = UIStr.Str(item.getRegularPrice())
            )
            Spacer(modifier = Modifier.width(18.sdp))
            FarePriceComposable(
                title = UIStr.ResStr(R.string.student), price = UIStr.Str(item.getStudentPrice())
            )
//            FarePriceComposable(
//                title = UIStr.ResStr(R.string.senior_citizen),
//                price = UIStr.Str(item.getSeniorCitizenPrice())
//            )
        }


        FarePriceComposable(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.sdp),
            title = UIStr.ResStr(R.string.senior_citizen),
            price = UIStr.Str(item.getSeniorCitizenPrice())
        )
    }
}


@Composable
fun FarePriceComposable(
    modifier: Modifier = Modifier,
    title: UIStr = UIStr.ResStr(R.string.regular),
    price: UIStr = UIStr.ResStr(R.string.regular)
) {
    Column(
        modifier = modifier
    ) {
        BasicText(
            text = title.toStr(), style = baseStyle().copy(
                fontWeight = FontWeight.Normal
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicText(
            text = price.toStr(), style = baseStyle().copy(
                color = Color(0xFF442580), fontWeight = FontWeight.W600
            )
        )
        Spacer(modifier = Modifier.height(12.sdp))
    }
}