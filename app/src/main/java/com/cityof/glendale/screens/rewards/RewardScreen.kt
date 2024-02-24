package com.cityof.glendale.screens.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.completeAddress
import com.cityof.glendale.network.responses.isItemPlural
import com.cityof.glendale.screens.rewards.RewardContract.Intent
import com.cityof.glendale.screens.rewards.RewardContract.NavAction
import com.cityof.glendale.theme.FF4B2E85
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.xt2Digit
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber

@Composable
@Preview
fun RewardScreenPreview() {
    RewardScreen(
        viewModel = RewardViewModel(
            AppPreferencesManagerImpl(LocalContext.current.appDataStore),
            AppRepository(MockApiService())
        )
    )
}

@Composable
fun RewardScreen(
    navHostController: NavHostController? = null, viewModel: RewardViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    val lazyColumnState = rememberLazyListState()


    ToastApp(message = state.toastMsg)
    DoUnauthorization(state.isAuthErr, navHostController)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.MERCHANT_DETAILS -> {
                navHostController?.navigate(Routes.MerchantDetails2.name)
            }

            null -> {}
//            NavAction.MERCHANT_DETAILS_BEELINE -> {
//                navHostController?.navigate(Routes.MerchantPass.name)
//            }
        }
    })



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9),
    ) {
        RewardHeader(state.hivePoints)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(FFF9F9F9), state = lazyColumnState
        ) {
//        item {
//            RewardHeader(state.hivePoints)
//        }

            items(state.list.size) {
                if (it == 0) Spacer(modifier = Modifier.height(8.sdp))
                val item = state.list[it]

                Timber.d("$it > ${state.list.size - 1} = ${it > state.list.size - 1}")
                Timber.d("state.isEndReached.not() = ${state.isEndReached.not()}")
                Timber.d("state.isLoading.not() = ${state.isLoading.not()}")

                if (it >= state.list.size - 1 && state.isEndReached.not() && state.isLoading.not()) {
                    viewModel.dispatch(Intent.LoadMerchant)
                }
                MerchantCard(
                    item
                ) {

                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.MERCHANT_DETAILS, item
                    )
                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.HIVE_POINTS, state.hivePoints
                    )
//                if (item.isBeeline()) {
//                    viewModel.dispatch(Intent.NavMerchantDetailBeeline)
//                } else {
                    viewModel.dispatch(Intent.NavMerchantDetail)
//                }


//                if (it == 0) {
//                    navHostController?.navigate(Routes.MerchantPass.name)
//                } else {
//                navHostController?.navigate(Routes.MerchantDetails2.name)
//                }
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

@Composable
fun MerchantCard(data: Merchant, onClick: () -> Unit) {

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
            CircledImageWithBorder(
                url = data.merchantImage ?: "", showProgressBar = false
            )
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
                        text = data.name ?: "",
                        style = baseStyle(),
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    Column {
                        Text(
                            text = stringResource(
                                if (data.isItemPlural()) R.string.options else R.string.option,
                                data.itemCount ?: ""
                            ), style = baseStyle().copy(
                                color = Purple, fontWeight = FontWeight.W700, fontSize = 10.ssp
                            ), modifier = Modifier
                                .background(
                                    color = FFEFE8FB, shape = RoundedCornerShape(size = 30.dp)
                                )
                                .padding(
                                    horizontal = 4.sdp, vertical = 4.sdp
                                ), maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.sdp))
                    }

                }

                Spacer(modifier = Modifier.height(4.sdp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_marker_yellow),
                        contentDescription = "Marker",
                        modifier = Modifier
                            .padding(
                                top = 2.sdp
                            )
                            .size(13.sdp)
                    )
                    Spacer(modifier = Modifier.width(2.sdp))
                    Text(
                        text = data.completeAddress(), style = baseStyle().copy(
                            fontWeight = FontWeight.Normal
                        ), maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(8.sdp))
                Text(text = stringResource(id = R.string.see_more), style = baseStyle().copy(
                    textDecoration = TextDecoration.Underline,
                    color = Purple,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.ssp
                ), modifier = Modifier.noRippleClickable {
                    onClick()
                })
                Spacer(modifier = Modifier.height(8.sdp))
            }
        }
    }
}


//@Composable
//fun RewardHeader(hivePoints: HivePoints) {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Purple),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = 10.sdp, top = 8.sdp, bottom = 8.sdp
//            ),
//            color = Purple,
//            titleColor = Color.White,
//        )
//
//        Box(
//            contentAlignment = Alignment.Center, modifier = Modifier
//                .padding(
//                    horizontal = 24.sdp
//                )
//                .fillMaxWidth()
//                .border(
//                    width = 0.5.dp,
//                    color = Color(0xFFA2AAAD),
//                    shape = RoundedCornerShape(size = 5.dp)
//                )
//
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(all = 8.sdp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                RewardHeaderCard(
//                    modifier = Modifier.weight(1f),
//                ) {
//                    RewardPoints(
//                        points = UIStr.Str("${hivePoints.totalPoints ?: ""}")
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.sdp))
//                RewardHeaderCard(
//                    modifier = Modifier.weight(1f)
//                ) {
//                    RewardPoints(
//                        points = UIStr.Str("${hivePoints.availablePoints ?: ""}"),
//                        desc = UIStr.Str("Available Points")
//                    )
//                }
//            }
//
//            Image(
//                painter = painterResource(id = R.drawable.ic_star_badges),
//                contentDescription = "Star Badges"
//            )
//        }
//        Spacer(modifier = Modifier.height(28.sdp))
//    }
//}

@Composable
fun RewardHeader(hivePoints: HivePoints) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppBarWithGlobe(
            modifier = Modifier.padding(
                start = 12.sdp,
//                end = 74.sdp
            ),
            color = Purple,
            titleColor = Color.White,
        )

//        Spacer(modifier = Modifier.height(4.sdp))
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .padding(
                    horizontal = 24.sdp
                )
                .fillMaxWidth()
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFA2AAAD),
                    shape = RoundedCornerShape(size = 5.dp)
                )

        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(all = 8.sdp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                RewardHeaderCard(
//                    modifier = Modifier.weight(1f),
//                ) {
//                    RewardPoints(
//                        points = UIStr.Str("${hivePoints.totalPoints ?: ""}")
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.sdp))
            RewardHeaderCard(
                modifier = Modifier.fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(8.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bee_hive),
                        contentDescription = "Star Badges"
                    )
                }
                RewardPoints(
                    points = UIStr.Str(hivePoints.availablePoints?.xt2Digit() ?: ""),
                    desc = UIStr.ResStr(R.string.available_points)
                )

            }
//            }


        }
        Spacer(modifier = Modifier.height(10.sdp))
    }
}

@Composable
fun RewardPoints(
    points: UIStr = UIStr.Str("1,500"), desc: UIStr = UIStr.Str("Total Earned Points")
) {
    Spacer(modifier = Modifier.height(10.sdp))
    Text(
        text = points.toStr(), modifier = Modifier.fillMaxWidth(), style = baseStyleLarge().copy(
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 16.ssp,
            fontWeight = FontWeight.W700
        )
    )
    Text(
        text = desc.toStr(), modifier = Modifier.fillMaxWidth(), style = baseStyle2().copy(
            textAlign = TextAlign.Center, color = Color.White, fontSize = 14.ssp
        )
    )
    Spacer(modifier = Modifier.height(10.sdp))
}

@Composable
fun RewardHeaderCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(FF4B2E85),
        shape = RoundedCornerShape(4.sdp),
        content = content
    )
}
