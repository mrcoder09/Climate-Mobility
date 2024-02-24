package com.cityof.glendale.screens.feedback

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AnnotatedClickableText
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.DIALOG_BUTTON_HEIGHT
import com.cityof.glendale.composables.components.DialogApp
import com.cityof.glendale.composables.components.DropDownText
import com.cityof.glendale.composables.components.FeedbackDropDown
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.clickSpanStyle
import com.cityof.glendale.composables.components.normalSpanStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.UmoVehicle
import com.cityof.glendale.network.umoresponses.toBusMarker
import com.cityof.glendale.network.umoresponses.toComposeColor
import com.cityof.glendale.screens.trips.tripPlan.locationPermissions
import com.cityof.glendale.theme.ERR_RED
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.bitmapDescriptorFromVector
import com.cityof.glendale.utils.xtHasPermission
import com.cityof.glendale.utils.xtIntentDialer
import com.cityof.glendale.utils.xtJson
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber

@Composable
fun FeedbackScreen(
    navHostController: NavHostController?, viewModel: FeedbackViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    val showDisclaimer = remember { mutableStateOf(true) }
    val context = LocalContext.current
    var properties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL
                )
            )
    }

    var isCurrentLoc by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is FeedbackContract.NavAction.NavFeedbackList -> {
                navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE,
                    (navigation as FeedbackContract.NavAction.NavFeedbackList).vehicle
                )
                navHostController?.navigate(
                    Routes.FeedbackList.name
                )
            }

            null -> {}
        }
    })

    LaunchedEffect(key1 = Unit, block = {
        viewModel.is911Dialog()
    })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

        Timber.d(
            "${it.xtJson()}"
        )
        val fineLocationResult = it[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationResult = it[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationResult && coarseLocationResult) {
            viewModel.getCurrentLocation()
            properties = properties.copy(
                isMyLocationEnabled = true
            )
        } else {
            viewModel.dispatch(FeedbackContract.Intent.ShowToast(UIStr.Str("Location Permission is required for this functionality.")))
        }

    }

    LaunchedEffect(key1 = isCurrentLoc, block = {
        if (isCurrentLoc.not()) return@LaunchedEffect
        if (!context.xtHasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) && context.xtHasPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).not()
        ) {
            permissionLauncher.launch(locationPermissions)
        } else {
            viewModel.getCurrentLocation()
            properties = properties.copy(
                    isMyLocationEnabled = true
            )
        }
        isCurrentLoc = false
    })

    ToastApp(
        state.toastMsg
    )

    DisclaimerAlert(value = state.is911Dialog, onDismiss = {
        showDisclaimer.value = false
        viewModel.initUi()
        viewModel.dispatch(FeedbackContract.Intent.Set911Visibility(false))
    }) {
        context.xtIntentDialer("911")
        viewModel.dispatch(FeedbackContract.Intent.Set911Visibility(false))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBarWithGlobe(modifier = Modifier.padding(start = 12.sdp))

        Box {
            MapComposable(
                state.isLoading,
                state.currentLatLng,
                state.routes,
                state.vehicles,
                state.selectedRoute,
                properties,
                onRouteSelected = {
                    viewModel.dispatch(FeedbackContract.Intent.VehicleList(it))
                },
                onCurrentLocationRequest = {
                    Timber.d("$isCurrentLoc")
                    isCurrentLoc = true
                    Timber.d("$isCurrentLoc")
                }, onVehicleClicked = {
                    viewModel.dispatch(FeedbackContract.Intent.NavFeedbackList(it))
                })


        }

    }
}


@Composable
@Preview
fun MapComposable(
    isLoading: Boolean = false,
    currentLatLng: LatLng = LatLng(0.0, 0.0),
    routes: List<UmoRoute> = emptyList(),
    vehicles: List<UmoVehicle> = emptyList(),
    route: UmoRoute = UmoRoute(),
    properties: MapProperties =  MapProperties(
        mapType = MapType.NORMAL
    ),
    onCurrentLocationRequest: () -> Unit = {},
    onRouteSelected: (UmoRoute) -> Unit = {},
    onVehicleClicked: (UmoVehicle) -> Unit = {},
) {

    var selectedindex by remember { mutableIntStateOf(0) }
//    var selectedValue by remember { mutableStateOf(UmoRoute()) }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                zoomControlsEnabled = true
            )
        )
    }


    val initialZoom = 6f
    val finalZoom = 15f
    var destinationLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destinationLatLng, initialZoom)
    }

    if (destinationLatLng == LatLng(0.0, 0.0)) {
        LaunchedEffect(key1 = currentLatLng) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(
                        currentLatLng, finalZoom, 0f, 0f
                    )
                ), durationMs = 1000
            )
        }
    } else LaunchedEffect(key1 = destinationLatLng) {
        Timber.d("CAMERA: destinationLatLng changed")
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(destinationLatLng, finalZoom, 0f, 0f)
            ), durationMs = 1000
        )
    }

