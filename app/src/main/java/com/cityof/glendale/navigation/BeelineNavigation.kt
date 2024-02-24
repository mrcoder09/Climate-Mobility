package com.cityof.glendale.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.cityof.glendale.composables.LockOrientation
import com.cityof.glendale.screens.FareScreen
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListScreen
import com.cityof.glendale.screens.forgotpwd.ForgotPwdScreen
import com.cityof.glendale.screens.forgotpwd.createnewpwd.CreatePwdScreen
import com.cityof.glendale.screens.forgotpwd.otpverify.OtpVerifyScreen
import com.cityof.glendale.screens.home.HomeScreen
import com.cityof.glendale.screens.home.vehiclemission.VehicleEmissionScreen
import com.cityof.glendale.screens.landing.Landing
import com.cityof.glendale.screens.languages.LanguageScreen
import com.cityof.glendale.screens.login.LoginScreen
import com.cityof.glendale.screens.more.MoreScreen
import com.cityof.glendale.screens.rewards.RewardScreen
import com.cityof.glendale.screens.signup.SignUpScreen
import com.cityof.glendale.screens.signup.personalDetails.PersonalDetailScreen
import com.cityof.glendale.screens.signup.personalDetails.TermConditionScreen
import com.cityof.glendale.screens.video.VideoScreen


//TODO: REPLACE THIS WITH CURRENT NAVIGATION TO STRUCTURE NAVIGATION FOR THE APP.

object BeelineNavigation {
    @Composable
    fun SetUpAppNavigation(navHostController: NavHostController) {
        NavHost(navController = navHostController,
            startDestination = Routes.LOGIN_GRAPH.name,
            builder = {
                this.loginGraph(navHostController)
                this.homeGraph(navHostController)
            })
    }
}


fun NavGraphBuilder.loginGraph(navHostController: NavHostController) {
    navigation(
        route = Routes.LOGIN_GRAPH.name, startDestination = Routes.Video.name
    ) {
        composable(Routes.Video.name) {
            LockOrientation()
            VideoScreen(navHostController)
        }
        composable(Routes.Languages.name) {
            LockOrientation(false)
            LanguageScreen(navHostController)
        }
        composable(Routes.Landing.name) {
            LockOrientation(false)
            Landing(navHostController)
        }
        composable(Routes.Login.name) {
            LockOrientation(false)
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
        composable(Routes.DummyTC.name) {
            TermConditionScreen(navHostController)
        }
    }
}

fun NavGraphBuilder.homeGraph(navHostController: NavHostController) {
    navigation(
        route = Routes.HOME_GRAPH.name, startDestination = Routes.Dashboard.name
    ) {
        composable(Routes.Dashboard.name) {
//            BeelineDashboardScreen(navHostController = navHostController)
        }

        composable(Routes.Home.name) {
            HomeScreen(navHostController)
        }
        composable(Routes.VehicleEmission.name) {
            VehicleEmissionScreen(navHostController)
        }
        composable(Routes.Fare.name) {
            FareScreen(navHostController)
        }
        composable(Routes.Rewards.name) {
            RewardScreen(navHostController)
        }
        composable(Routes.FeedbackList.name) {
            FeedbackListScreen(navHostController)
        }
        composable(Routes.More.name) {
            MoreScreen(navHostController)
        }
    }
}