package com.cityof.glendale.screens.trips.tripPlan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.CircleApp
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.AppOutlinedButton
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.NativeDatePicker
import com.cityof.glendale.composables.components.NativeTimePicker
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.StyleButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.VerticalDottedLine
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.cornerShape
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.data.enums.TravelModeData
import com.cityof.glendale.data.enums.isBeeline
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.MockGoogleApiRepo
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.googleresponses.getDistanceFormatted
import com.cityof.glendale.network.googleresponses.getDuration
import com.cityof.glendale.network.googleresponses.getFormattedArrivalTime
import com.cityof.glendale.network.googleresponses.getFormattedDepartureTime
import com.cityof.glendale.network.googleresponses.getStopCounts
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.screens.trips.tripPlan.TripPlanContract.Intent
import com.cityof.glendale.theme.FF252826
import com.cityof.glendale.theme.FF333333
import com.cityof.glendale.theme.FF69A251
import com.cityof.glendale.theme.FFDADADA
import com.cityof.glendale.theme.FFEFB30F
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.DateFormats.DATE_FORMAT
import com.cityof.glendale.utils.DateFormats.TIME_FORMAT_2
import com.cityof.glendale.utils.MockLocationService
import com.cityof.glendale.utils.hasLocationPermission
import com.cityof.glendale.utils.xtFormat
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


const val HORIZONTAL_SPACING = 12
const val CORNER_RADIUS = 8
val locationPermissions = arrayOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
)

@Composable
@Preview
fun TripPlanPreview() {
    TripPlanScreen(
        viewModel = TripPlanViewModel(
            googleApiRepository = GoogleApiRepository(MockGoogleApiRepo()),
            appRepository = AppRepository(MockApiService()),
            locationService = MockLocationService()
        )
    )
}

@Composable
fun TripPlanScreen(
    navHostController: NavHostController? = null, viewModel: TripPlanViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        if (state.isEdit.not()) {
            navHostController?.previousBackStackEntry?.savedStateHandle?.get<SavedTrip>(
                AppConstants.DATA_BUNDLE
            )?.let {
                viewModel.handleTripEdit(it)
            }
        }
    })


    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is TripPlanContract.NavAction.NavLocationSearch -> {
                navHostController?.navigate(Routes.LocationSearch.name)
            }

            null -> {}
            is TripPlanContract.NavAction.NavTripDetails -> {
//                AppConstants.travelMode = state.selectedTravelMode.mode
                navHostController?.navigate(Routes.TripDetail.name)
            }

            is TripPlanContract.NavAction.NavRouteTracking -> {}
        }
    })


    LaunchedEffect(key1 = Unit, block = {

        val isData =
            navHostController?.currentBackStackEntry?.savedStateHandle?.contains(AppConstants.DATA_BUNDLE)
                ?: false
        if (isData && AppConstants.isLocNew) {
            navHostController?.currentBackStackEntry?.savedStateHandle?.get<LocationSearched>(
                AppConstants.DATA_BUNDLE
            )?.let {

                if (state.tripSelect == TripSelect.ORIGIN_LOC) {
                    viewModel.dispatch(
                        Intent.OriginLocChanged(it)
                    )
                } else {
                    viewModel.dispatch(
                        Intent.DestinationLocChanged(it)
                    )
                }
                AppConstants.isLocNew = false
            }
        }
    })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val fineLocationResult = it[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationResult = it[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationResult && coarseLocationResult) {
            viewModel.dispatch(Intent.InitCurrentLocation)
        } else {
            viewModel.dispatch(Intent.ShowToast(UIStr.ResStr(R.string.msg_location_permission_required)))
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        if (context.hasLocationPermission().not()) {
            permissionLauncher.launch(locationPermissions)
        } else {
            viewModel.dispatch(Intent.InitCurrentLocation)
        }
    })


    ProgressDialogApp(
        state.isLoading
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
    ) {


        AppBarWithBack(
            color = Purple,
            title = stringResource(id = R.string.trip_plan),
            arrowTint = Color.White,
            titleColor = Color.White,
        ) {
            navHostController?.popBackStack()
        }


        TripPlanHeader(state.date,
            state.time,
            state.originLoc,
            state.destinationLoc,
            onDateChanged = {
                viewModel.dispatch(Intent.DateChanged(it))
            },
            onTimeChanged = {
                viewModel.dispatch(Intent.TimeChanged(it))
            },
            onOriginClicked = {
//                viewModel.dispatch(Intent.NavLocationSearch(TripSelect.ORIGIN_LOC))
            },
            onCurrentLocationClick = {
                if (context.hasLocationPermission().not()) {
                    permissionLauncher.launch(locationPermissions)
                } else {
                    viewModel.dispatch(Intent.FetchCurrentLocation)
                }
            },
            onDestinationClicked = {
                viewModel.dispatch(Intent.NavLocationSearch(TripSelect.DESTINATION_LOC))
            },
            onSwapLocations = {
                viewModel.dispatch(Intent.SwitchLocations)
            })

        TripOptions(
            state.travelModes, state.selectedTravelMode
        ) {
            viewModel.dispatch(Intent.TravelModeChanged(it))
        }
        if (state.isLoading.not()) RouteSuggestions(state) {
            viewModel.dispatch(
                Intent.NavTripDetails(
                    route = it
                )
            )
        }

    }


}

