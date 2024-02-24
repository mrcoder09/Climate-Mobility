package com.cityof.glendale.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.cityof.glendale.composables.components.WebViewScreen
import com.cityof.glendale.network.Endpoints
import com.cityof.glendale.network.responses.LoginData
import com.cityof.glendale.screens.BottomBar
import com.cityof.glendale.screens.FareScreen
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListScreen
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackScreen
import com.cityof.glendale.screens.forgotpwd.ForgotPwdScreen
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdScreen
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyScreen
import com.cityof.glendale.screens.home.HomeScreen
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionScreen
import com.cityof.glendale.screens.landing.Landing
import com.cityof.glendale.screens.languages.LanguageScreen
import com.cityof.glendale.screens.login.LoginScreen
import com.cityof.glendale.screens.more.MoreScreen
import com.cityof.glendale.screens.more.contactus.ContactUsScreen
import com.cityof.glendale.screens.more.notifications.NotificationSettings
import com.cityof.glendale.screens.more.profileSettings.ProfileSettingScreen
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileScreen
import com.cityof.glendale.screens.more.profileSettings.editprofile.EditProfileViewModel
import com.cityof.glendale.screens.more.profileSettings.editprofile.OtherInfoScreen
import com.cityof.glendale.screens.rewards.RewardScreen
import com.cityof.glendale.screens.rewards.merchantdetails.MerchantDetailScreen
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemDetailScreen
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemDetailViewModel
import com.cityof.glendale.screens.rewards.redeemdetails.RedeemItemScreen
import com.cityof.glendale.screens.signup.SignUpScreen
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailScreen
import com.cityof.glendale.screens.signup.personalDetails.TermConditionScreen
import com.cityof.glendale.screens.trips.fareinfo.FareInfoScreen
import com.cityof.glendale.screens.trips.locationSearch.LocationSearchScreen
import com.cityof.glendale.screens.trips.routemap.RouteListScreen
import com.cityof.glendale.screens.trips.routetracking.RouteTrackingScreen
import com.cityof.glendale.screens.trips.savedtrips.SavedTripScreen
import com.cityof.glendale.screens.trips.tripPlan.TripDetailScreen
import com.cityof.glendale.screens.trips.tripPlan.TripPlanScreen
import com.cityof.glendale.screens.trips.tripPlan.TripPlanViewModel
import com.cityof.glendale.screens.video.VideoScreen
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.xtJson
import timber.log.Timber

object AppNav {


