package com.cityof.glendale.screens.trips.routetracking

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavHostController
import com.cityof.glendale.ComposeMainActivity
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackIn
import com.cityof.glendale.utils.AppConstants
import timber.log.Timber


//@Composable
//fun RouteTrackingScreen(navHostController: NavHostController? = null) {
//
//    AndroidViewBinding(factory = FragmentRouteTrackingBinding::inflate){
//
//    }
//}


//@Composable
//fun RouteTrackingScreen(
//    navHostController: NavHostController? = null
//) {
//    val tag = RouteTrackingFragment::class.java.simpleName
//    val context = LocalContext.current as ComposeMainActivity
//
//    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->
//        FrameLayout(context).apply {
//            id = ViewCompat.generateViewId()
//        }
//    }, update = {
//        val fragmentManager = context.supportFragmentManager
//        val route =
//            navHostController?.previousBackStackEntry?.savedStateHandle?.get<Route>(AppConstants.DATA_BUNDLE)
//        val fragmentRoute = fragmentManager.findFragmentByTag(tag)
//        val fragmentAlreadyAdded = fragmentRoute != null
//
//
//        if (!fragmentAlreadyAdded) {
//            val fragment = RouteTrackingFragment.newInstance(route)
//            fragmentManager.commit {
//                add(
//                    it.id, RouteTrackingFragment.newInstance(route), tag
//                )
//            }
//        } else {
//            fragmentRoute?.let {
//                fragmentManager.commit {
//                    replace(it.id, it)
//                }
//            }
//        }
//    })
//}


//@Composable
//fun RouteTrackingScreen( navController: NavHostController? = null) {
//
//    AndroidViewBinding(RouteTrackingContainerViewBinding::inflate){
//        val fragment = fragmentContainerView.getFragment<RouteTrackingFragment>()
//
//    }
//}


@Composable
fun RouteTrackingScreen(
    navController: NavHostController? = null
) {

    val activity = LocalContext.current as ComposeMainActivity
    val containerId by rememberSaveable { mutableIntStateOf(View.generateViewId()) }
    val scope = rememberCoroutineScope()

    AndroidView(modifier = Modifier.fillMaxSize(), factory = { context ->

        val fragmentContainerView = FragmentContainerView(context).apply {
            id = containerId
        }

        val trip = navController?.previousBackStackEntry?.savedStateHandle?.get<SavedTrip>(
            AppConstants.DATA_BUNDLE
        )

        val fragment = RouteTrackingFragment.newInstance(trip, onExit = {
            navController?.navigate(Routes.Dashboard.name) {
                popUpTo(Routes.Dashboard.name) {
                    inclusive = false
                }
            }
        }, onFeedback = { latlng, busId ->
            Timber.d("BUS_ID : $busId")
            val feedbackIn = MyFeedbackIn(
                from = trip?.fromAddress,
                to = trip?.toAddress,
                busNumber = busId,
                isFromRouteTracking = true
            )

            navController?.currentBackStackEntry?.savedStateHandle?.set(
                AppConstants.TRIP_IN, feedbackIn
            )

            AppConstants.myFeedbackIn = feedbackIn
            Timber.d("PRINT ME")
            navController?.navigate(Routes.MyFeedback.name) {
                popUpTo(
                    Routes.Dashboard.name
                )
            }
        }, onPop = {
            navController?.popBackStack()
        })

        activity.supportFragmentManager.beginTransaction()
            .replace(containerId, fragment, fragment.javaClass.simpleName).commitAllowingStateLoss()

        fragmentContainerView
    })

    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            Timber.d("ROUTE_TRACK: ON_DISPOSE_OFF")
            activity.supportFragmentManager.findFragmentById(containerId)?.let { fragment ->
                activity.supportFragmentManager.beginTransaction().remove(fragment)
                    .commitAllowingStateLoss()
            }
        }
    }
}