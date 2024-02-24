package com.cityof.glendale.composables.components

import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.textSelectionRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.cityof.glendale.R
import com.cityof.glendale.composables.cornerShape
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.theme.ContainerColor
import com.cityof.glendale.theme.ERR_RED
import com.cityof.glendale.theme.FF434343
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.theme.RobotoFontFamily
import com.cityof.glendale.theme.TEXT_FIELD_HINT_COLOR
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

private const val TAG = "TextFields"
const val TF_HEIGHT = 50
const val TOP_HEIGHT = 6
const val BOTTOM_HEIGHT = 6

//const val TEXT_LENGTH = 5000
const val TEXT_MAX_LENGTH = 255
const val MOBILE_LENGTH = 10
const val EMAIL_LENGTH = 50
const val PASSWORD_LENGTH = 20

const val BLANK = ""
const val SINGLE_SPACE = " "
const val SPECIAL_CHARACTERS = "~!@#$%^&*()_+<>?,./[]{}:;'"
const val SPECIAL_CHARACTERS_FOR_FIRST_LAST_NAME = "~!@#$%^&*()_+<>?,./[]{}:;"
const val NUMBERS = "0123456789"
const val SMALL_ALPHABET = "abcdefghijklmnopqrstuvwxyz"
const val CAPITAL_ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val ALL_CHARACTERS =
    "$BLANK$SINGLE_SPACE$SPECIAL_CHARACTERS$NUMBERS$SMALL_ALPHABET$CAPITAL_ALPHABETS"


@Composable
fun baseStyleLarge(): TextStyle {
    return TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 20.ssp,
        color = Purple,
        lineHeight = 24.ssp, //TextUnit(28f, TextUnitType.Sp),
        fontFamily = RobotoFontFamily
    )
}

/**
 * @author Satnam Singh
 */
@Composable
fun baseStyle(): TextStyle {
    return TextStyle(
        color = Color.Black, //FF777C80,
        fontSize = 13.ssp,
        fontWeight = FontWeight.Medium,
        lineHeight = 17.ssp,
        letterSpacing = 0.2f.sp,  //TextUnit(0.2f, TextUnitType.Sp),
        fontFamily = RobotoFontFamily
    )
}


/**
 * This is a BASE Style for APP's Dashboard UI, Do not use in
 * any Pre-Login Screens
 * @author Satnam Singh
 */
@Composable
fun baseStyle2(): TextStyle {
    return TextStyle(
        color = Color.Black,      //FF777C80,
        fontSize = 15.ssp,
        fontWeight = FontWeight.Normal,
        lineHeight = 19.ssp,
        letterSpacing = 0.2f.sp,
        fontFamily = RobotoFontFamily
    )
}


val TextFieldColor = @Composable {
    TextFieldDefaults.colors(
        focusedContainerColor = ContainerColor,
        unfocusedContainerColor = ContainerColor,
        disabledContainerColor = ContainerColor,
        errorContainerColor = ContainerColor,
        cursorColor = Color.Black,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        errorCursorColor = ERR_RED,
        errorTrailingIconColor = ERR_RED
    )
}


val FloatLabelStyle = @Composable {
    baseStyle().copy(
        fontWeight = FontWeight.Normal, fontSize = 12.ssp, color = TEXT_FIELD_HINT_COLOR,//FF777C80
    )
}


val FloatLabelEditProfile = @Composable {
    FloatLabelStyle().copy(
        fontWeight = FontWeight.Medium
    )
}

val TextInputStyle = @Composable {
    TextStyle(
        fontSize = 13.ssp,
        fontWeight = FontWeight.W400,
        letterSpacing = TextUnit(0.2f, TextUnitType.Sp),
        fontFamily = RobotoFontFamily,
        color = Color.Black
    )
}

val ErrTextStyle = @Composable {
    TextStyle(
        fontSize = 12.ssp,
        fontWeight = FontWeight.W400,
        letterSpacing = TextUnit(0.2f, TextUnitType.Sp),
        fontFamily = RobotoFontFamily,
        color = ERR_RED
    )
}