//    LaunchedEffect(key1 = destinationLatLng) {
//        Timber.d("CAMERA: destinationLatLng changed")
//        Timber.d(
//            "${
//                destinationLatLng == LatLng(0.0, 0.0)
//            }"
//        )
//        if (destinationLatLng == LatLng(0.0, 0.0)) {
//            cameraPositionState.animate(
//                update = CameraUpdateFactory.newCameraPosition(
//                    CameraPosition(
//                        currentLatLng, finalZoom, 0f, 0f
//                    )
//                ), durationMs = 1000
//            )
//        } else cameraPositionState.animate(
//            update = CameraUpdateFactory.newCameraPosition(
//                CameraPosition(destinationLatLng, finalZoom, 0f, 0f)
//            ), durationMs = 1000
//        )
//    }

    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState,
        ) {

            vehicles.forEachIndexed { index, vehicle ->
                Timber.d("TAGT: ${vehicle.xtJson()}")
                if (vehicle.lat != null && vehicle.lon != null) {
                    Marker(state = MarkerState(
                        position = LatLng(vehicle.lat, vehicle.lon)
                    ), icon = bitmapDescriptorFromVector(
                        LocalContext.current, route.toBusMarker()
                    )

//                    bitmapDescriptorFromVector(
//                        LocalContext.current, R.drawable.ic_marker
//                    )
                        , onClick = {
                        onVehicleClicked(vehicle)
                        true
                    })
                    Timber.d("TAG: index == vehicles.lastIndex == ${index == vehicles.lastIndex}")
                    if (index == vehicles.lastIndex) {
                        destinationLatLng = LatLng(vehicle.lat, vehicle.lon)
                    }
                }
            }
        }

        FeedbackDropDown(list = routes,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 12.sdp, end = 12.sdp, top = 12.sdp
                ),
            selectedIndex = selectedindex,
            selectedValue = route.title ?: stringResource(R.string.select_your_route),
            color = route.toComposeColor(),
            routeId = route.id ?: "",
            leadingIcon = R.drawable.ic_search,
            onItemSelected = { index, item ->
//                selectedValue = item
                selectedindex = index
                onRouteSelected(item)
            }) {
            DropDownText(
                text = it.title ?: ""
            )
        }

        ProgressDialogApp(
            isLoading
        )

        Image(
            painter = painterResource(id = R.drawable.location_crosshairs),
            contentDescription = null,
            modifier = Modifier
                .padding(
                    end = 10.sdp, bottom = 86.sdp
                )
                .background(
                    color = Color.White, shape = RoundedCornerShape(32.dp)
                )
                .size(34.sdp)
                .noRippleClickable(onCurrentLocationRequest)
                .padding(8.sdp)
                .align(Alignment.BottomEnd)
        )
    }

}


@Composable
@Preview
fun DisclaimerAlert(
    value: Boolean = true, onDismiss: () -> Unit = {}, onContinue: () -> Unit = {}
) {

    if (value) {
        DialogApp {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(
                    horizontal = 14.sdp
                )
            ) {

                Spacer(modifier = Modifier.height(8.sdp))
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier.noRippleClickable(
                            onDismiss
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.sdp))
                Text(
                    text = stringResource(R.string.disclaimer), style = baseStyle().copy(
                        fontSize = 16.ssp
                    )
                )
                Spacer(modifier = Modifier.height(8.sdp))
                AnnotatedClickableText(
                    text = stringResource(R.string.if_you_are_experiencing_or_witnessing_an_emergency),
                    clickableText = stringResource(R.string.call_911),
                    normalSpanStyle = normalSpanStyle().copy(
                        fontWeight = FontWeight.Normal, fontSize = 14.ssp, color = Color.Black
                    ),
                    clickSpanStyle = clickSpanStyle().copy(
                        color = ERR_RED, fontSize = 14.ssp, fontWeight = FontWeight.Medium
                    ),
                    maxLines = 3
                ) {

                }
                Spacer(modifier = Modifier.height(20.sdp))
                AppButton(modifier = Modifier
                    .width(100.sdp)
                    .height(DIALOG_BUTTON_HEIGHT.sdp)
                    .padding(
                        horizontal = 2.sdp
                    ),
                    title = stringResource(id = R.string.call),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White, containerColor = ERR_RED
                    ),
                    onClick = {
                        onContinue()
                        onDismiss()
                    })
                Spacer(modifier = Modifier.height(26.sdp))
            }
        }
    }

}