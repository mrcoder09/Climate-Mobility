package com.cityof.glendale.composables.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cityof.glendale.theme.FFCDCDCD
import ir.kaaveh.sdpcompose.sdp


@Composable
@Preview
fun VerticalLine(
    color: Color = FFCDCDCD, height: Dp = 50.sdp, width: Float = 12f
) {

    Canvas(
        Modifier
            .width(width = 1.dp)
            .height(height)
            .padding(vertical = 1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = width
        )
    }
}


@Composable
fun VerticalDottedLine(
    color: Color = FFCDCDCD, dotSize: Float = 10f, height: Int = 50
) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotSize, dotSize), 0f)
    Canvas(
        Modifier
            .width(10.sdp)
            .height(height.sdp)
            .padding(vertical = 1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            pathEffect = pathEffect
        )
    }
}


@Composable
fun CardComposable(
    modifier: Modifier = Modifier.fillMaxWidth(),
    cardcolor: CardColors = CardDefaults.cardColors(Color.White),
    cornerRadius: Int = 4,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = cardcolor,
        shape = RoundedCornerShape(cornerRadius.sdp),
        content = content
    )
}

@Preview
@Composable
fun PreviewOnly() {
}