@Composable
fun AppTextField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String = "Email",
    value: String = "",
    error: String = "",
    hintStyle: TextStyle = FloatLabelStyle(),
    maxLength: Int = TEXT_MAX_LENGTH,
    icon: Int = R.drawable.ic_sms,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    valueChanged: (String) -> Unit
) {

    Timber.d("${value.length}")

    var textSelection by remember {
        mutableIntStateOf(value.length)
    }
    Timber.d("$textSelection")

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) valueChanged(it.trim())
            },
            label = {
                Text(label, maxLines = 1, style = hintStyle)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp)
                .onFocusChanged {
                    Timber.d("${it.isFocused}")
                    if (it.isFocused.not()) {
//                        textSelection = 0
                    }
                },
            shape = cornerShape(),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            colors = TextFieldColor(),
            isError = error.isNotEmpty(),
            textStyle = TextInputStyle(),
        )
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }
}


@Composable
fun AppPasswordField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String = "",
    value: String = "",
    error: String = "",
    hintStyle: TextStyle = FloatLabelStyle(),
    leadingIcon: Int = R.drawable.ic_unlock,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    valueChanged: (String) -> Unit
) {

    val passwordVisible = rememberSaveable { mutableStateOf(false) }
    val trailingIcon = @Composable {
        IconButton(onClick = { passwordVisible.value = passwordVisible.value.not() }) {
            if (passwordVisible.value) Icon(
                painter = painterResource(id = R.drawable.ic_eye_open), contentDescription = null
            )
            else Icon(
                painter = painterResource(id = R.drawable.ic_eye_close), contentDescription = null
            )
        }
    }


    Column(modifier = modifier) {
        TextField(
            value = value.trim(),
            onValueChange = {
                val length = it.length
                if (length <= PASSWORD_LENGTH && StringUtils.containsAny(SINGLE_SPACE, it).not()) {
                    valueChanged(it.trim())
                }
            },
            label = {
                Text(
                    label, style = hintStyle
                )
            },
            isError = error.isNotEmpty(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "Back button"
                )
            },
            trailingIcon = trailingIcon,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp)
                .semantics {
                    textSelectionRange = TextRange(0, 0)
                },
            shape = cornerShape(),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            colors = TextFieldColor(),
            textStyle = TextInputStyle()
        )
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }

}

@Composable
fun AppText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    icon: Int = R.drawable.ic_sms,
    value: String = "",
    label: String = "",
    error: String = "",
    hintStyle: TextStyle = FloatLabelStyle(),
    onClick: () -> Unit
) {

    Column(modifier = modifier) {
        TextField(value = value,
            onValueChange = {},
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            label = {
                Text(
                    label, style = hintStyle
                )
            },
            isError = error.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp),
            shape = cornerShape(),
            singleLine = true,
            readOnly = true,
            colors = TextFieldColor(),
            textStyle = TextInputStyle(),
            interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onClick()
                        }
                    }
                }
            })
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }
}


@Composable
fun ErrorText(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(
            bottom = 12.sdp
        ), err: String = ""
) {
    Text(
        modifier = modifier, text = err, style = ErrTextStyle()
    )
}


@Composable
fun clickSpanStyle() = SpanStyle(
    color = Purple,
    fontWeight = FontWeight.Medium,
    fontSize = 13.ssp,
    fontFamily = RobotoFontFamily,
)

@Composable
fun normalSpanStyle(): SpanStyle {
    return SpanStyle(
        color = FF434343,
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.ssp
    )
}

