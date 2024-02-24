package com.cityof.glendale.utils

import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackIn


object AppConstants {

    //COMPILE TIME CONSTANTS
    const val DATA_BUNDLE = "Bundle_Data"
    const val ANDROID = "Android"
    const val ANIMATION_FILE = "beeline_animation.mp4"
    const val PICK_UP_TRUCKS = "Pick Up Trucks"

    //FEEDBACK
    const val TRIP_IN = "TRIP_IN"

    //Reward Items
    const val MERCHANT_DETAILS = "merchant"
    const val MERCHANT_ITEM = "merchant_item"
    const val HIVE_POINTS = "hive_points"

    //FARE
    const val FREE = "Free"

    //RUNTIME
    var isFromLogout = false
    var isLoadProfile = false
    var isBiometricAfterLogout = false
    var myFeedbackIn: MyFeedbackIn? = null

    //TRIP PLANNING
    var isLocNew = false
//    var travelMode: TravelMode = TravelMode.Bus


    //DRIVE PDF CONTROLLER
    const val PDF_VIEWER_LINK = "https://drive.google.com/viewerng/viewer?embedded=true&url="

}
