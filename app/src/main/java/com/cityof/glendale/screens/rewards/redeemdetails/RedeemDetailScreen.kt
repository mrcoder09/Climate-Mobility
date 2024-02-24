package com.cityof.glendale.screens.rewards.redeemdetails

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.CongratulationDialog
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.network.responses.completeAddress
import com.cityof.glendale.network.responses.isBeeline
import com.cityof.glendale.theme.FF228D00
import com.cityof.glendale.theme.FF4B2E85
import com.cityof.glendale.theme.FFA28DCA
import com.cityof.glendale.theme.FFEFB30F
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.xt2Digit
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
@Preview
fun RedeemDetailPreview() {
    RedeemDetailScreen(
        viewModel = RedeemDetailViewModel(
            AppRepository(MockApiService())
        )
    )
}


@Composable
fun RedeemDetailScreen(
    navHostController: NavHostController? = null, viewModel: RedeemDetailViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
//    val navigation by viewModel.navigation.collectAsState(initial = null)

    var showRedeemDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showRedeemDialogForPass by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit, block = {
        val item = navHostController?.previousBackStackEntry?.savedStateHandle?.get<MerchantItem>(
            AppConstants.MERCHANT_ITEM
        )

        val merchant = navHostController?.previousBackStackEntry?.savedStateHandle?.get<Merchant>(
            AppConstants.MERCHANT_DETAILS
        )

        val hivepoint =
            navHostController?.previousBackStackEntry?.savedStateHandle?.get<HivePoints>(
                AppConstants.HIVE_POINTS
            )
        viewModel.initUi(merchant, item, hivepoint)
    })

//    var showCongratulations by rememberSaveable {
//        mutableStateOf(false)
//    }


    ConfirmationDialog(show = showRedeemDialog, onClick = {
        showRedeemDialog = false
        navHostController?.navigate(Routes.RedeemItemScreen.name)
    }, onDismiss = {
        showRedeemDialog = false
    })

    ConfirmationDialogForPass(show = showRedeemDialogForPass, onClick = {
        viewModel.dispatch(RedeemDetailContract.Intent.RedeemRewardClick)
        showRedeemDialogForPass = false
    }, onDismiss = {
        showRedeemDialogForPass = false
    })

    CongratulationDialog(
        showDialog = state.showCongratulations, onDismiss = {
            viewModel.dispatch(RedeemDetailContract.Intent.ShowCongratulations())
            repeat(2) {
                navHostController?.popBackStack()
            }
        }, message = UIStr.ResStr(R.string.msg_redeem_congratulations_for_pass)
    )

    ProgressDialogApp(state.isLoading)
    if (state.merchant.isBeeline()) ToastApp(state.toastMsg)
    DoUnauthorization(state.isAuthErr, navHostController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        MerchantDetailHeader(
            state.merchant
        ) {
            navHostController?.popBackStack()
        }

        OrderDetails(state.item, state.merchant, state.hivePoints)

        Spacer(modifier = Modifier.height(32.sdp))
        AppButton(
            title = stringResource(R.string.redeem_rewards),
            modifier = Modifier
                .height(46.sdp)
                .fillMaxWidth()
                .padding(
                    horizontal = 24.sdp
                ),
            style = StyleButton().copy(
                lineHeight = TextUnit(15f, TextUnitType.Sp)
            )
        ) {
            if (state.merchant.isBeeline()) {
                showRedeemDialogForPass = true
            } else {
                showRedeemDialog = true
            }
        }
        Spacer(modifier = Modifier.height(32.sdp))
    }
}

