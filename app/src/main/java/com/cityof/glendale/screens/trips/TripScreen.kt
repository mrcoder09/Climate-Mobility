package com.cityof.glendale.screens.trips

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.BUTTON_HEIGHT
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.navigation.removeBackStackEntries
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.toComposeColor
import com.cityof.glendale.theme.ERR_RED
import com.cityof.glendale.theme.FF69A251
import com.cityof.glendale.theme.FFEFB30F
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber


const val HORIZONTAL_SPACING = 24
const val CARD_ELEVATION = 1

@Composable
@Preview(showSystemUi = true)
fun TripScreenPreview() {
    TripScreen(
        viewModel = TripViewModel(
            appRepository = AppRepository(MockApiService())
        )
    )
}

@Composable
fun TripScreen(
    navHostController: NavHostController? = null, viewModel: TripViewModel = hiltViewModel()
) {


    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            TripContract.NavAction.NavFare -> navHostController?.navigate(Routes.FareInfo.name)
            TripContract.NavAction.NavRouteList -> navHostController?.navigate(Routes.RouteList.name)
            null -> {}
            is TripContract.NavAction.NavTripPlan -> {
                (navigation as TripContract.NavAction.NavTripPlan).trip?.let {
                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.DATA_BUNDLE, it
                    )
                }
                navHostController?.navigate(Routes.TripPlan.name)
            }

            TripContract.NavAction.NavSavedTrips -> navHostController?.navigate(Routes.SaveTrips.name)
        }
    })

    ProgressDialogApp(
        state.isLoading
    )
    DoUnauthorization(
        state.isAuthErr
    )
    ToastApp(state.toastMsg)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9),
    ) {

        AppBarWithGlobe(
            modifier = Modifier.padding(
                start = 12.sdp,
//                end = 74.sdp
            )
        )


        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                TripeHeader(
                    title = UIStr.ResStr(R.string.trips_planning),
                    buttonText = UIStr.ResStr(R.string.plan_a_trip)
                ) {
                    navHostController?.removeBackStackEntries()
                    navHostController?.navigate(Routes.TripPlan.name)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.sdp))
                SectionHeader(
                    title = stringResource(id = R.string.saved_trips),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 24.sdp
                        )
                ) {
                    viewModel.dispatch(TripContract.Intent.NavSavedTrips)
                }
            }

            items(state.saveTrips.size) {
                val item = state.saveTrips[it]
                SavedTripsComposable(item, onEdit = {
                    viewModel.dispatch(TripContract.Intent.EditTrip(item))
                }, onDelete = {
                    viewModel.dispatch(TripContract.Intent.DeleteTrip(item.id ?: "", it))
                })
            }

            item {
                RouteAndMaps(
                    R.string.routes_maps,
                    {
                        RouteListingComposable(state.routes)
                    },
                ) {
                    viewModel.dispatch(TripContract.Intent.NavRouteList)
                }
            }

            item {

                TripFooter(
                    title = UIStr.ResStr(R.string.fare_info)
                ) {
                    viewModel.dispatch(TripContract.Intent.NavFare)
                }
            }

        }
    }
}


@Composable
fun TripeHeader(
    color: Color = Color.White,
    title: UIStr = UIStr.Str(""),
    buttonText: UIStr = UIStr.Str(""),
    onClick: () -> Unit
) {


    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(horizontal = 22.sdp)
    ) {
        Spacer(modifier = Modifier.height(8.sdp))
        TitleComposable(title = title.toStr())
        Spacer(modifier = Modifier.height(6.sdp))
        AppButton(title = buttonText.toStr(), onClick = onClick)
        Spacer(modifier = Modifier.height(22.sdp))
    }
}

@Composable
fun TripFooter(
    title: UIStr = UIStr.Str(""), onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.sdp)
    ) {
        Spacer(modifier = Modifier.height(8.sdp))
        TitleComposable(title = title.toStr())
        Spacer(modifier = Modifier.height(6.sdp))
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .border(
                    width = 1.dp, color = Purple, shape = RoundedCornerShape(size = 8.dp)
                )
                .fillMaxWidth()
                .height(BUTTON_HEIGHT.sdp)
                .background(color = Color.White, shape = RoundedCornerShape(size = 8.dp))
                .noRippleClickable(
                    onClick = onClick

                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.beeling_logo_new),
                contentDescription = null,
                modifier = Modifier.height(42.sdp)
            )
        }
        Spacer(modifier = Modifier.height(22.sdp))
    }
}

//@Composable
//fun SearchBar(onClick: () -> Unit) {
//    BasicTextField(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(
//                horizontal = 24.sdp
//            ),
//        value = "",
//        onValueChange = {},
//        singleLine = true,
//        enabled = true,
//    ) {
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(TF_HEIGHT.sdp)
//                .background(
//                    Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 12.dp)
//                )
//                .padding(
//                    start = 12.sdp
//                ),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                text = stringResource(R.string.search_destination), style = baseStyle().copy(
//
//                )
//            )
//            Spacer(modifier = Modifier.weight(1f))
//            Image(
//                painter = painterResource(id = R.drawable.ic_search_trip),
//                contentDescription = null,
//                modifier = Modifier.noRippleClickable(onClick)
//            )
//        }
//    }
//}


