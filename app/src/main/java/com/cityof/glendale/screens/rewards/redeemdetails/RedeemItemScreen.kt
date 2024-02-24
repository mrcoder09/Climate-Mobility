package com.cityof.glendale.screens.rewards.redeemdetails

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.CongratulationDialog
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.theme.FF4B2E85
import com.cityof.glendale.theme.FFF9F9F9
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
@Preview
fun RedeemItemScreenPreview() {
    RedeemItemScreen()
}


@Composable
fun RedeemItemScreen(
    navHostController: NavHostController? = null, viewModel: RedeemDetailViewModel = hiltViewModel()

) {

    val state by viewModel.state.collectAsState()

    CongratulationDialog(
        showDialog = state.showCongratulations, onDismiss = {
            viewModel.dispatch(RedeemDetailContract.Intent.ShowCongratulations())
            repeat(3) {
                navHostController?.popBackStack()
            }
        }, message = UIStr.ResStr(R.string.msg_redeem_congratulations)
    )

    ProgressDialogApp(state.isLoading)
    ToastApp(state.toastMsg)
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

        ItemOrderDetails(state.item, state.merchant)

        Spacer(modifier = Modifier.height(32.sdp))
        AppButton(
            title = stringResource(R.string.redeem_now), modifier = Modifier
                .height(46.sdp)
                .fillMaxWidth()
                .padding(
                    horizontal = 24.sdp
                ), style = StyleButton().copy(
                lineHeight = TextUnit(15f, TextUnitType.Sp)
            )
        ) {
            viewModel.dispatch(RedeemDetailContract.Intent.RedeemRewardClick)
        }
        Spacer(modifier = Modifier.height(32.sdp))
    }
}

@Composable
fun ItemOrderDetails(item: MerchantItem, merchant: Merchant) {
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
//            Spacer(modifier = Modifier.height(if (merchant.isBeelineOffer()) 20.sdp else 55.sdp))
            Spacer(modifier = Modifier.height(55.sdp))
            Text(
                text = item.title ?: "", style = baseStyleLarge().copy(
                    fontSize = 15.ssp, color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(6.sdp))
            Text(
                text = stringResource(id = R.string.points,item.hivePoints ?:""), style = baseStyle().copy(
                    color = FF4B2E85, fontWeight = FontWeight.Normal, fontSize = 10.ssp
                ), modifier = Modifier
                    .background(
                        color = Color.White, shape = RoundedCornerShape(size = 30.dp)
                    )
                    .padding(
                        horizontal = 14.sdp, vertical = 6.sdp
                    ), maxLines = 1
            )
            Spacer(modifier = Modifier.height(22.sdp))
        }

//        if (merchant.isBeelineOffer().not())

        Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()
        ) {
            CircledImageWithBorder(
                url = item.productImage ?: "", size = 90
            )
        }

    }
}


//@Preview(device = "spec:parent=pixel_5,orientation=landscape")
//@Preview(device = "spec:parent=pixel_5")
//@Composable
//fun ConfirmationDialog(
//    show: Boolean = true, onClick: () -> Unit = {}, onDismiss: () -> Unit = {}
//) {
//
//    if (show) Dialog(
//        properties = DialogProperties(
//            usePlatformDefaultWidth = false
//        ), onDismissRequest = onDismiss
//    ) {
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(
//                    all = HORIZONTAL_PADDING.sdp
//                ), shape = RoundedCornerShape(10.sdp), color = Color.White
//        ) {
//            Spacer(modifier = Modifier.height(10.sdp))
//            Row(
//                horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()
//            ) {
//
//                Image(
//                    painter = painterResource(id = R.drawable.ic_x),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .noRippleClickable(onDismiss)
//                        .padding(
//                            all = 10.sdp
//                        )
//                )
//            }
//            Spacer(modifier = Modifier.height(10.sdp))
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        horizontal = 16.sdp
//                    )
//                    .verticalScroll(rememberScrollState()),
//                horizontalAlignment = Alignment.CenterHorizontally,
//            ) {
//                Spacer(modifier = Modifier.height(40.sdp))
//                Image(
//                    painter = painterResource(id = R.drawable.ic_star_badges),
//                    modifier = Modifier
//                        .size(
//                            80.dp
//                        )
//                        .background(color = Color(0xFFFCEAEA))
//                        .padding(
//                            horizontal = 12.sdp
//                        ),
//                    contentDescription = ""
//                )
//                Spacer(modifier = Modifier.height(20.sdp))
//                TitleWithDesc(
//                    title = R.string.title_are_you_sure,
//                    desc = R.string.msg_want_to_redeem,
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    titleStyle = baseStyleLarge().copy(
//                        color = Color.Black
//                    ),
//                    descStyle = baseStyle2().copy(
//                        fontSize = 14.ssp, lineHeight = 20.ssp
//                    )
//                )
//                Spacer(modifier = Modifier.height(32.sdp))
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(
//                            horizontal = 5.sdp
//                        ), horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    AppButton(
//                        modifier = Modifier
//                            .height(46.sdp)
//                            .weight(1f),
//                        style = StyleButton().copy(
//                            lineHeight = TextUnit(15f, TextUnitType.Sp)
//                        ),
//                        title = stringResource(R.string.proceed),
//                        colors = ButtonDefaults.buttonColors(
//                            contentColor = Color.White, containerColor = PROFILE_RED_BUTTON
//                        ),
//                        onClick = onClick
//                    )
//                    Spacer(modifier = Modifier.width(12.sdp))
//                    AppButton(
//                        modifier = Modifier
//                            .height(46.sdp)
//                            .weight(1f), style = StyleButton().copy(
//                            lineHeight = TextUnit(15f, TextUnitType.Sp)
//                        ), title = stringResource(id = R.string.cancel), onClick = onDismiss
//                    )
//                }
//                Spacer(modifier = Modifier.height(24.sdp))
//            }
//        }
//    }
//}