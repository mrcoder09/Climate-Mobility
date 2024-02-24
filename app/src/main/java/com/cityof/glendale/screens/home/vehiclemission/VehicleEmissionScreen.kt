package com.cityof.glendale.screens.home.vehiclemission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.data.fixes.VehicleEmission
import com.cityof.glendale.data.fixes.getTitle
import com.cityof.glendale.data.fixes.isPickUpSubCategory
import com.cityof.glendale.data.fixes.isPickup
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionContract.VehicleEmissionDimen
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.google.gson.Gson
import ir.kaaveh.sdpcompose.sdp
import timber.log.Timber


@Composable
@Preview
fun VehicleEmissionPreview() {
    VehicleEmissionScreen(
        viewModel = VehicleEmissionViewModel(Gson())
    )
}

@Composable
fun VehicleEmissionScreen(
    navHostController: NavHostController? = null,
    viewModel: VehicleEmissionViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit, block = {

    })

    Column {

        AppBarWithBack(
            title = stringResource(id = R.string.about_emissions)
        ) {
            navHostController?.popBackStack()
        }

//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = VehicleEmissionDimen.HORIZONTAL_PADDING.sdp,
//                end = 48.sdp
//            )
//        )


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FFF9F9F9)
        ) {

            item {

                Spacer(modifier = Modifier.height(12.sdp))
                Text(
                    text = stringResource(R.string.msg_enviromental_protection),
                    style = baseStyle2().copy(
                        fontWeight = FontWeight.W500, textAlign = TextAlign.Justify
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = VehicleEmissionDimen.HORIZONTAL_PADDING.sdp)
                )
                Spacer(modifier = Modifier.height(12.sdp))
            }

            items(state.list.size) {
                VehicleEmissionComposable(emission = state.list[it])
            }
        }
    }
}


@Composable
fun VehicleEmissionComposable(emission: VehicleEmission) {

    Timber.d(emission.toString())

    if (emission.isPickup()) {

        Text(
            text = emission.category ?: "", style = baseStyle2().copy(
                fontWeight = FontWeight.W600
            ), modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 28.sdp, top = 18.sdp, bottom = 16.sdp
                )
        )

    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = VehicleEmissionDimen.HORIZONTAL_PADDING.sdp,
                    end = VehicleEmissionDimen.HORIZONTAL_PADDING.sdp,
                    bottom = VehicleEmissionDimen.SPACE_BETWEEN.sdp
                ),
            elevation = CardDefaults.cardElevation(VehicleEmissionDimen.CARD_ELEVATION.sdp),
            colors = CardDefaults.cardColors(Color.White),
            shape = RoundedCornerShape(8.sdp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 12.sdp, start = 12.sdp, end = 12.sdp, bottom = 12.sdp
                    ), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = emission.getTitle() ?: "",
                    style = if (emission.isPickUpSubCategory()) baseStyle2().copy(
//                        color = FF333333
                    ) else baseStyle2().copy(
                        fontWeight = FontWeight.W600
                    )
                )
                Text(
                    text = emission.emission ?: "", style = baseStyle2().copy(
                        color = Purple, fontWeight = FontWeight.W600
                    )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.sdp, end = 12.sdp, bottom = 12.sdp
                    )
            ) {
                Text(
                    text = emission.description ?: "", style = baseStyle2().copy(
//                        color = FF333333,
                        fontWeight = FontWeight.W400
                    )
                )
                Spacer(modifier = Modifier.height(4.sdp))
                emission.vehicleList?.forEach {
                    Text(
                        text = "\u2022 $it", style = baseStyle2().copy(
//                            color = FF333333,
                            fontWeight = FontWeight.W400
                        )
                    )
                }
            }
        }
    }
}