@Composable
fun AnnotatedClickableText(
    text: String,
    clickableText: String,
    normalSpanStyle: SpanStyle = normalSpanStyle(),
    clickSpanStyle: SpanStyle = clickSpanStyle(),
    paragraphStyle: ParagraphStyle = ParagraphStyle(
        textAlign = TextAlign.Center
    ),
    maxLines: Int = 1,
    onClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {

        withStyle(
            style = paragraphStyle
        ) {
            //append your initial text
            withStyle(style = normalSpanStyle) {
                append(text)
                append(" ")
            }

            //Start of the pushing annotation which you want to color and make them clickable later
            pushStringAnnotation(
                tag = clickableText,// provide tag which will then be provided when you click the text
                annotation = clickableText
            )


            //add text with your different color/style
            withStyle(
                style = clickSpanStyle
            ) {
                append(clickableText)
            }
            // when pop is called it means the end of annotation with current tag
            pop()
        }

    }

    ClickableText(text = annotatedText,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        onClick = { offset ->
            annotatedText.getStringAnnotations(
                tag = clickableText,// tag which you used in the buildAnnotatedString
                start = offset, end = offset
            ).firstOrNull()?.let { annotation ->
                //do your stuff when it gets clicked
                Log.d("Clicked", annotation.item)
                onClick()
            }
        })
}


@Composable
fun UnderlinedClickableText(
    text: String, spanStyle: SpanStyle = SpanStyle(
        color = Purple,
        fontWeight = FontWeight.W500,
        fontSize = 12.ssp,
        fontFamily = RobotoFontFamily
    ), onClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = spanStyle.color,
                fontWeight = spanStyle.fontWeight,
                fontSize = spanStyle.fontSize,
                fontFamily = RobotoFontFamily
            )
        ) {
            append(text)
            addStringAnnotation(
                tag = "clickable", annotation = "true", start = 0, end = text.length
            )
        }

    }

    ClickableText(text = annotatedText) { offset ->
        annotatedText.getStringAnnotations("clickable", offset, offset).firstOrNull()?.let {
            onClick()
        }
    }
}



@Composable
fun UnderLinedWithClick(
    text :String= stringResource(R.string.i_accept_terms),
    underlined :String = stringResource(id = R.string.term_conditions),
    onClick: () -> Unit
){
    val annotatedText = buildAnnotatedString {

        withStyle(
            style = normalSpanStyle().copy(
                color = Color.Black
            )
        ) {
            append(text)
            append(" ")
        }

        pushStringAnnotation(
            tag = underlined,// provide tag which will then be provided when you click the text
            annotation = underlined
        )


        withStyle(
            style = clickSpanStyle().copy(
                textDecoration = TextDecoration.Underline, color = Purple
            )
        ) {
            append(underlined)
        }
        pop()
    }

    ClickableText(text = annotatedText) { offset ->
        annotatedText.getStringAnnotations(tag = underlined, offset, offset).firstOrNull()
            ?.let {
                Timber.d("onClick")
                onClick()
            }
    }
}


@Composable
fun AppClickableText(
    stringRes: Int = R.string.app_name, onClick: (Int) -> Unit, spanStyle: SpanStyle = SpanStyle(
        color = Purple,
        fontWeight = FontWeight.W500,
        fontSize = 12.ssp,
        fontFamily = RobotoFontFamily
    )
) {
    ClickableText(
        text = AnnotatedString(
            stringResource(id = stringRes), spanStyle = spanStyle
        ), onClick = onClick
    )
}

@Composable
fun AppClickableText(
    value: String = "",
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    spanStyle: SpanStyle = SpanStyle(
        color = Purple,
        fontWeight = FontWeight.W500,
        fontSize = 12.ssp,
        fontFamily = RobotoFontFamily
    )
) {
    ClickableText(
        modifier = modifier, text = AnnotatedString(
            value, spanStyle = spanStyle
        ), onClick = onClick
    )
}

