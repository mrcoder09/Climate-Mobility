package com.cityof.glendale.navigation

sealed class Routes(val name: String) {


    object RedeemItemScreen : Routes("REEDEM_ITEM_SCREEN")


    object HOME_GRAPH : Routes("HOME_GRAPH")
    object LOGIN_GRAPH : Routes("LOGIN_GRAPH")

    object Splash : Routes("SPLASH")
    object Video : Routes("VIDEO")
    object Languages : Routes("LANGUAGE_SELECTION")
    object Landing : Routes("LANDING")
    object Login : Routes("LOGIN")
    object SignUp : Routes("SIGN_UP")
    object PersonalDetails : Routes("PERSONAL_DETAILS")
    object ForgotPwd : Routes("FORGOT_PWD")
    object OtpVerify : Routes("OTP_VERIFY/{email}")

    object CreatePwd : Routes("CREATE_PWD/{email}")

    object Dashboard : Routes("DASHBOARD")



    object DummyTC : Routes("TERM_CONDITION")

    //HOME
    object Home : Routes("Home")
    object VehicleEmission : Routes("VEHICLE_EMISSION")

    //TRIP PLANNING
    object TripPlan : Routes("TRIP_PLANNING")
    object TripDetail: Routes("TRIP_DETAILS")
    object RouteTracking: Routes("ROUTE_TRACKING_FRAG")
    object SaveTrips: Routes("SAVED_TRIPS")
    object LocationSearch : Routes("LOCATION")


    //FARE
    object Fare : Routes("Fare")
    object RouteList : Routes("ROUTE_LIST")
    object FareInfo : Routes("FARE_INFO")

    //REWARDS
    object Rewards : Routes("Rewards")
    object MerchantPass : Routes("MERCHANT_PASS")
    object MerchantDetails2 : Routes("MERCHANT_DETAILS_2")
    object RedeemDetails : Routes("REDEEM_DETAILS")

    //FEEDBACKS
    object Feedback : Routes("FEEDBACK")
    object FeedbackList : Routes("FEEDBACK_LIST")
    object MyFeedback :Routes("MY_FEEDBACK")


    object WebView : Routes("WebView/{title}/{url}")

    //MORE SCREENS
    object More : Routes("More")
    object ProfileSetting : Routes("PROFILE_SETTINGS")
    object EditProfile : Routes("EDIT_PROFILE/{login_data}")
    object NotificationSetting : Routes("NOTIFICATION_SETTING")
    object OtherInfo : Routes("OTHER_INFO")
    object ContactUs : Routes("CONTACT_US")

}