    @Composable
    fun SetUpNavigation(navHostController: NavHostController) {
        NavHost(navController = navHostController, startDestination = Routes.Video.name, builder = {
            composable(Routes.Video.name) {
//                LockOrientation()
                VideoScreen(navHostController)
            }
            composable(Routes.Languages.name) {
//                LockOrientation(false)
                LanguageScreen(navHostController)
            }
            composable(Routes.Landing.name) {
//                LockOrientation(false)
                Landing(navHostController)
            }
            composable(route = Routes.Login.name, deepLinks = listOf(navDeepLink {
                uriPattern = Endpoints.DEEP_LINKING_LINK
            })) {
//                LockOrientation(false)
                LoginScreen(navHostController)
            }
            composable(Routes.SignUp.name) {
                SignUpScreen(navHostController)
            }
            composable(Routes.PersonalDetails.name) {
                PersonalDetailScreen(navHostController)
            }


            composable(Routes.ForgotPwd.name) {
                ForgotPwdScreen(navHostController)
            }
            composable(Routes.OtpVerify.name, arguments = listOf(navArgument("email") {
                type = NavType.StringType
            })) { navBackStackEntry ->
                val email = navBackStackEntry.arguments?.getString("email") ?: ""
                OtpVerifyScreen(navHostController, email)
            }
            composable(Routes.CreatePwd.name, arguments = listOf(navArgument("email") {
                type = NavType.StringType
            })) { navBackStackEntry ->
                val email = navBackStackEntry.arguments?.getString("email") ?: ""
                CreatePwdScreen(navHostController, email)
            }


            composable(Routes.Dashboard.name) {
//                LockOrientation(false)
                BottomBar(navHostController)
            }



            composable(Routes.ProfileSetting.name) {
                ProfileSettingScreen(navHostController)
            }
            composable(Routes.EditProfile.name) { navBackStackEntry ->

                LaunchedEffect(key1 = navBackStackEntry, block = {
                    val loginData =
                        navHostController.previousBackStackEntry?.savedStateHandle?.get<LoginData>(
                            AppConstants.DATA_BUNDLE
                        )
                    Timber.d(
                        loginData?.xtJson()
                    )
                })

                EditProfileScreen(navHostController)
            }



            composable(Routes.NotificationSetting.name) {
                NotificationSettings(navHostController)
            }
            composable(Routes.OtherInfo.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(Routes.EditProfile.name)
                }
                val viewModel: EditProfileViewModel = hiltViewModel(parentEntry)
                OtherInfoScreen(navHostController, viewModel)
            }
            composable(Routes.ContactUs.name) {
                ContactUsScreen(navHostController)
            }

            //TRIP PLANNING
            composable(Routes.TripPlan.name) {
                TripPlanScreen(navHostController)
            }
            composable(Routes.LocationSearch.name) {
                LocationSearchScreen(navHostController)
            }
            composable(Routes.TripDetail.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(Routes.TripPlan.name)
                }
                val viewModel: TripPlanViewModel = hiltViewModel(parentEntry)
                TripDetailScreen(navHostController, viewModel)
            }
            composable(Routes.RouteTracking.name){
//                RouteTrackingScreen(navHostController = navHostController)

                RouteTrackingScreen(navController = navHostController)

//                TrackingRoute(navHostController)
            }
            composable(Routes.SaveTrips.name) {
                SavedTripScreen(navHostController)
            }



            composable(Routes.DummyTC.name) {
                TermConditionScreen(navHostController)
            }
            composable(Routes.WebView.name, arguments = listOf(navArgument("title") {
                type = NavType.StringType
            }, navArgument("url") {
                type = NavType.StringType
            })) {
                val title = it.arguments?.getString("title") ?: ""
                val url = it.arguments?.getString("url") ?: ""
                WebViewScreen(title, url, navHostController)
            }

            //HOME
            composable(Routes.Home.name) {
                HomeScreen(navHostController)
            }
            composable(Routes.VehicleEmission.name) {
                VehicleEmissionScreen(navHostController)
            }


            //FARE
            composable(Routes.Fare.name) {
                FareScreen(navHostController)
            }

            composable(Routes.FareInfo.name) {
                FareInfoScreen(navHostController)
            }

            composable(Routes.RouteList.name) {
                RouteListScreen(navHostController)
            }

            //REWARDS
            composable(Routes.Rewards.name) {
                RewardScreen(navHostController)
            }
            composable(Routes.MerchantDetails2.name) {
                MerchantDetailScreen(navHostController)
            }
            composable(Routes.RedeemDetails.name) {
                RedeemDetailScreen(navHostController)
            }
            composable(Routes.RedeemItemScreen.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(Routes.RedeemDetails.name)
                }
                val viewModel: RedeemDetailViewModel = hiltViewModel(parentEntry)
                RedeemItemScreen(navHostController, viewModel)
            }


//            composable(Routes.Feedback.name) {
//                FeedbackScreen(navHostController)
//            }

            //FEEDBACKS
            composable(Routes.FeedbackList.name) {
                FeedbackListScreen(navHostController)
            }
//            composable(Routes.MyFeedback.name) {
//                val id = it.arguments?.getString(AppConstants.BUS_ID, null)
//                MyFeedbackScreen(
//                    navHostController = navHostController,
//                    id = id)
//            }

            composable(Routes.MyFeedback.name) {
//                val id = it.arguments?.getString(AppConstants.BUS_ID, null)
                MyFeedbackScreen(
                    navHostController = navHostController)
            }

            composable(Routes.More.name) {
                MoreScreen(navHostController)
            }

        })
    }

}


