package com.cityof.glendale.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cityof.glendale.R

val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_thinitalic, FontWeight.W100, FontStyle.Italic),
    Font(R.font.roboto_thin, FontWeight.W100),
    Font(R.font.roboto_regular, FontWeight.W400),
    Font(R.font.roboto_mediumitalic, FontWeight.W500, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.W500),
    Font(R.font.roboto_lightitalic, FontWeight.W300, FontStyle.Italic),
    Font(R.font.roboto_light, FontWeight.W300),
    Font(R.font.roboto_italic, FontWeight.W400, FontStyle.Italic),
    Font(R.font.roboto_bolditalic, FontWeight.W700, FontStyle.Italic),
    Font(R.font.roboto_bold, FontWeight.W700),
    Font(R.font.roboto_blackitalic, FontWeight.W900, FontStyle.Italic),
    Font(R.font.roboto_black, FontWeight.W900),
)

val RobotoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 30.sp,
        letterSpacing = 0.sp,
        lineHeight = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W700,
        fontSize = 24.sp,
        letterSpacing = 0.sp,
        lineHeight = 30.sp
    ),
    displaySmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 26.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 18.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 22.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.sp,
        lineHeight = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp,
        lineHeight = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        letterSpacing = 1.5.sp,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = 1.5.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        letterSpacing = 1.5.sp,
        lineHeight = 18.sp
    )
)