@Composable
fun OrderDetails(item: MerchantItem, merchant: Merchant, hivePoints: HivePoints) {
    Spacer(modifier = Modifier.height(24.sdp))
    Text(
        text = stringResource(R.string.order_details), style = baseStyleLarge().copy(
            fontSize = 15.ssp, color = Color.Black
        )
    )
    Spacer(modifier = Modifier.height(10.sdp))
    Box {
        Column(
            modifier = Modifier
                .padding(
                    top = 50.sdp, start = 24.sdp, end = 24.sdp
                )
                .fillMaxWidth()
                .background(
                    color = FF4B2E85, shape = RoundedCornerShape(size = 10.dp)
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(if (merchant.isBeeline()) 20.sdp else 55.sdp))
//            Spacer(modifier = Modifier.height(55.sdp))
            Text(
                text = item.title ?: "", style = baseStyleLarge().copy(
                    fontSize = 15.ssp, color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(6.sdp))
            Text(
                text = "${item.hivePoints} Points", style = baseStyle().copy(
                    color = FF4B2E85, fontWeight = FontWeight.Normal, fontSize = 10.ssp
                ), modifier = Modifier
                    .background(
                        color = Color.White, shape = RoundedCornerShape(size = 30.dp)
                    )
                    .padding(
                        horizontal = 14.sdp, vertical = 6.sdp
                    ), maxLines = 1
            )
            Spacer(modifier = Modifier.height(18.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.sdp
                    )
            ) {
                Text(
                    text = stringResource(R.string.redeem_points), style = baseStyleLarge().copy(
                        fontSize = 16.ssp, color = Color.White
                    )
                )
            }
            PointComposable(
                points = UIStr.Str("${hivePoints.availablePoints?.xt2Digit()}")
            )
            PointComposable(
                UIStr.ResStr(R.string.order_total_cost),
                UIStr.Str("${item.hivePoints}"),
                color = FFEFB30F
            )
            HorizontalDivider(
                color = FFA28DCA, modifier = Modifier.padding(
                    start = 20.sdp, end = 20.sdp, top = 8.sdp, bottom = 4.sdp
                )
            )
            PointComposable(
                UIStr.ResStr(R.string.updated_balance),
                UIStr.Str("${hivePoints.availablePoints?.minus((item.hivePoints ?: 0))?.xt2Digit()}")
            )
            Spacer(modifier = Modifier.height(22.sdp))
        }

        if (merchant.isBeeline().not()) Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
        ) {
            CircledImageWithBorder(
                url = item.productImage ?: "", size = 90
            )
        }

    }
}

@Composable
fun PointComposable(
    title: UIStr = UIStr.ResStr(R.string.available_points),
    points: UIStr = UIStr.Str("1,452.00"),
    color: Color = Color.White
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(
            start = 20.sdp, end = 20.sdp, top = 8.sdp
        )
    ) {
        Text(
            text = title.toStr(), style = baseStyle2().copy(
                color = color, fontSize = 13.ssp
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = points.toStr(), style = baseStyle().copy(
                color = color, fontSize = 13.ssp
            )
        )
    }
}

@Composable
fun MerchantDetailHeader(merchant: Merchant, onBackPress: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        AppBarWithBack(
            title = stringResource(id = R.string.redeem_details), onBackClick = onBackPress
        )

//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = 24.sdp,
//                end = 42.sdp
//            )
//        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircledImageWithBorder(
                url = merchant.merchantImage ?: "", size = 100
            )
            Column {
                Spacer(modifier = Modifier.height(12.sdp))
                Text(
                    text = merchant.name ?: "", style = baseStyleLarge().copy(
                        textAlign = TextAlign.Center, fontSize = 16.ssp, color = Color.Black
                    ), modifier = Modifier.padding(horizontal = 16.sdp)
                )
                Spacer(modifier = Modifier.height(4.sdp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(horizontal = 16.sdp)
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
                        text = merchant.completeAddress(), style = baseStyle().copy(
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

            }

        }
        Spacer(modifier = Modifier.height(22.sdp))
    }
}


