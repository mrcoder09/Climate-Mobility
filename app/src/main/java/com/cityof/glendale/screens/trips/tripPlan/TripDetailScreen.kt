package com.cityof.glendale.screens.trips.tripPlan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.CircleApp
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppOutlinedButton
import com.cityof.glendale.composables.components.BUTTON_HEIGHT
import com.cityof.glendale.composables.components.CongratulationDialog
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.HtmlText
import com.cityof.glendale.composables.components.ModifierButton
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.TextFieldColor
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.data.enums.TravelMode
import com.cityof.glendale.data.enums.getCircledIcon
import com.cityof.glendale.data.enums.isBeeline
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.MockGoogleApiRepo
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.googleresponses.Step
import com.cityof.glendale.network.googleresponses.getArrivalTime
import com.cityof.glendale.network.googleresponses.getDepartureTime
import com.cityof.glendale.network.googleresponses.getDistanceFormatted
import com.cityof.glendale.network.googleresponses.getDuration
import com.cityof.glendale.network.googleresponses.getLegStart
import com.cityof.glendale.network.googleresponses.getStepCount
import com.cityof.glendale.network.googleresponses.getStopCounts
import com.cityof.glendale.network.googleresponses.getTravelMode
import com.cityof.glendale.theme.FF69A251
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFEFB30F
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.MockLocationService
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber


@Composable
fun TripLineSpacer(space: Int = 10) {
    Spacer(modifier = Modifier.width(space.sdp))
}

@Composable
@Preview
fun TripDetailScreen() {

    TripDetailScreen(
        viewModel = TripPlanViewModel(
            googleApiRepository = GoogleApiRepository(MockGoogleApiRepo()),
            appRepository = AppRepository(MockApiService()),
            locationService = MockLocationService()
        )
    )
}

