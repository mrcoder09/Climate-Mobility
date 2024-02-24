package com.cityof.glendale.screens.rewards.merchantdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.network.responses.canRedeem
import com.cityof.glendale.network.responses.isBeeline
import com.cityof.glendale.screens.rewards.MerchantDetailHeader
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFE52E29
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Preview
@Composable
fun MerchantItemsPreview() {
    MerchantDetailScreen(
        viewModel = MerchantViewModel(
            AppPreferencesManagerImpl(LocalContext.current.appDataStore),
            AppRepository(MockApiService())
        )
    )
}

@Composable
fun MerchantDetailScreen(
    navHostController: NavHostController? = null, viewModel: MerchantViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        val merchant = navHostController?.previousBackStackEntry?.savedStateHandle?.get<Merchant>(
            AppConstants.MERCHANT_DETAILS
        )

        val hivePoints =
            navHostController?.previousBackStackEntry?.savedStateHandle?.get<HivePoints>(
                AppConstants.HIVE_POINTS
            )

        viewModel.initUi(merchant, hivePoints)
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            MerchantContract.NavAction.NavRedeemDetail -> {
                navHostController?.navigate(Routes.RedeemDetails.name)
            }

            null -> {

            }
        }
    })

    ToastApp(message = state.toastMsg)
    DoUnauthorization(state.isAuthErr, navHostController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
    ) {




        MerchantDetailHeader(
            state.merchant
        ) {
            navHostController?.popBackStack()
        }

        LazyColumn {


            items(state.list.size) {
                if (it == 0) Spacer(modifier = Modifier.height(8.sdp))
                val item = state.list[it]

                if (it >= state.list.size - 1 && state.isEndReached.not() && state.isLoading.not()) {
                    viewModel.merchantItems()
                }

                if (state.merchant.isBeeline()) {
                    MerchantPassCard(
                        item = item, state.hivePoints.availablePoints ?: 0.0
                    ) {

                        navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                            AppConstants.MERCHANT_DETAILS, state.merchant
                        )
                        navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                            AppConstants.MERCHANT_ITEM, item
                        )
                        navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                            AppConstants.HIVE_POINTS, state.hivePoints
                        )
                        viewModel.dispatch(
                            MerchantContract.Intent.NavRedeemDetails
                        )
                    }
                } else ItemComposable(item, state.hivePoints.availablePoints ?: 0.0) {

                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.MERCHANT_DETAILS, state.merchant
                    )
                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.MERCHANT_ITEM, item
                    )
                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.HIVE_POINTS, state.hivePoints
                    )
                    viewModel.dispatch(
                        MerchantContract.Intent.NavRedeemDetails
                    )
                }
                Spacer(modifier = Modifier.height(8.sdp))
            }

            item {
                if (state.isLoading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                10.sdp
                            ), horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }
            }
        }
    }

}


//@Composable
//fun ItemComposable(item: MerchantItem = MerchantItem(), availablePts: Int = 1, onClick: () -> Unit = {}) {
//
//    Box(
//        contentAlignment = Alignment.CenterStart
//    ) {
//        Card(
//            modifier = Modifier
//                .padding(
//                    start = 48.sdp, end = 22.sdp
//                )
//                .noRippleClickable {
////                    onClick()
//                },
//            colors = CardDefaults.cardColors(Color.White),
//            elevation = CardDefaults.cardElevation(10.sdp),
//            shape = RoundedCornerShape(4.sdp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        start = 40.sdp, top = 12.sdp, bottom = 12.sdp, end = 8.sdp
//                    )
//            ) {
//                Column(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
////                    Spacer(modifier = Modifier.height(6.sdp))
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        verticalAlignment = Alignment.Bottom,
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            text = item.title ?: "",
//                            style = baseStyle(),
//                            maxLines = 2,
//                            modifier = Modifier.weight(1f)
//                        )
//                        Text(
//                            text = "${item.hivePoints} Points", style = baseStyle().copy(
//                                color = Purple, fontWeight = FontWeight.W700, fontSize = 11.ssp
//                            ), modifier = Modifier
//                                .background(
//                                    color = FFEFE8FB, shape = RoundedCornerShape(size = 30.dp)
//                                )
//                                .padding(
//                                    horizontal = 4.sdp, vertical = 4.sdp
//                                ), maxLines = 1
//                        )
//                    }
//
//                    Spacer(modifier = Modifier.height(4.sdp))
//                    Text(
//                        text = item.description ?: "", style = baseStyle().copy(
//                            fontWeight = FontWeight.W300, fontSize = 12.ssp
//                        ), maxLines = 2
//                    )
//                    Spacer(modifier = Modifier.height(8.sdp))
//                    Text(
//                        text = "Redeem", style = baseStyle().copy(
//                            color = Color.White, fontWeight = FontWeight.Normal, fontSize = 11.ssp
//                        ), modifier = Modifier
//                            .background(
//                                color = if (item.canRedeem(availablePts)) FFE52E29 else FF777C80,
//                                shape = RoundedCornerShape(size = 30.dp)
//                            )
//                            .padding(
//                                horizontal = 18.sdp, vertical = 6.sdp
//                            )
//                            .noRippleClickable {
//                                if (item.canRedeem(availablePts)) onClick()
//                            }, maxLines = 1
//                    )
//                    Spacer(modifier = Modifier.height(22.sdp))
//                }
//            }
//        }
//
//        CircledImageWithBorder(
//            url = item.productImage ?: "",
//            size = 70,
//            modifier = Modifier.padding(start = 12.sdp),
//            showProgressBar = false
//        )
//    }
//}