@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Preview(device = "spec:parent=pixel_5")
@Composable
fun ConfirmationDialog(
    show: Boolean = true, onClick: () -> Unit = {}, onDismiss: () -> Unit = {}
) {

    if (show) DialogApp(
        onDismiss = onDismiss
    ) {
        Spacer(modifier = Modifier.height(2.sdp))
//        Row(
//            horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_x),
//                contentDescription = null,
//                modifier = Modifier
//                    .noRippleClickable(onDismiss)
//                    .padding(
//                        all = 10.sdp
//                    )
//            )
//        }
        Spacer(modifier = Modifier.height(10.sdp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.sdp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.sdp))

            Box(modifier = Modifier
                .clip(CircleShape)
                .background(color = Color(0xFFFCEAEA))){
                Image(
                    painter = painterResource(id = R.drawable.bee_hive), modifier = Modifier
                        .size(
                            80.dp
                        )

                        .padding(
                            horizontal = 12.sdp
                        ), contentDescription = ""
                )
            }

            Spacer(modifier = Modifier.height(20.sdp))
            TitleWithDesc(
                title = R.string.title_are_you_sure,
                desc = R.string.msg_want_to_redeem,
                horizontalAlignment = Alignment.CenterHorizontally,
                titleStyle = baseStyleLarge().copy(
                    color = Color.Black
                ),
                descStyle = baseStyle2().copy(
                    fontSize = 14.ssp, lineHeight = 20.ssp
                )
            )
            Spacer(modifier = Modifier.height(32.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.sdp
                    ), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppButton(
                    modifier = Modifier
                        .height(46.sdp)
                        .weight(1f),
                    style = StyleButton().copy(
                        lineHeight = TextUnit(15f, TextUnitType.Sp)
                    ),
                    title = stringResource(R.string.proceed),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = FF228D00
                    ),
                    onClick = onClick
                )
                Spacer(modifier = Modifier.width(12.sdp))
                AppButton(
                    modifier = Modifier
                        .height(46.sdp)
                        .weight(1f), style = StyleButton().copy(
                        lineHeight = TextUnit(15f, TextUnitType.Sp)
                    ), title = stringResource(id = R.string.cancel), onClick = onDismiss
                )
            }
            Spacer(modifier = Modifier.height(24.sdp))
        }
    }
}


@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Preview(device = "spec:parent=pixel_5")
@Composable
fun ConfirmationDialogForPass(
    show: Boolean = true, onClick: () -> Unit = {}, onDismiss: () -> Unit = {}
) {

    if (show) DialogApp(
        onDismiss = onDismiss
    ) {
        Spacer(modifier = Modifier.height(2.sdp))
//        Row(
//            horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_x),
//                contentDescription = null,
//                modifier = Modifier
//                    .noRippleClickable(onDismiss)
//                    .padding(
//                        all = 10.sdp
//                    )
//            )
//        }
        Spacer(modifier = Modifier.height(10.sdp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.sdp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.sdp))
            Box(modifier = Modifier
                .clip(CircleShape)
                .background(color = Color(0xFFFCEAEA))){
                Image(
                    painter = painterResource(id = R.drawable.bee_hive), modifier = Modifier
                        .size(
                            80.dp
                        )

                        .padding(
                            horizontal = 12.sdp
                        ), contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.height(20.sdp))
            TitleWithDesc(
                title = R.string.title_are_you_sure,
                desc = R.string.msg_want_to_redeem_for_pass,
                horizontalAlignment = Alignment.CenterHorizontally,
                titleStyle = baseStyleLarge().copy(
                    color = Color.Black
                ),
                descStyle = baseStyle2().copy(
                    fontSize = 14.ssp, lineHeight = 20.ssp
                )
            )
            Spacer(modifier = Modifier.height(32.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 5.sdp
                    ), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppButton(
                    modifier = Modifier
                        .height(46.sdp)
                        .weight(1f),
                    style = StyleButton().copy(
                        lineHeight = TextUnit(15f, TextUnitType.Sp)
                    ),
                    title = stringResource(R.string.proceed),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = FF228D00
                    ),
                    onClick = onClick
                )
                Spacer(modifier = Modifier.width(12.sdp))
                AppButton(
                    modifier = Modifier
                        .height(46.sdp)
                        .weight(1f), style = StyleButton().copy(
                        lineHeight = TextUnit(15f, TextUnitType.Sp)
                    ), title = stringResource(id = R.string.cancel), onClick = onDismiss
                )
            }
            Spacer(modifier = Modifier.height(24.sdp))
        }
    }
}