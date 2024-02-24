package com.cityof.glendale.screens.trips.savedtrips

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.theme.Purple
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


@Composable
@Preview
fun SavedTripItem(
    title: String = "W Milford St",
    address: String = "Glendale, CA 91203, USA",
    time: String = "4:33 PM",
    isOrigin: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isOrigin) OriginLineWithCircle()
        else DestinationLineWithCircle()
        Spacer(modifier = Modifier.width(4.sdp))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        ) {
            Text(
                text = title, style = baseStyle2().copy(
                    fontWeight = FontWeight.Bold
                ), maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = address, style = baseStyle2().copy(
                    fontSize = 12.ssp
                ), maxLines = 1
            )
        }
        Text(
            text = time, style = baseStyle2().copy(
                fontSize = 12.ssp, fontWeight = FontWeight.ExtraBold
            )
        )
    }
}


@Composable
@Preview
fun DestinationLineWithCircle(
    circleSize: Int = 16, height: Int = 40, color: Color = Purple
) {

    ConstraintLayout(
        modifier = Modifier
            .width(20.dp)
            .height(40.dp)
    ) {
        val (circle, verticalLine, circle1, circle2) = createRefs()
        Box(modifier = Modifier
            .constrainAs(circle) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .size(circleSize.dp)
            .border(2.dp, color, CircleShape)
            .padding(1.dp)
            .clip(CircleShape)
            .background(Color.White))
        VerticalDivider(modifier = Modifier
            .constrainAs(verticalLine) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(circle.top)
            }
            .height(12.dp), color = Purple, thickness = 2.dp)

        CircleOnly(modifier = Modifier.constrainAs(circle1) {
            bottom.linkTo(verticalLine.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        CircleOnly(modifier = Modifier
            .padding(top = 2.dp)
            .constrainAs(circle2) {
                bottom.linkTo(circle1.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
    }
}

@Composable
@Preview
fun OriginLineWithCircle(
    circleSize: Int = 16, height: Int = 40, color: Color = Purple
) {

    ConstraintLayout(
        modifier = Modifier
            .width(20.dp)
            .height(50.dp)
    ) {
        val (circle, verticalLine, circle1, circle2) = createRefs()
        Box(modifier = Modifier
            .constrainAs(circle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .size(circleSize.dp)
            .border(2.dp, color, CircleShape)
            .padding(1.dp)
            .clip(CircleShape)
            .background(Color.White))
        VerticalDivider(modifier = Modifier
            .constrainAs(verticalLine) {
                top.linkTo(circle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .height(12.dp), color = Purple, thickness = 2.dp)
        CircleOnly(modifier = Modifier.constrainAs(circle1) {
            top.linkTo(verticalLine.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
        CircleOnly(modifier = Modifier.constrainAs(circle2) {
            top.linkTo(circle1.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }
}


@Composable
@Preview
fun CircleOnly(
    modifier: Modifier = Modifier, circleSize: Int = 8, height: Int = 40, color: Color = Purple
) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .size(circleSize.dp)
            .border(2.dp, color, CircleShape)
            .clip(CircleShape)
            .background(Purple)
            .then(modifier)
    )
}