@Composable
fun TripDetailScreen(
    navHostController: NavHostController? = null, viewModel: TripPlanViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    var showLabelDialog by remember {
        mutableStateOf(false)
    }
    var showCongratulations by remember {
        mutableStateOf(false)
    }

    AddLabelComposable(showLabelDialog, state.label, onValueChange = {
        viewModel.dispatch(TripPlanContract.Intent.LabelChanged(it))
    }, onSubmit = {
//        showCongratulations = true
        showLabelDialog = false
        if (state.isTripEdit()) {
            viewModel.dispatch(TripPlanContract.Intent.EditTrip(it))
        } else {
            if (state.label.isEmpty()) {
                viewModel.dispatch(TripPlanContract.Intent.ShowToast(UIStr.ResStr(R.string.msg_add_label)))
                return@AddLabelComposable
            }
            viewModel.dispatch(TripPlanContract.Intent.SaveTrip(it))
        }
    }, onCancel = {
        showLabelDialog = false
    })

    CongratulationDialog(showDialog = state.showCongratulations,
        message = UIStr.ResStr(R.string.you_have_successfully_saved_your_trip),
        onDismiss = {
            viewModel.dispatch(TripPlanContract.Intent.ShowCongrats(false))
            navHostController?.popBackStack(Routes.Dashboard.name, false)
        })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            TripPlanContract.NavAction.NavLocationSearch -> {}
            is TripPlanContract.NavAction.NavTripDetails -> {}
            is TripPlanContract.NavAction.NavRouteTracking -> {
                val trip = (navigation as TripPlanContract.NavAction.NavRouteTracking).trip
                navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, trip
                )
                navHostController?.navigate(Routes.RouteTracking.name)
            }

            null -> {}
        }
    })


    ProgressDialogApp(state.isLoading)
    ToastApp(state.toastMsg)
    DoUnauthorization(state.isAuthErr)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            AppBarWithBack(
                color = Purple,
                title = stringResource(id = R.string.trip_plan),
                arrowTint = Color.White,
                titleColor = Color.White,
            ) {
                navHostController?.popBackStack()
            }
            TripDetailHeader(
                state.originLoc, state.destinationLoc, state.route, state.selectedTravelMode.mode
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.sdp
                    )
            ) {

                item {
                    Spacer(modifier = Modifier.height(14.sdp))
                    TripOriginComposable(
                        item = state.originLoc, time = state.route.getDepartureTime() ?: ""
                    )
                }

                item {

//                    if (AppConstants.travelMode == TravelMode.Bus)
                    if (state.selectedTravelMode.isBeeline()) {
                        BeelineDetailComposable(route = state.route)
                    } else WalkingBicycleComposable(
                        route = state.route,
                        travelMode = state.selectedTravelMode.mode
                    )
                }

                item {
                    TripEndComposable2(
                        state.destinationLoc, time = state.route.getArrivalTime() ?: ""
                    )
                    Spacer(modifier = Modifier.height(12.sdp))
                }
            }
        }

        Box {

            if (state.isTripEdit()) AppButton(
                title = stringResource(R.string.update_trip), modifier = ModifierButton().padding(
                    start = 22.sdp, end = 10.sdp
                )
            ) {
                showLabelDialog = true
            } else Row {
                AppButton(
                    title = stringResource(R.string.start_trip),
                    modifier = ModifierButton()
                        .padding(start = 22.sdp, end = 10.sdp)
                        .weight(1f),
                    colors = if (state.canStartTrip()) ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = Purple
                    ) else ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = FF777C80
                    )
                ) {

                    Timber.d("Can Start Trip: ${state.canStartTrip()}")

                    if (state.canStartTrip()) {
                        viewModel.dispatch(
                            TripPlanContract.Intent.SaveTrip(
                                "start_trip", false
                            )
                        )
                    } else {
                        viewModel.dispatch(TripPlanContract.Intent.ShowToast(UIStr.ResStr(R.string.trip_cannot_be_start_before_selected_date)))
                    }

//                    context.startActivity(
//                        Intent(
//                        context, RouteTrackingActivity::class.java
//                    ).apply {
//                        putExtra(
//                            AppConstants.DATA_BUNDLE, state.route
//                        )
//                        putExtra(AppConstants.ORIGIN_LOC, state.originLoc)
//                        putExtra(AppConstants.DESTINATION_LOC, state.destinationLoc)
//
//                    })


//                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
//                        AppConstants.DATA_BUNDLE, state.route
//                    )
//                    navHostController?.navigate(Routes.RouteTracking.name)

                }
                Column(
                    modifier = Modifier
                        .size(
                            width = 65.sdp, height = BUTTON_HEIGHT.sdp
                        )
                        .border(width = 1.dp, color = Purple, shape = RoundedCornerShape(8.dp))
                        .noRippleClickable {
                            showLabelDialog = true
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.save), style = baseStyle2().copy(
                            color = Purple, fontSize = 12.ssp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.sdp))

                }
                Spacer(modifier = Modifier.width(16.sdp))
            }


        }


        Spacer(modifier = Modifier.height(12.sdp))
    }
}

@Composable
fun BeelineDetailComposable(route: Route?) {

    Column {
        route?.legs?.getOrNull(0)?.steps?.forEachIndexed { index, step ->
            when (step.getTravelMode()) {
                TravelMode.Bus -> {
                    BeelineBusComposable(step)
                }

                TravelMode.Walking, TravelMode.Bicycling -> {
                    WalkingBicycle4Beeline(step = step)
                }
            }
        }
    }

}

@Composable
fun BeelineBusComposable(step: Step) {
    Column {

        BusStoppageHeader(
            step.transitDetail?.departureStop?.name ?: "",
            step.transitDetail?.departureTime?.text ?: ""

        )
        BusGroupComposable(
            number = step.transitDetail?.line?.shortName ?: "",
            address = step.transitDetail?.headsign ?: "",
            duration = step.duration?.text ?: ""
        )
        BusStoppageHeader(
            step.transitDetail?.arrivalStop?.name ?: "", step.transitDetail?.arrivalTime?.text ?: ""
        )

    }
}

@Composable
@Preview
fun BusStoppageHeader(
    name: String = "Title", time: String = "Time"
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                50.sdp
            )
    ) {
        LineWithCircleInCenter(
            minusBy = 20
        )
        TripLineSpacer()
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HorizontalDivider()
            Spacer(modifier = Modifier.weight(1f))
            TripTimeComposable(address = name, time = time)
//            Row(
//                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = name, style = baseStyle2().copy(
//                        fontSize = 14.ssp, fontWeight = FontWeight.Bold
//                    ), maxLines = 1
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                Text(
//                    text = time, style = baseStyle2().copy(
//                        fontSize = 12.ssp, fontWeight = FontWeight.Normal, color = FF69A251
//                    ), maxLines = 1
//                )
//            }
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
        }

    }
}