@Composable
fun checkBoxTextStyle(): TextStyle {
    return baseStyle().copy(
        fontSize = 12.ssp, lineHeight = 17.ssp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelledCheckbox(
    value: Boolean = false,
    label: String,
    textStyle: TextStyle = checkBoxTextStyle(),
    onChecked: (Boolean) -> Unit
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Row(
            modifier = Modifier.defaultMinSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = value, onCheckedChange = {
                    onChecked(it)
                }, enabled = true, colors = CheckboxDefaults.colors(Purple)
            )
            Spacer(modifier = Modifier.width(2.sdp))
            Text(
                text = label, style = textStyle
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelledRadioButton(value: Boolean = false, label: String, onClick: () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Row(
            modifier = Modifier.defaultMinSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = value, onClick = onClick
            )
            Spacer(modifier = Modifier.width(2.sdp))
            Text(
                text = label, style = baseStyle().copy()
            )
        }
    }
}

@Composable
fun ProfileTextField(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .background(Color.White),
    label: String = "",
    value: String = ""
) {

    Column(modifier = modifier) {
        Text(
            text = label, style = FloatLabelEditProfile()
        )
        Spacer(modifier = Modifier.height(2.sdp))
        Text(
            text = value, style = baseStyle().copy(
//                color = TEXT_FIELD_HINT_COLOR,
                fontSize = 14.ssp, fontWeight = FontWeight.Normal
            ), maxLines = 1
        )
        Spacer(modifier = Modifier.height(5.sdp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.sdp))
    }
}

@Composable
@Preview
fun PhoneTextField(
    value: String = "",
    error: String = "",
    maxChars: Int = 10,
    onCrossClick: () -> Unit = {},
    valueChanged: (String, String) -> Unit = { _, _ -> }
) {


    Column(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = {
                val txt = it.trim()
                if (txt.length <= maxChars) {
                    valueChanged(txt, formatMobile(it))
                }
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dialer),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            label = {
                Text(
                    stringResource(id = R.string.phone_number), style = FloatLabelStyle()
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) IconButton(onClick = onCrossClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_x),
                        tint = MaterialTheme.colorScheme.outline,
                        contentDescription = "trailing icon"
                    )
                }
            },
            isError = error.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp),
            shape = cornerShape(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            colors = TextFieldColor(),
            visualTransformation = MobileTransformation(),
            textStyle = TextInputStyle()
        )
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }


}

fun formatMobile(input: String): String {
    return if (input.length == 10) {
        val regex = """(\d{3})(\d{3})(\d{4})""".toRegex()
        regex.replace(input, "($1) $2-$3")
    } else {
        input
    }
}

@Composable
fun TitleWithDesc(
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    title: Int,
    spaceBetween: Int = 6,
    desc: Int? = null,
    titleStyle: TextStyle = baseStyleLarge().copy(fontSize = 24.ssp, lineHeight = 30.ssp),
    descStyle: TextStyle = baseStyle().copy(fontSize = 14.ssp, lineHeight = 19.ssp)
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = stringResource(title),
            style = titleStyle,
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(spaceBetween.sdp))
        if (desc != null) Text(
            text = stringResource(desc),
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start,
            style = descStyle
        )
    }
}

@Composable
fun TitleWithDesc(
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    title: String,
    height: Int = 1,
    desc: String? = null,
    titleStyle: TextStyle = baseStyleLarge(),
    descStyle: TextStyle = baseStyle2()
) {
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = title, style = titleStyle,
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(height.sdp))
        if (desc != null) Text(
            text = desc,
            textAlign = if (horizontalAlignment == Alignment.CenterHorizontally) TextAlign.Center else TextAlign.Start,
            style = descStyle
        )
    }
}

@Composable
fun ZipCodeComposable(
    value: String = stringResource(id = R.string.zip_code),
    error: String = "",
    hintStyle: TextStyle = FloatLabelEditProfile(),
    valueChanged: (String) -> Unit,
) {

    val length = 5
    Column {
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= length) {
                    valueChanged(it)
                }
            },
            label = {
                Text(
                    stringResource(R.string.zip_code), style = hintStyle, maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_unlock),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(TF_HEIGHT.sdp),
            shape = cornerShape(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldColor(),
            isError = error.isNotEmpty(),
            textStyle = TextInputStyle()
        )
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }
}


