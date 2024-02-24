package com.cityof.glendale.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.ComposeMainActivity
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.SetStatusBarColor
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.screens.feedback.FeedbackScreen
import com.cityof.glendale.screens.home.HomeScreen
import com.cityof.glendale.screens.more.MoreScreen
import com.cityof.glendale.screens.rewards.RewardScreen
import com.cityof.glendale.screens.trips.TripScreen
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@Preview(showSystemUi = true)
fun BottomBar(
    navHostController: NavHostController? = null, viewModel: DashboardViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = DashboardContract.NavAction.None)

    if (state.isTripOnGoing()) {
        state.savedTrip?.let {
            OngoingTripBottomSheet(item = it, onDirection = {
                viewModel.dispatch(DashboardContract.Intent.NavRouteTrack(it))
            })
        }
    }

    LaunchedEffect(key1 = navigation, block = {
        Timber.d("DASHBOARD_LAUNCHED_EFFECT ${navigation.toString()}")
        when (navigation) {
            is DashboardContract.NavAction.NavRouteTrack -> {
                Timber.d("DASHBOARD_LAUNCHED_EFFECT ${navigation.toString()}")
                val trip = (navigation as DashboardContract.NavAction.NavRouteTrack).trip
                navHostController?.currentBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, trip
                )
                navHostController?.navigate(Routes.RouteTracking.name)
            }
            DashboardContract.NavAction.None -> {}
        }
        viewModel.dispatch(DashboardContract.Intent.ResetNav)
    })

    Scaffold(modifier = Modifier, bottomBar = {
        NavigationBar(containerColor = Color.White,
            modifier = Modifier
                .background(color = Color(0xFFFFFFFF))
                .graphicsLayer {
                    shadowElevation = 60f
                }) {
            arrayOf(
                R.drawable.ic_tab_home,
                R.drawable.ic_tab_trips,
                R.drawable.ic_tab_rewards,
                R.drawable.ic_tab_feedback,
                R.drawable.ic_tab_more
            ).zip(
                stringArrayResource(id = R.array.tab_arr)
            ) { icon, title ->
                BottomNavItem(
                    title, title.uppercase(Locale.getDefault()), painterResource(id = icon)
                )
            }.forEachIndexed { index, item ->
                NavigationBarItem(icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (selectedIndex == index) Purple else Color.Black
                    )
                }, label = {
                    Text(
                        item.title, style = baseStyle2().copy(
                            color = Color.Black,
                            fontSize = 12.ssp,
                            fontWeight = if (selectedIndex == index) FontWeight.W500 else FontWeight.Normal
                        ), maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, selected = selectedIndex == index, onClick = {
                    selectedIndex = index
                })
            }
        }
    }) {

        Timber.d(
            "${it.calculateBottomPadding()} ${it.calculateTopPadding()}"
        )
        SetStatusBarColor(Color.White)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(
                    bottom = it.calculateBottomPadding(),
                    top = 16.sdp
                )// Set the desired background color
        ) {
            when (selectedIndex) {
                0 -> {
                    HomeScreen(navHostController, onCross = { trip ->
                        viewModel.dispatch(DashboardContract.Intent.CheckOngoingTrip(trip))
                    })
                    (context as ComposeMainActivity).logEvents("HOME-SCREEN")
                }

                1 -> {
                    TripScreen(navHostController)
                    (context as ComposeMainActivity).logEvents("TRIP-SCREEN")
                }
                2 -> {
                    RewardScreen(navHostController)
                    (context as ComposeMainActivity).logEvents("REWARD-SCREEN")
                }
                3 -> {
                    FeedbackScreen(navHostController)
                    (context as ComposeMainActivity).logEvents("FEEDBACK-SCREEN")
                }
                4 -> {
                    MoreScreen(navHostController)
                    (context as ComposeMainActivity).logEvents("MORE-SCREEN")
                }
            }
        }
    }


}

//@Composable
//fun HomeScreen(navHostController: NavHostController?) {
//    TabContent(content = "Home Screen")
//}

@Composable
fun FareScreen(navHostController: NavHostController?) {
    Column {
        AppBarWithGlobe(
            modifier = Modifier.padding(
                start = 12.sdp,
//                end = 74.sdp
            )
        )
    }
}

//@Composable
//fun RewardScreen(navHostController: NavHostController?) {
//    TabContent(content = "Reward Screen")
//}

data class BottomNavItem(val title: String, val route: String = "", val icon: Painter)

@Composable
fun TabContent(content: String) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = content,
            modifier = Modifier.fillMaxSize(),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )

    }

}