@Composable
@Preview
fun ItemComposable(
    item: MerchantItem = MerchantItem(),
    availablePts: Double = 0.0,
    onClick: () -> Unit = {}
) {

    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .padding(
                    start = 48.sdp, end = 22.sdp
                )
                .noRippleClickable {
//                    onClick()
                },
            colors = CardDefaults.cardColors(Color.White),
            elevation = CardDefaults.cardElevation(4.sdp),
            shape = RoundedCornerShape(4.sdp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 44.sdp, top = 8.sdp, bottom = 12.sdp, end = 8.sdp
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title ?: "",
                            style = baseStyle(),
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(id = R.string.points, item.hivePoints?: ""), style = baseStyle().copy(
                                color = Purple, fontWeight = FontWeight.W700, fontSize = 11.ssp
                            ), modifier = Modifier
                                .background(
                                    color = FFEFE8FB, shape = RoundedCornerShape(size = 30.dp)
                                )
                                .padding(
                                    horizontal = 10.sdp, vertical = 5.sdp
                                ), maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.sdp))
                    Text(
                        text = item.description ?: "", style = baseStyle().copy(
                            fontWeight = FontWeight.W300, fontSize = 12.ssp
                        ), maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(12.sdp))
                    Text(
                        text = stringResource(id = R.string.redeem), style = baseStyle().copy(
                            color = Color.White, fontWeight = FontWeight.Normal, fontSize = 11.ssp
                        ), modifier = Modifier
                            .background(
                                color = if (item.canRedeem(availablePts)) FFE52E29 else FF777C80,
                                shape = RoundedCornerShape(size = 30.dp)
                            )
                            .padding(
                                horizontal = 18.sdp, vertical = 6.sdp
                            )
                            .noRippleClickable {
                                if (item.canRedeem(availablePts)) onClick()
                            }, maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.sdp))
                }
            }
        }

        CircledImageWithBorder(
            url = item.productImage ?: "",
            size = 70,
            modifier = Modifier.padding(start = 12.sdp, bottom = 8.sdp),
            showProgressBar = false
        )
    }
}

@Composable
fun MerchantPassCard(item: MerchantItem, availablePts: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.sdp)
            .noRippleClickable {
//                onClick()
            },
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.sdp),
        shape = RoundedCornerShape(4.sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.sdp, top = 8.sdp, bottom = 8.sdp, end = 4.sdp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.sdp
                    )

            ) {
                Spacer(modifier = Modifier.height(8.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title ?: "",
                        style = baseStyle(),
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.points, item.hivePoints ?: ""), style = baseStyle().copy(
                            color = Purple, fontWeight = FontWeight.W700, fontSize = 10.ssp
                        ), modifier = Modifier
                            .background(
                                color = FFEFE8FB, shape = RoundedCornerShape(size = 30.dp)
                            )
                            .padding(
                                horizontal = 4.sdp, vertical = 4.sdp
                            ), maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.sdp))
                Text(
                    text = item.description ?: "", style = baseStyle().copy(
                        fontWeight = FontWeight.W300, fontSize = 12.ssp
                    ), maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.sdp))
                Text(
                    text = stringResource(id = R.string.redeem), style = baseStyle().copy(
                        color = Color.White, fontWeight = FontWeight.Normal, fontSize = 11.ssp
                    ), modifier = Modifier
                        .background(
                            color = if (item.canRedeem(availablePts)) FFE52E29 else FF777C80,
                            shape = RoundedCornerShape(size = 30.dp)
                        )
                        .padding(
                            horizontal = 18.sdp, vertical = 6.sdp
                        )
                        .noRippleClickable {
                            if (item.canRedeem(availablePts)) onClick()
                        }, maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.sdp))
            }
        }
    }
}