@Composable
fun BusGroupComposable(
    number: String = "02", address: String = "Text Here", duration: String
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            LineWithImageInCenter(
                icon = R.drawable.ic_bus_purple, height = 30
            )
            TripLineSpacer()
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Purple, shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Text(
                            text = number, style = baseStyle().copy(
                                color = Color.White, fontSize = 7.ssp
                            )
                        )
                    }
                    Text(
                        text = address,
                        style = baseStyle2().copy(
                            fontSize = 14.ssp, fontWeight = FontWeight.Bold, color = Purple
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(horizontal = 4.sdp, vertical = 1.sdp)
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.sdp))
                }
                Spacer(modifier = Modifier.height(3.sdp))
                Text(
                    text = duration, maxLines = 1, modifier = Modifier.padding(start = 18.sdp)
                )
            }
        }
    }
}


@Composable
fun WalkingBicycle4Beeline(step: Step?) {

    var isExpand by remember {
        mutableStateOf(false)
    }
    var rotationDegree by remember {
        mutableFloatStateOf(0f)
    }

    Column {
        WayStopHeader(
            "${step?.getStepCount() ?: 0}",
            step?.htmlInstructions ?: "",
            step?.duration?.text ?: "",
            rotationDegree,
            step?.getTravelMode()
        ) {
            isExpand = isExpand.not()
            rotationDegree = if (rotationDegree == 0f) 180f else 0f
        }
        if (isExpand) {
            step?.steps?.forEach {
                WayStep(step = step)
            }
        }
    }

}


@Composable
fun WalkingBicycleComposable(route: Route?, travelMode: TravelMode) {

    var isExpand by remember {
        mutableStateOf(false)
    }
    var rotationDegree by remember {
        mutableFloatStateOf(0f)
    }

    Column {
        WayStopHeader(
            "${route?.getStopCounts() ?: 0}",
            route?.getLegStart() ?: "",
            route?.getDuration() ?: "",
            rotationDegree,
            travelMode = travelMode
        ) {
            isExpand = isExpand.not()
            rotationDegree = if (rotationDegree == 0f) 180f else 0f
        }
        if (isExpand) {
            route?.legs?.let {
                val leg = it[0]
                leg.steps?.forEachIndexed { index, step ->
                    WayStep(step = step)
                }
            }
        }
    }

}

@Composable
fun WayStopHeader(
    count: String = "02",
    address: String = "Text Here",
    duration: String,
    rotation: Float = 0f,
    travelMode: TravelMode?,
    onExpand: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            LineWithImageInCenter(
                icon = if (travelMode == TravelMode.Bicycling) R.drawable.ic_bicycle
                else R.drawable.ic_walking
            )
            TripLineSpacer()
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.sdp)
                        .noRippleClickable(onExpand), verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircleApp(
                            size = 14, color = FFEFB30F
                        )
                        Text(
                            text = count, style = baseStyle().copy(
                                color = Color.White, fontSize = 6.ssp
                            )
                        )
                    }
                    Text(
                        text = address,
                        style = baseStyle2().copy(
                            fontSize = 14.ssp, fontWeight = FontWeight.Bold, color = Purple
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .padding(horizontal = 4.sdp, vertical = 1.sdp)
                            .weight(1f),
                        overflow = TextOverflow.Ellipsis
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_down_arrow),
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                    Spacer(modifier = Modifier.width(8.sdp))

                }
                Spacer(modifier = Modifier.height(3.sdp))
                Text(
                    text = "$duration (${count} Stops)",
                    maxLines = 1,
                    modifier = Modifier.padding(start = 18.sdp)
                )
            }
        }
    }
}

@Composable
fun WayStep(step: Step) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.sdp)
    ) {
        LineWithCircleInCenter()
        TripLineSpacer()
        Column {
            Spacer(modifier = Modifier.height(8.sdp))
            HtmlText(
                html = step.htmlInstructions ?: ""
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = step.distance?.text ?: "", maxLines = 1)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${step.duration?.text}", style = baseStyle2().copy(
                        fontSize = 10.ssp, color = FF69A251
                    )
                )
                Spacer(modifier = Modifier.width(8.sdp))
            }
            Spacer(modifier = Modifier.height(8.sdp))
            HorizontalDivider()
        }

    }
}

