package com.cityof.glendale.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ir.kaaveh.sdpcompose.ssp


//val RobotoFontFamily = FontFamily(
//    Font(R.font.roboto_thinitalic, FontWeight.W100, FontStyle.Italic),
//    Font(R.font.roboto_thin, FontWeight.W100),
//    Font(R.font.roboto_regular, FontWeight.W400),
//    Font(R.font.roboto_mediumitalic, FontWeight.W500, FontStyle.Italic),
//    Font(R.font.roboto_medium, FontWeight.W500),
//    Font(R.font.roboto_lightitalic, FontWeight.W300, FontStyle.Italic),
//    Font(R.font.roboto_light, FontWeight.W300),
//    Font(R.font.roboto_italic, FontWeight.W400, FontStyle.Italic),
//    Font(R.font.roboto_bolditalic, FontWeight.W700, FontStyle.Italic),
//    Font(R.font.roboto_bold, FontWeight.W700),
//    Font(R.font.roboto_blackitalic, FontWeight.W900, FontStyle.Italic),
//    Font(R.font.roboto_black, FontWeight.W900),
//)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(

        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)


val bodyLarge = @Composable {
    TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 28.ssp,
    )
}