@Composable
fun RouteSuggestions(state: TripPlanContract.State, onClick: (Route) -> Unit) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
    ) {


        item {

            Text(
                text = state.suggestedTitle.toStr(), modifier = Modifier.padding(
                    start = HORIZONTAL_SPACING.sdp, end = HORIZONTAL_SPACING.sdp, top = 18.sdp
                ), style = baseStyleLarge().copy(
                    color = FF333333, fontSize = 16.ssp
                )
            )
        }

        items(state.routes.size) {
            val item = state.routes[it]
            TripSuggestions(it, item, state.selectedTravelMode) {
                onClick(item)
            }
        }
    }
}

@Composable
fun TripPlanHeader(
    date: Long,
    time: Long,
    originLoc: LocationSearched?,
    destinationLoc: LocationSearched?,
    onDateChanged: (Long) -> Unit,
    onTimeChanged: (Long) -> Unit,
    onOriginClicked: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onDestinationClicked: () -> Unit,
    onSwapLocations: () -> Unit
) {


    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Purple)
            .padding(
                horizontal = HORIZONTAL_SPACING.sdp, vertical = 16.sdp
            )
    ) {
        Text(
            text = stringResource(R.string.let_plan_a_trip), style = baseStyleLarge().copy(
                color = Color.White, fontSize = 22.ssp, lineHeight = 35.ssp
            )
        )
        Row(
            modifier = Modifier.padding(
                top = 8.sdp
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = date.xtFormat(DATE_FORMAT), style = baseStyleLarge().copy(
                color = Color.White, fontSize = 16.ssp
            ), modifier = Modifier.noRippleClickable {
                NativeDatePicker(
                    context,
                    minDate = System.currentTimeMillis() - 1000,
                    onTimeChanged = onDateChanged
                )
            })
            Spacer(modifier = Modifier.width(4.sdp))
            Text(text = time.xtFormat(TIME_FORMAT_2), style = baseStyleLarge().copy(
                color = Color(0xFFA28DCA), fontSize = 12.ssp
            ), modifier = Modifier.noRippleClickable {
                NativeTimePicker(context) { time, hour, minutes ->
                    onTimeChanged(time)
                }
            })
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_dull_purple),
                contentDescription = null,
                modifier = Modifier.padding(
                    start = 2.sdp, bottom = 4.sdp
                )
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(
                    top = 12.sdp, bottom = 12.sdp
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.red_ellipse),
                    contentDescription = null,
                    modifier = Modifier
                )
                VerticalDottedLine(
                    height = 35
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_marker_yellow),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 6.sdp
                    )
            ) {
                TextFieldForTripPlan(
                    title = originLoc?.address ?: "",
                    hint = stringResource(R.string.current_location),
                    iconRight = R.drawable.ic_location_detect,
                    onClick = onOriginClicked,
                    onCurrentLocationClick = onCurrentLocationClick
                )
                Spacer(modifier = Modifier.height(8.sdp))
                TextFieldForTripPlan(
                    title = destinationLoc?.address ?: "",
                    hint = stringResource(id = R.string.end_destination),
                    onClick = onDestinationClicked
                )
            }
            Box {
                Image(
                    painter = painterResource(id = R.drawable.ic_swip_button),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable(onSwapLocations)
                )
            }
        }

    }
}


@Composable
fun TripOptions(
    transportModes: List<TravelModeData>,
    transportModeSelected: TravelModeData,
    onTravelModeChange: (TravelModeData) -> Unit
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 8.sdp, vertical = HORIZONTAL_SPACING.sdp
            )
            .noRippleClickable {

            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        transportModes.forEachIndexed { index, transportMode ->
            TransportModeComposable(transportMode, transportModeSelected, onTravelModeChange)
            if (index != transportModes.size - 1) Spacer(modifier = Modifier.width(10.sdp))
        }
    }
}


@Composable
fun TransportModeComposable(
    mode: TravelModeData,
    transportModeSelected: TravelModeData,
    onClick: (mode: TravelModeData) -> Unit
) {

    Column(
        modifier = Modifier
            .size(
                70.sdp, 90.sdp
            )
            .background(
                color = if (mode == transportModeSelected) FFEFE8FB
                else FFF9F9F9, shape = cornerShape(12)
            )
            .border(
                width = 1.sdp, color = if (mode == transportModeSelected) Purple
                else Color.Transparent, shape = cornerShape(12)
            )
            .noRippleClickable {
                onClick(mode)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = mode.icon), contentDescription = null)
        Spacer(modifier = Modifier.height(8.sdp))
        Text(
            text = mode.name.toStr(), style = baseStyle().copy(
                fontSize = 12.ssp,
                fontWeight = FontWeight.Bold,
                color = FF252826,
                textAlign = TextAlign.Center,
                letterSpacing = 0.85.sp,
            )
        )
    }
}

