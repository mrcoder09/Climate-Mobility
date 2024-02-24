package com.cityof.glendale.composables.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import com.cityof.glendale.R
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.screens.trips.RouteCircledComposable
import com.cityof.glendale.theme.BG_WINDOW
import com.cityof.glendale.theme.ContainerColor
import com.cityof.glendale.utils.LangHelper
import com.cityof.glendale.utils.capitalizeWords
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
val DropDownStyle = @Composable {
    TextFieldDefaults.colors(
        focusedContainerColor = ContainerColor,
        unfocusedContainerColor = ContainerColor,
        disabledContainerColor = ContainerColor,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )
}

@Composable
@Preview
fun DropDownPreview() {

    AppDropDown(list = listOf("ABC", "ABC", "ABC"), onItemSelected = { index, item ->

    }) {

    }
}


@Composable
fun <T> AppDropDown(
    modifier: Modifier = Modifier.fillMaxWidth(),
    list: List<T>,
    icon: Int? = null,
    error: String = "",
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedValue: String = "The Quick Brown",
    selectedIndex: Int = 0,
    dropDownColor: Color = BG_WINDOW,
    content: @Composable (T) -> Unit,
) {
    Timber.d(selectedValue)
    val expanded = remember { mutableStateOf(false) }
//    val selectedIndex = remember { mutableIntStateOf(0) }
    val size = remember { mutableStateOf(Size.Zero) }


    Column(
        modifier = modifier
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(6.dp))
                .fillMaxWidth()
        ) {
            Image(painter = painterResource(id = R.drawable.ic_sms), contentDescription = null)
            BasicText(text = if (selectedIndex == 0) selectedValue else selectedValue.capitalizeWords(),
                modifier = Modifier
                    .height(TF_HEIGHT.sdp)
                    .background(Color.White)
                    .onGloballyPositioned {
                        size.value = it.size.toSize()
                    }
                    .noRippleClickable {
                        expanded.value = true
                    },
                maxLines = 1,
                style = if (selectedIndex == 0) FloatLabelStyle() else TextInputStyle()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_down_arrow), contentDescription = null
            )
        }


        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp, start = 16.dp
                    ), err = error
            )
        }

//        DropdownMenu(modifier = Modifier
//            .width(with(LocalDensity.current) {
//                size.value.width.toDp()
//            })
//            .background(dropDownColor), expanded = expanded.value, onDismissRequest = {
//            expanded.value = false
//        }) {
//            list.forEachIndexed { index, item ->
//                DropdownMenuItem(onClick = {
//                    expanded.value = false
////                    selectedIndex = index
//                    onItemSelected(index, item)
//                }) {
//                    content(item)
//                }
//            }
//        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(BOTTOM_HEIGHT.sdp)
//        )
    }
}

@Composable
fun LanguageContextDropDown(
    isPopupVisible: Boolean = false,
    onDismiss: (String?) -> Unit,
) {

    val context = LocalContext.current
    val list = LangHelper.appLanguages(context)

    if (isPopupVisible) {

        Popup(alignment = Alignment.TopStart, onDismissRequest = { onDismiss(null) }) {
            Column(
                modifier = Modifier
                    .width(170.sdp)
                    .padding(
                        top = 20.sdp, end = 10.sdp
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 18.sdp
                        ), contentAlignment = Alignment.CenterEnd
                ) {
                    Image(
                        modifier = Modifier.shadow(
                            elevation = 8.sdp
                        ),
                        painter = painterResource(id = R.drawable.ic_arrow_solid_white),
                        contentDescription = "image description",
                        contentScale = ContentScale.None
                    )
                }
                Card(
                    elevation = CardDefaults.cardElevation(8.sdp),
                    colors = CardDefaults.cardColors(Color.White)
                ) {
                    list.forEach { item ->
                        Row(
                            modifier = Modifier
                                .height(32.sdp)
                                .fillMaxWidth()
                                .padding(horizontal = 8.sdp)
                                .noRippleClickable {
                                    onDismiss(item.locale)
//                                    LangHelper.setLocale(
//                                        context, item.locale
//                                    )
//                                    (context as Activity).recreate()
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.nativeName, style = baseStyle().copy(
                                    fontSize = 14.ssp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                            RadioButton(selected = item.isSelected, onClick = {
                                onDismiss(item.locale)
//                                LangHelper.setLocale(context, item.locale)
//                                (context as Activity).recreate()
                            })
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun <T> AppDropDown2(
    modifier: Modifier = Modifier.fillMaxWidth(),
    list: List<T>,
    leadingIcon: Int? = null,
    error: String = "",
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedValue: String = "",
    selectedIndex: Int = 0,
    dropDownColor: Color = BG_WINDOW,
    content: @Composable (T) -> Unit,
) {
    Timber.d(selectedValue)
    val expanded = remember { mutableStateOf(false) }
//    val selectedIndex = remember { mutableIntStateOf(0) }
    val size = remember { mutableStateOf(Size.Zero) }


    Column(modifier = modifier) {

        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(value = if (selectedIndex == 0) selectedValue else selectedValue.capitalizeWords(),
                onValueChange = {},
                readOnly = true,
                leadingIcon = if (leadingIcon != null) {
                    {
                        Icon(
                            painter = painterResource(id = leadingIcon),
                            tint = MaterialTheme.colorScheme.outline,
                            contentDescription = "leading icon"
                        )
                    }
                } else null,
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TOP_HEIGHT.sdp)
                    .height(TF_HEIGHT.sdp)
                    .onGloballyPositioned {
                        size.value = it.size.toSize()
                    },
                shape = RoundedCornerShape(6.dp),
                singleLine = true,
                colors = TextFieldColor(),
                textStyle = if (selectedIndex == 0) FloatLabelStyle() else TextInputStyle()
            )

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp)
                .noRippleClickable {
                    expanded.value = true
                })
        }

        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp, start = 16.dp
                    ), err = error
            )
        }
        DropdownMenu(modifier = Modifier
            .width(with(LocalDensity.current) {
                size.value.width.toDp()
            })
            .background(dropDownColor), expanded = expanded.value, onDismissRequest = {
            expanded.value = false
        }) {
            list.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    expanded.value = false
//                    selectedIndex = index
                    onItemSelected(index, item)
                }) {
                    content(item)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(BOTTOM_HEIGHT.sdp)
        )
    }
}


