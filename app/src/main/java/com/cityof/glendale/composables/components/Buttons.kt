package com.cityof.glendale.composables.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cityof.glendale.R
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.theme.RobotoFontFamily
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


val BUTTON_HEIGHT = 46

val StyleButton = @Composable {
    TextStyle(
        fontSize = 14.ssp,
        fontWeight = FontWeight.Medium,
        lineHeight = 18.ssp,
        fontFamily = RobotoFontFamily
    )
}

val ModifierButton = @Composable {
    Modifier
        .fillMaxWidth()
        .height(BUTTON_HEIGHT.sdp)
}

@Composable
fun AppButton(
    title: String,
    modifier: Modifier = ModifierButton(),
    style: TextStyle = StyleButton(),
    shape: RoundedCornerShape = RoundedCornerShape(8.sdp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        contentColor = Color.White, containerColor = Purple
    ),
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        contentPadding = PaddingValues(0.dp),
        shape = shape
    ) {
        Text(text = title, style = style, maxLines = 1)
    }
}

@Composable
@Preview
fun ButtonWithLeadingIcon(
    modifier: Modifier = ModifierButton(),
    title: String = "Delete Account",
    icon: Int = R.drawable.ic_delete,
    style: TextStyle = StyleButton(),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        contentColor = Color.White, containerColor = Purple
    ),
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick, modifier = modifier, colors = colors,
        shape = RoundedCornerShape(8.sdp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Image(painter = painterResource(id = icon), contentDescription = "")
        Text(
            text = title, style = style, modifier = Modifier.padding(
                start = 8.sdp
            )
        )
    }
}

@Composable
fun AppOutlinedButton(
    modifier: Modifier = ModifierButton(),
    style: TextStyle = StyleButton(),
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(
        contentColor = Purple,
    ),
    title: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick, colors = colors, border = BorderStroke(
            width = 1.dp, color = Purple
        ), modifier = modifier, shape = RoundedCornerShape(6.sdp)
    ) {
        Text(text = title, style = style)
    }
}