@Composable
@Preview
fun TripSuggestions(
    index: Int = 0,
    item: Route = Route(),
    travelModeData: TravelModeData = TravelModeData(),
    onClick: () -> Unit = {}
) {

    val isShow by remember {
        mutableStateOf(false)
    }

    val circleColor = if (index % 3 == 0) Purple
    else if (index % 3 == 1) FFEFB30F
    else FF69A251


    val heightInBetween = 6
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = HORIZONTAL_SPACING.sdp
            )
            .noRippleClickable(onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = 12.sdp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = if (travelModeData.isBeeline()) R.drawable.ic_bus_routes else travelModeData.icon),
                    contentDescription = null
                )
                if (isShow) Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircleApp(
                        size = 14, color = circleColor
                    )
                    Text(
                        text = "${item.getStopCounts() ?: ""}", style = baseStyle().copy(
                            color = Color.White, fontSize = 6.ssp
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = 20.sdp
                    )
            ) {
                Text(
                    text = if (travelModeData.isBeeline()) (item.getFormattedArrivalTime()
                        ?: "") else (item.summary ?: ""), style = baseStyle().copy(
                        fontSize = 14.ssp, fontWeight = FontWeight.Bold, color = FF252826
                    ), maxLines = 2
                )
                Spacer(modifier = Modifier.height(heightInBetween.sdp))
                if (travelModeData.isBeeline()) Text(
                    text = "${item.getFormattedDepartureTime()}", style = baseStyle2().copy(
                        fontSize = 11.ssp
                    ), maxLines = 2
                )
            }
            Column(
                horizontalAlignment = Alignment.End, modifier = Modifier.padding(
                    end = 12.sdp
                )
            ) {
                Text(
                    text = item.getDuration() ?: "", style = baseStyle().copy(
                        color = FF69A251, textAlign = TextAlign.End, fontSize = 10.ssp
                    )
                )
                Spacer(modifier = Modifier.height(heightInBetween.sdp))
                Text(
                    text = item.getDistanceFormatted() ?: "", style = baseStyle().copy(
                        fontSize = 12.ssp,
                        color = Color.Black,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.W500
                    )
                )
            }
        }
        HorizontalDivider(
            color = FFDADADA, modifier = Modifier.padding(
                start = 40.sdp
            )
        )
    }
}


@Composable
fun TextFieldForTripPlan(
    title: String = "",
    hint: String = "",
    iconRight: Int? = null,
    onClick: () -> Unit,
    onCurrentLocationClick: () -> Unit = {}
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 4.sdp
            )
            .noRippleClickable(onClick),
        value = title,
        onValueChange = {},
        singleLine = true,
        enabled = false,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.sdp)
                .background(
                    Purple, shape = RoundedCornerShape(size = CORNER_RADIUS.sdp)
                )
                .border(
                    width = 0.5.dp,
                    color = Color(0xFFA2AAAD),
                    shape = RoundedCornerShape(size = CORNER_RADIUS.sdp)
                )
                .padding(
                    start = 12.sdp
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = if (title.isEmpty()) hint else title, style = baseStyle2().copy(
                    color = Color.White, fontSize = 12.ssp
                ), modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(), maxLines = 1
            )
            iconRight?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable(onCurrentLocationClick)
                )
            }
        }
    }
}


@Preview
@Composable
fun LocationSettingDialog(
    onDismiss: () -> Unit = {}
) {
    DialogApp(onDismiss = { }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.sdp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.sdp))
            TitleWithDesc(
                title = stringResource(id = R.string.location), titleStyle = baseStyleLarge().copy(
                    color = FF333333, fontSize = 16.ssp
                ), desc = stringResource(R.string.msg_location_setting_permission), height = 6
            )
            Spacer(modifier = Modifier.height(20.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.sdp
                    ), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(DIALOG_BUTTON_HEIGHT.sdp),
                    title = stringResource(id = R.string.go_to_settings),
                    style = StyleButton().copy(
                        fontSize = 11.ssp
                    )
                ) {
                    onDismiss()
                }
                Spacer(modifier = Modifier.width(4.sdp))
                AppOutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(DIALOG_BUTTON_HEIGHT.sdp),
                    title = stringResource(id = R.string.cancel),
                    style = StyleButton().copy(
                        fontSize = 11.ssp
                    )
                ) {
                    onDismiss()
                }
            }
            Spacer(modifier = Modifier.height(20.sdp))
        }
    }
}