@Composable
fun StreetAddressComposable(
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String = "Email",
    value: String = "",
    error: String = "",
    hintStyle: TextStyle = FloatLabelStyle(),
    maxLength: Int = TEXT_MAX_LENGTH,
    icon: Int = R.drawable.ic_marker,
    characterAccepted: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences
    ),
    valueChanged: (String) -> Unit
) {

    Column(modifier = modifier) {
        TextField(
            value = value,
            onValueChange = {
                if (it.length <= maxLength) valueChanged(it)
            },
            label = {
                Text(label, maxLines = 1, style = hintStyle)
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = icon),
                    tint = MaterialTheme.colorScheme.outline,
                    contentDescription = "leading icon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp),
            shape = cornerShape(),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            colors = TextFieldColor(),
            isError = error.isNotEmpty(),
            textStyle = TextInputStyle()
        )
        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 2.sdp,
                        start = 12.sdp,
                    ), err = error
            )
        }
        Spacer(modifier = Modifier.height(BOTTOM_HEIGHT.sdp))
    }
}


@Composable
fun TextWithIcon(
    onClick: () -> Unit,
    preText: String? = null,
    postText: String? = null,
    icon: Int = R.drawable.ic_edit,
    iconTint: Color = Purple
) {
    val emailWithIcon = buildAnnotatedString {
        preText?.let {
            append(it)
        }
        append(" ")
        postText?.let {
            append(it)
        }
        appendInlineContent("icon")
    }

    val annotatedString = buildAnnotatedString {
        withStyle(normalSpanStyle()) {
            append(stringResource(id = R.string.msg_digit_code_sent))
            append(" ")
        }
        withStyle(clickSpanStyle()) {
            pushStringAnnotation(
                tag = emailWithIcon.toString(), annotation = emailWithIcon.toString()
            )
            append(emailWithIcon)
            pop()
        }
    }

    val map = mapOf("icon" to InlineTextContent(
        Placeholder(
            width = 14.sp,
            height = 14.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
        )
    ) {
        Icon(painterResource(id = icon), // Your icon goes here
            contentDescription = null, tint = iconTint, modifier = Modifier.noRippleClickable {
                onClick()
            })
    })
}


/**
 * A continent version of [BasicText] component to be able to handle click event on the text.
 *
 * This is a shorthand of [BasicText] with [pointerInput] to be able to handle click
 * event easily.
 *
 * @sample androidx.compose.foundation.samples.ClickableText
 *
 * For other gestures, e.g. long press, dragging, follow sample code.
 *
 * @sample androidx.compose.foundation.samples.LongClickableText
 *
 * @see BasicText
 * @see androidx.compose.ui.input.pointer.pointerInput
 * @see androidx.compose.foundation.gestures.detectTapGestures
 *
 * @param text The text to be displayed.
 * @param modifier Modifier to apply to this layout node.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the
 * text will be positioned as if there was unlimited horizontal space. If [softWrap] is false,
 * [overflow] and [TextAlign] may have unexpected effects.
 * @param overflow How visual overflow should be handled.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if
 * necessary. If the text exceeds the given number of lines, it will be truncated according to
 * [overflow] and [softWrap]. If it is not null, then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A
 * [TextLayoutResult] object that callback provides contains paragraph information, size of the
 * text, baselines and other details. The callback can be used to add additional decoration or
 * functionality to the text. For example, to draw selection around the text.
 * @param onClick Callback that is executed when users click the text. This callback is called
 * with clicked character's offset.
 */
@Composable
fun ClickableTextWithInlinedContent(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    inlinedContent: Map<String, InlineTextContent> = mapOf(),
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    BasicText(text = text,
        modifier = modifier.then(pressIndicator),
        inlineContent = inlinedContent,
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        })
}


@Composable
private fun MyUI(placeholder: String = "Enter Your Name") {
    var value by remember {
        mutableStateOf("")
    }

    BasicTextField(
        value = value,
        onValueChange = { newText ->
            value = newText
        },
        textStyle = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        ),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 64.dp) // margin left and right
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color(0xFFAAE9E6),
                        shape = RoundedCornerShape(size = 16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp), // inner padding
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.LightGray,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun HtmlText(html: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> TextView(context) },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            it.textSize = 12f
            it.maxLines = 2
        }
    )
}

@Composable
@Preview
fun previewOnly() {

    Column {
        MyUI()
        MyUI()
        MyUI()
        MyUI()
    }


}