@Composable
fun TripDetailHeader(
    origin: LocationSearched?, destination: LocationSearched?, route: Route?, travelMode: TravelMode
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple)
            .padding(
                horizontal = 14.sdp
            ),
    ) {
        Spacer(modifier = Modifier.height(24.sdp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${destination?.address}", style = baseStyle2().copy(
                        color = Color.White, fontWeight = FontWeight.W600
                    ), maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.sdp))
                Row {
                    Text(
                        text = "From", style = baseStyle2().copy(
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(2.sdp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_direct_up),
                        contentDescription = null,
                        modifier = Modifier
                            .width(18.dp)
                            .height(18.dp)
                            .background(
                                color = Color(0xFF7A64A5), shape = RoundedCornerShape(size = 3.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(2.sdp))
                    Text(
                        text = "${origin?.address}", style = baseStyle2().copy(
                            color = Color.White
                        ), maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.sdp))
                Text(
                    text = "${route?.getDuration()} (${route?.getDistanceFormatted()})",
                    style = baseStyle2().copy(
                        color = Color.White
                    )
                )
            }
//            Spacer(modifier = Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = travelMode.getCircledIcon()),
                    contentDescription = null
                )
//                Box(
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircleApp(
//                        size = 14, color = FFEFB30F
//                    )
//                    Text(
//                        text = "${route?.getStopCounts() ?: ""}", style = baseStyle().copy(
//                            color = Color.White, fontSize = 6.ssp
//                        )
//                    )
//                }
            }
        }

        Spacer(modifier = Modifier.height(24.sdp))
    }
}


@Composable
@Preview
fun CircleWithLine(
    circleSize: Int = 16, height: Int = 40, color: Color = Purple
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
    ) {
        Box(
            modifier = Modifier
                .size(circleSize.dp)
                .border(2.dp, color, CircleShape)
                .padding(1.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        VerticalDivider(
            modifier = Modifier.height(height.sdp), color = Purple, thickness = 2.dp
        )
    }
}


@Composable
@Preview
fun LineWithImageInCenter(
    circleSize: Int = 14, height: Int = 40, color: Color = Purple, icon: Int = R.drawable.ic_walking
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
    ) {
        VerticalDivider(
            modifier = Modifier.height((10).sdp), color = Purple, thickness = 2.dp
        )
        Spacer(modifier = Modifier.height(2.sdp))
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(22.sdp)
        )
        Spacer(modifier = Modifier.height(2.sdp))
        VerticalDivider(
            modifier = Modifier.height((30).sdp), color = Purple, thickness = 2.dp
        )
    }
}

@Composable
@Preview
fun LineWithCircleInCenter(
    circleSize: Int = 14, height: Int = 40, minusBy: Int = 10, color: Color = Purple
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
    ) {
        VerticalDivider(
            modifier = Modifier.height((minusBy).sdp), color = Purple, thickness = 2.dp
        )
        Box(
            modifier = Modifier
                .size(circleSize.dp)
                .border(2.dp, color, CircleShape)
                .padding(1.dp)
                .clip(CircleShape)
                .background(Color.White)

        )
        VerticalDivider(
            modifier = Modifier.height((height - minusBy).sdp), color = Purple, thickness = 2.dp
        )
    }
}

@Composable
@Preview
fun VerticalLineOnly(
    circleSize: Int = 14, height: Int = 40, color: Color = Purple
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
    ) {
        VerticalDivider(
            modifier = Modifier.height((height).sdp), color = color, thickness = 2.dp
        )
    }
}

@Composable
@Preview
fun CircleWithLineOppoSite(
    circleSize: Int = 14, height: Int = 40
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
    ) {

        VerticalDivider(
            modifier = Modifier.height(height.sdp), color = Purple, thickness = 2.dp
        )
        Box(
            modifier = Modifier
                .size(circleSize.dp)
                .border(2.dp, Purple, CircleShape)
                .padding(1.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
@Preview
fun TripOriginComposable(
    item: LocationSearched? = LocationSearched(), time: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                40.sdp
            )
    ) {
        CircleWithLine()
        TripLineSpacer()
        Column {
            TripTimeComposable(address = item?.name ?: "", time = time)
            Text(
                text = item?.address ?: "", maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.sdp))
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
        }

    }
}

