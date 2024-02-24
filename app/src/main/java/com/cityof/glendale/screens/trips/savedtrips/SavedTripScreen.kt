package com.cityof.glendale.screens.trips.savedtrips

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.BUTTON_HEIGHT
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.navigation.removeBackStackEntries
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.canStartTrip
import com.cityof.glendale.network.responses.formattedPoints
import com.cityof.glendale.network.responses.getFormattedFromTime
import com.cityof.glendale.network.responses.getFormattedToTime
import com.cityof.glendale.network.responses.isTripOngoing
import com.cityof.glendale.network.responses.toIcon
import com.cityof.glendale.screens.trips.OptionsForDot
import com.cityof.glendale.theme.FF69A251
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber

@Composable
@Preview(showSystemUi = true)
fun SavedTripPreview() {
    SavedTripScreen(
        viewModel = SavedTripViewModel(
            appRepository = AppRepository(MockApiService())
        )
    )
}

@Composable
fun SavedTripScreen(
    navHostController: NavHostController? = null, viewModel: SavedTripViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is SavedTripContract.NavAction.RouteTracking -> {

                val trip = (navigation as SavedTripContract.NavAction.RouteTracking).trip
//                context.startActivity(
//                    Intent(
//                    context, RouteTrackingActivity::class.java
//                ).apply {
//                    putExtra(
//                        AppConstants.DATA_BUNDLE, route
//                    )
//                })

                navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, trip
                )
                navHostController?.navigate(Routes.RouteTracking.name)
            }

            is SavedTripContract.NavAction.TripPlan -> {

                (navigation as SavedTripContract.NavAction.TripPlan).trip?.let {
                    navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                        AppConstants.DATA_BUNDLE, it
                    )
                }
                navHostController?.navigate(Routes.TripPlan.name)
            }

            null -> {}
            SavedTripContract.NavAction.None -> {}
        }
        viewModel.dispatch(SavedTripContract.Intent.ResetNav)
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
        AppBarWithBack(
            color = Color.White, title = stringResource(id = R.string.saved_trips)
        ) {
            navHostController?.popBackStack()
        }

        SavedTripHeader {
//            navHostController?.navigate(Routes.TripPlan.name)

            navHostController?.removeBackStackEntries()
            viewModel.dispatch(SavedTripContract.Intent.NavTripPlan(false, null))
        }
        SavedTripDivider()

        LazyColumn {

            items(state.list.count()) {
                val trip = state.list[it]
                SavedTripComposable(item = trip, onEdit = {
                    viewModel.dispatch(SavedTripContract.Intent.NavTripPlan(true, trip))
                }, onDelete = {
                    viewModel.dispatch(SavedTripContract.Intent.DeleteTrip(trip.id ?: "", it))
                }) {
                    if (trip.canStartTrip()) {
                        viewModel.dispatch(SavedTripContract.Intent.StartTrip(trip))
                    } else {
                        viewModel.dispatch(SavedTripContract.Intent.ShowToast(UIStr.ResStr(R.string.trip_cannot_be_start_before_selected_date)))
                    }
                }
                SavedTripDivider()

            }

        }
    }
}

@Composable
fun SavedTripDivider() {
    HorizontalDivider(
        modifier = Modifier.height(20.dp)
    )
}

@Composable
fun SavedTripHeader(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(
                all = 18.sdp
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.my_trips), style = baseStyle2().copy(
                    fontSize = 16.ssp, fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(6.sdp))
            Text(
                text = stringResource(id = R.string.saved_trips), style = baseStyle2().copy(
                    fontSize = 12.ssp,
                )
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_fab_add),
            contentDescription = null,
            modifier = Modifier.noRippleClickable(onAdd)
        )
    }
}

@Composable
@Preview
fun SavedTripComposable(
    item: SavedTrip = SavedTrip(),
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onStartTrip: () -> Unit = {}
) {

    val context = LocalContext.current
    var isPopupVisible by remember {
        mutableStateOf(false)
    }

    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = 20.sdp, vertical = 20.sdp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_loc_purple),
                    contentDescription = null
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = 8.sdp
                        )
                ) {
                    Text(
                        text = item.label ?: "", style = baseStyle2().copy(
                            fontWeight = FontWeight.Bold
                        ), maxLines = 1
                    )
                    Text(
                        text = item.formattedPoints(context), style = baseStyle2().copy(
                            color = FF69A251, fontSize = 12.ssp
                        ), maxLines = 1
                    )
//                    Text(text = item.formattedDateTime())
                }
                Column {
                    Image(painter = painterResource(id = R.drawable.ic_three_dots),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.sdp)
                            .padding(end = 8.sdp)
                            .noRippleClickable {
                                Timber.d("ONCLICK")
                                isPopupVisible = true
                            }
                            .pointerInput(Unit) {
                                detectTapGestures {

                                    Timber.d("isPopupVisible = $isPopupVisible")
                                    offset = it
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

        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.sdp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.mode_of_transport),
                modifier = Modifier.weight(1f),
                style = baseStyle2().copy(
                    fontSize = 12.ssp, color = Color(0xFF324A5E)
                )
            )
            Image(
                painter = painterResource(id = item.toIcon()),
                contentDescription = null,
                modifier = Modifier.size(28.sdp)
            )
        }

        SavedTripItem(
            title = item.startingPoint ?: "",
            address = item.fromAddress ?: "",
            time = item.getFormattedFromTime()
        )
        Spacer(modifier = Modifier.height(6.dp))
        SavedTripItem(
            title = item.endDestination ?: "",
            address = item.toAddress ?: "",
            isOrigin = false,
            time = item.getFormattedToTime()
        )
        AppButton(
            title = if (item.isTripOngoing()) stringResource(id = R.string.direction) else stringResource(
                id = R.string.start_trip
            ),
            modifier = Modifier
                .width(120.sdp)
                .padding(top = 18.sdp)
                .height(BUTTON_HEIGHT.sdp),
            colors = if (item.canStartTrip()) ButtonDefaults.buttonColors(
                contentColor = Color.White, containerColor = Purple
            ) else ButtonDefaults.buttonColors(
                contentColor = Color.White, containerColor = FF777C80
            ),
            onClick = onStartTrip
        )
    }
}

//@Composable
//@Preview
//fun SavedTripItem(
//    title: String = "W Milford St",
//    address: String = "Glendale, CA 91203, USA",
//    time: String = "4:33 PM",
//    isOrigin: Boolean = true
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (isOrigin) OriginLineWithCircle()
//        else DestinationLineWithCircle()
//        Spacer(modifier = Modifier.width(4.sdp))
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .height(50.dp)
//        ) {
//            Text(
//                text = title, style = baseStyle2().copy(
//                    fontWeight = FontWeight.Bold
//                ), maxLines = 1
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = address, style = baseStyle2().copy(
//                    fontSize = 12.ssp
//                ), maxLines = 1
//            )
//        }
//        Text(
//            text = time, style = baseStyle2().copy(
//                fontSize = 12.ssp, fontWeight = FontWeight.ExtraBold
//            )
//        )
//    }
//}





