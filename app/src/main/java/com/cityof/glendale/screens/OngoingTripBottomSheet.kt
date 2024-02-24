package com.cityof.glendale.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.BUTTON_HEIGHT
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.formattedPoints
import com.cityof.glendale.network.responses.getFormattedFromTime
import com.cityof.glendale.network.responses.getFormattedToTime
import com.cityof.glendale.network.responses.toIcon
import com.cityof.glendale.screens.more.CORNER_RADIUS
import com.cityof.glendale.screens.trips.savedtrips.SavedTripItem
import com.cityof.glendale.theme.FF69A251
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun OngoingTripBottomSheet(
    item: SavedTrip = SavedTrip(), onDirection: () -> Unit = {}
) {


    val context = LocalContext.current
    val sheetState =
        rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { false })


    ModalBottomSheet(
        containerColor = Color.White, onDismissRequest = {}, shape = RoundedCornerShape(
            topStart = CORNER_RADIUS.sdp, topEnd = CORNER_RADIUS.sdp
        ), sheetState = sheetState, dragHandle = null
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    horizontal = 20.sdp, vertical = 20.sdp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ic_loc_purple),
                        contentDescription = null
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                horizontal = 8.sdp
                            )
                    ) {
                        Text(
                            text = item.label ?: "", style = baseStyle2().copy(
                                fontWeight = FontWeight.Bold
                            ), maxLines = 1
                        )
                        Text(
                            text = item.formattedPoints(context),
                            style = baseStyle2().copy(
                                color = FF69A251, fontSize = 12.ssp
                            ),
                            maxLines = 1
                        )
                    }
                }

            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.sdp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.mode_of_transport),
                    modifier = Modifier.weight(1f),
                    style = baseStyle2().copy(
                        fontSize = 12.ssp, color = Color(0xFF324A5E)
                    )
                )
                Image(
                    painter = painterResource(id = item.toIcon()),
                    contentDescription = null,
                    modifier = Modifier.size(28.sdp)
                )
            }

            SavedTripItem(
                title = item.startingPoint ?: "",
                address = item.fromAddress ?: "",
                time = item.getFormattedFromTime()
            )
            Spacer(modifier = Modifier.height(6.dp))
            SavedTripItem(
                title = item.endDestination ?: "",
                address = item.toAddress ?: "",
                isOrigin = false,
                time = item.getFormattedToTime()
            )
            AppButton(
                title = stringResource(id = R.string.direction),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.sdp)
                    .height(BUTTON_HEIGHT.sdp),
                onClick = onDirection
            )
            Spacer(modifier = Modifier.height(52.sdp))
        }

    }
}