@Composable
fun TripEndComposable2(
    item: LocationSearched?, time: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(
                40.sdp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(20.sdp)
        ) {
            VerticalDivider(
                modifier = Modifier.height(4.sdp), color = Purple, thickness = 2.dp
            )
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .border(2.dp, Purple, CircleShape)
                    .padding(1.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
        TripLineSpacer()
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = item?.name ?: "", style = baseStyle2().copy(
//                        fontSize = 14.ssp, fontWeight = FontWeight.Bold
//                    ), maxLines = 1
//                )
//                Spacer(modifier = Modifier.weight(1f))
//                Text(
//                    text = time, style = baseStyle2().copy(
//                        fontSize = 12.ssp, fontWeight = FontWeight.Normal, color = FF69A251
//                    ), maxLines = 1
//                )
//            }
            Spacer(modifier = Modifier.height(4.sdp))
            TripTimeComposable(address = item?.name ?: "", time = time)
            Text(text = item?.address ?: "", maxLines = 1)
        }

    }
}

@Composable
fun AddLabelComposable(
    showDialog: Boolean = false,
    label: String = "",
    onValueChange: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit
) {
    var selectedindex by remember { mutableIntStateOf(0) }
//    var label by remember { mutableStateOf("") }


    if (showDialog) DialogApp {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.sdp, vertical = 22.sdp
                )
        ) {
            Text(
                text = stringResource(R.string.add_label), style = baseStyle2().copy(
                    fontWeight = FontWeight.Bold, fontSize = 16.ssp
                )
            )
            Spacer(modifier = Modifier.height(8.sdp))
//            AppDropDown2(list = listOf("Home", "School", "Other"),
//                selectedValue = selectedValue,
//                selectedIndex = selectedindex,
//                onItemSelected = { index, item ->
//                    selectedValue = item
//                    selectedindex = index
//                }) {
//                DropDownText(text = it)
//            }

            TextFieldLabel(
                title = label, hint = stringResource(R.string.msg_enter_your_label), onValueChange
            )

            Spacer(modifier = Modifier.height(16.sdp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                AppButton(
                    title = stringResource(id = R.string.submit),
                    modifier = ModifierButton().weight(1f),
                    onClick = {
                        onSubmit(label)
                    })
                Spacer(modifier = Modifier.width(12.sdp))
                AppOutlinedButton(
                    title = stringResource(id = R.string.cancel),
                    modifier = ModifierButton().weight(1f),
                    onClick = onCancel
                )
                Spacer(modifier = Modifier.width(50.sdp))
            }

        }
    }

}


@Composable
@Preview
fun TripTimeComposable(
    address: String = "The quick brown fox jumps over a lazy dog.", time: String = "Time here"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = address, style = baseStyle2().copy(
                fontSize = 14.ssp, fontWeight = FontWeight.Bold
            ), maxLines = 1, modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(20.sdp))
        Text(
            text = time, style = baseStyle2().copy(
                fontSize = 12.ssp, fontWeight = FontWeight.Normal, color = FF69A251
            ), maxLines = 1
        )
    }
}


@Composable
@Preview
fun TextFieldLabel(
    title: String = "", hint: String = "", onValueChange: (String) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(
                all = 4.sdp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = title, onValueChange = onValueChange, modifier = Modifier.background(
                color = Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 12.dp)
            ), placeholder = {
                Text(
                    text = stringResource(R.string.add_your_label), style = baseStyle2().copy(
                        color = FF777C80
                    ), modifier = Modifier
                )
            }, maxLines = 1, colors = TextFieldColor(), textStyle = baseStyle2().copy(
                fontWeight = FontWeight.Normal, fontSize = 14.ssp
            )
        )

    }

//    BasicTextField(
//        modifier = Modifier
//            .fillMaxWidth(),
//        value = title,
//        onValueChange = onValueChange,
//        singleLine = true,
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(40.sdp)
//                .background(
//                    FFF3F3F3, shape = RoundedCornerShape(size = 4.sdp)
//                )
//                .padding(
//                    start = 12.sdp
//                ),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//
//            Text(
//                text = if (title.isEmpty()) hint else title, style = baseStyle2().copy(
//                    color = Color.Gray, fontSize = 12.ssp
//                ), modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth(), maxLines = 1
//            )
//        }
//    }
}

