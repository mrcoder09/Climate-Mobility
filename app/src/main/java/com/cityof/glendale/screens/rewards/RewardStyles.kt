package com.cityof.glendale.screens.rewards

import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.CircledImageWithBorder
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.network.responses.HivePoints
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.network.responses.completeAddress
import ir.kaaveh.sdpcompose.sdp
import kotlinx.parcelize.Parcelize


@Parcelize
data class IncomingBundleMerchant(
    val merchant: Merchant,
    val merchantItem: MerchantItem,
    val hivePoints: HivePoints
) : Parcelable

@Composable
fun MerchantDetailHeader(merchant: Merchant, onBackPress: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)
    ) {
        AppBarWithBack(
            title = merchant.name ?: "", onBackClick = onBackPress
        )

//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = 24.sdp,
//                end = 54.sdp
//            )
//        )
        CircledImageWithBorder(url = merchant.merchantImage ?: "", size = 100)
        Spacer(modifier = Modifier.height(12.sdp))
        Text(
            text = merchant.name ?: "",
            style = baseStyle().copy(textAlign = TextAlign.Center),
            modifier = Modifier.padding(horizontal = 16.sdp)
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Row(
            verticalAlignment = Alignment.Top, modifier = Modifier.padding(horizontal = 16.sdp)
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
        Spacer(modifier = Modifier.height(22.sdp))
    }
}