@Composable
fun <T> FeedbackDropDown(
    modifier: Modifier = Modifier.fillMaxWidth(),
    list: List<T>,
    error: String = "",
    selectedValue: String = "",
    selectedIndex: Int = 0,
    dropDownColor: Color = BG_WINDOW,
    leadingIcon: Int? = null,
    routeId: String? = "",
    color: Color?,
    onItemSelected: (index: Int, item: T) -> Unit,
    content: @Composable (T) -> Unit,
) {
    Timber.d(selectedValue)
    val expanded = remember { mutableStateOf(false) }
//    val selectedIndex = remember { mutableIntStateOf(0) }
    val size = remember { mutableStateOf(Size.Zero) }


    Column(modifier = modifier) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Row {


                TextField(value = if (selectedIndex == 0) selectedValue else selectedValue.capitalizeWords(),
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = if (leadingIcon != null) {
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = leadingIcon),
                                    tint = MaterialTheme.colorScheme.outline,
                                    contentDescription = "leading icon"
                                )
                                Spacer(modifier = Modifier.width(4.sdp))
                                if (routeId.isNullOrEmpty().not()) RouteCircledComposable(
                                    item = routeId,
                                    color = color,size = 24,
                                    fontSize = 10
                                )
                            }

                        }
                    } else null,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_down_arrow),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = TOP_HEIGHT.sdp)
                        .height(TF_HEIGHT.sdp)
                        .onGloballyPositioned {
                            size.value = it.size.toSize()
                        },
                    shape = RoundedCornerShape(6.dp),
                    singleLine = true,
                    colors = TextFieldColor(),
                    textStyle = if (selectedIndex == 0) FloatLabelStyle() else TextInputStyle()
                )
            }

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = TOP_HEIGHT.sdp)
                .height(TF_HEIGHT.sdp)
                .noRippleClickable {
                    expanded.value = true
                })
        }

        if (error.isNotEmpty()) {
            ErrorText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp, start = 16.dp
                    ), err = error
            )
        }
        DropdownMenu(modifier = Modifier
            .width(with(LocalDensity.current) {
                size.value.width.toDp()
            })
            .background(dropDownColor), expanded = expanded.value, onDismissRequest = {
            expanded.value = false
        }) {
            list.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    expanded.value = false
//                    selectedIndex = index
                    onItemSelected(index, item)
                }) {
                    content(item)
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(BOTTOM_HEIGHT.sdp)
        )
    }
}


@Composable
fun DropDownText(text: String, style: TextStyle = TextInputStyle()) {
    Text(
        text = text, style = style
    )
}


@Composable
fun DropDownTextWithIcons(
    text: String,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    style: TextStyle = TextInputStyle()
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (leadingIcon != null) Image(
            painter = painterResource(id = leadingIcon), contentDescription = null
        )
        Text(
            text = text, style = style
        )
        if (trailingIcon != null) Image(
            painter = painterResource(id = trailingIcon), contentDescription = null
        )
    }

}