@Composable
fun SectionHeader(
    modifier: Modifier = Modifier.fillMaxWidth(),
    title: String = "Saved Trips",
    isBeelineLogo: Boolean = false,
    onSeeMore: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleComposable(title)
        if (isBeelineLogo) Image(
            painter = painterResource(id = R.drawable.beeling_logo_new),
            contentDescription = null,
            modifier = Modifier.size(width = 42.sdp, height = 24.sdp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.see_more), style = baseStyle().copy(
                color = Purple
            ), modifier = Modifier.noRippleClickable(onClick = onSeeMore)
        )
    }
}

@Composable
fun TitleComposable(title: String = "") {
    Text(
        text = title, style = baseStyle().copy(fontSize = 16.ssp)
    )
}


@Composable
fun SavedTripsComposable(
    item: SavedTrip = SavedTrip(), onEdit: () -> Unit = {}, onDelete: () -> Unit = {}
) {

    var isPopupVisible by remember {
        mutableStateOf(false)
    }
    var itemHeight by remember {
        mutableStateOf(0)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    val density = LocalDensity.current

    if (isPopupVisible) {

//        OptionsForDot(offset = DpOffset(0.dp, 0.dp))
//        Option3Dot(offset, itemHeight, { isPopupVisible = false }) { option, index ->
//
//        }
    }

    Card(
        modifier = Modifier
            .padding(
                start = HORIZONTAL_SPACING.sdp, end = HORIZONTAL_SPACING.sdp, top = 6.sdp
            )
            .onSizeChanged {
                itemHeight = it.height //with(density) { it.height.dp }
            }, elevation = CardDefaults.cardElevation(CARD_ELEVATION.sdp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(id = R.drawable.ic_loc_purple),
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        horizontal = 8.sdp
                    )
                    .noRippleClickable {
                        Timber.d("Item Clicked")
                    })
            Column {
                Spacer(modifier = Modifier.height(10.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = item.label ?: "",
                        style = baseStyle().copy(fontSize = 14.ssp),
                        modifier = Modifier.padding(bottom = 4.sdp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        Image(painter = painterResource(id = R.drawable.ic_three_dots),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.sdp)
                                .padding(end = 8.sdp)
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        Timber.d("${it.x} ${it.y}")
                                        offset = it
                                        Timber.d("$isPopupVisible")
                                        isPopupVisible = true
                                    }
                                })
                        if (isPopupVisible) OptionsForDot(onDismiss = {
                            isPopupVisible = false
                        }) { option, index ->
                            when (index) {
                                0 -> {
                                    onEdit()
                                }

                                1 -> {
                                    onDelete()
                                }
                            }
                        }
                    }
                }

                Text(
                    text = item.fromAddress ?: "", style = baseStyle().copy(
                        fontWeight = FontWeight.Normal,
                    ), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(
                        end = 28.sdp
                    )
                )
                Spacer(modifier = Modifier.height(10.sdp))
            }
        }
    }

}

data class Option(
    val title: String, @DrawableRes val icon: Int
)


@Composable
fun RouteAndMaps(
    @StringRes title: Int, content: @Composable () -> Unit, onSeeMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = HORIZONTAL_SPACING.sdp
            )
    ) {
        Spacer(modifier = Modifier.height(24.sdp))
        SectionHeader(
            title = stringResource(id = title), isBeelineLogo = true, onSeeMore = onSeeMore
        )
        Spacer(modifier = Modifier.height(6.sdp))
        content()
        Spacer(modifier = Modifier.height(8.sdp))
    }
}


@Composable
fun RouteListingComposable(routes: List<UmoRoute>) {

    //#ED1B2D
    //    #EFB30F
    //    #69A251
    val list = listOf(
        ERR_RED, FFEFB30F, FF69A251
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), content = {
        items(routes.size) {
            val item = routes[it]
            val color = list[it]
            RouteCard(item, color)
        }
    })
}


@Composable
@Preview
fun RouteCard(item: UmoRoute = UmoRoute(), color: Color = Color.Black) {
    Card(
        modifier = Modifier
            .size(144.sdp, 160.sdp)
            .padding(
                start = 4.sdp, end = 4.sdp
            ), elevation = CardDefaults.cardElevation(CARD_ELEVATION.sdp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.sdp))
            RouteCircledComposable(item.id, item.toComposeColor())
            Text(
                text = stringResource(R.string.glendale), modifier = Modifier
                    .padding(
                        top = 12.sdp
                    )
                    .fillMaxWidth(), style = baseStyle().copy(
                    fontSize = 14.ssp, textAlign = TextAlign.Center
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.title ?: "",
                modifier = Modifier
                    .padding(
                        top = 4.sdp, start = 8.sdp, end = 8.sdp
                    )
                    .fillMaxWidth(),
                style = baseStyle().copy(
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Normal
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(14.sdp))
        }
    }
}

