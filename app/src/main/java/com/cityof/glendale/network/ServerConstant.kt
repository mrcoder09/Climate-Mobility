package com.cityof.glendale.network

import com.cityof.glendale.BuildConfig

object Endpoints {
    const val BASE_URL = BuildConfig.BASE_URL
    const val SIGN_UP = "api/v1/create"
    const val LOGIN = "api/v1/userLogin"

    const val GET_SCHOOL_LIST = "api/v1/school/getSchool"
    const val GET_VEHICLE_LIST = "api/v1/vehicle/getVehicle"
    const val FORGOT_PASSWORD = "api/v1/forgotPassword"
    const val CHANGE_PASSWORD = "api/v1/changePassword"
    const val VERIFY_OTP = "api/v1/verifyOTP"
    const val RESEND_USER_ACTIVATION = "api/v1/resendUserActivationlink"
    const val LOGOUT = "api/v1/logout"
    const val USER_PROFILE = "api/v1/userProfile"
    const val PROFILE_UPDATE = "api/v1/userUpdate"
    const val PROFILE_DELETE = "api/v1/deleteUserAccount"
    const val UPDATE_PASSWORD = "api/v1/updatePassword"
    const val UPDATE_NOTIFICATION = "api/v1/update-notification"


    const val EMISSION_DETAILS = "api/v1/emission/get-details" //TODO: IMPLEMENT

    const val FARE_INFO = "api/v1/fare/getFarelist"


    const val ROUTE_LIST = "api/v1/route/getRouteList"
    const val ROUTE_MAP = "api/v1/route/getRouteMapByrouteId"

    const val HIVE_POINTS = "api/v1/wallet/getPointbyuserId"
    const val REDEEM_HIVE_POINTS = "api/v1/wallet/redeem-points"

    //MERCHANT API's
    const val MERCHANT_LIST = "api/v1/merchant/userMerchantList"
    const val MERCHANT_ITEMS = "api/v1/items/userMerchantItemsList"

    //FEEDBACKS
    const val FEEDBACK_LISTING = "api/v1/feedback/getFeedbacklist"
    const val FEEDBACK_CREATE = "api/v1/feedback/create"

    const val SAVED_TRIP_LIST = "api/v1//trip/getTrips"
    const val ADD_TRIP = "api/v1/trip/save-trip"
    const val EDIT_SAVED_TRIP = "api/v1/trip/update-trip"
    const val DELETE_TRIP = "api/v1/trip/delete-trip"
    const val TRIP_ACTIVITY = "api/v1/trip/activity"
    const val TRIP_LOG  = "api/v1/trip/logs"

    const val SOCIAL_MEDIA_TEMPLATE = "api/v1/social/get-user-post-template"

    //MISC LINKS
    const val PRIVACY_POLICY =
        "https://cityofglendale-web.mobileprogramming.net/app/privacyPolicy"
    const val LICENSE_AGREEMENT =
        "https://cityofglendale-web.mobileprogramming.net/app/termsAndCondition"
    const val FAQ = "https://cityofglendale-web.mobileprogramming.net/app/faqs"
    const val CONTACT_US_FORM = "https://www.glendaletransit.com/contact-us/customer-report-form"
    const val DEEP_LINKING_LINK =
        "https://cityofglendale-web.mobileprogramming.net/app/login"
    const val TAP_STORE = "https://www.taptogo.net/"

}

object UmoEndPoints{
    const val ROUTE_LIST = "agencies/glendale/routes"
    const val VEHICLE_LIST = "/agencies/glendale/vehicles"
    const val VEHICLE_LIST_ON_ROUTE = "agencies/{agency}/routes/{route}/vehicles"
}

object GoogleEndPoints{

    /**
     *
     * https://maps.googleapis.com/maps/api/place/autocomplete/json?input=a&key=
     * https://maps.googleapis.com/maps/api/directions/json?destination=Magnolia, 1501 N Victory Pl, Burbank, CA 91502, United States&origin=W Dryden St, Glendale, CA 91202, USA&mode=walking&key=AIzaSyBoqY_DrBsMotSEOc2SRTfzFgiCRxs5mQ4&transit_mode=Walking&alternatives=true
     */

    const val BASE_URL = "https://maps.googleapis.com"
    const val DIRECTION = "/maps/api/directions/json"
    const val PLACE_API = "/maps/api/place/autocomplete/json"
    const val PLACE_DETAIL= "/maps/api/place/details/json"

    const val GOOGLE_API_KEY = "AIzaSyBoqY_DrBsMotSEOc2SRTfzFgiCRxs5mQ4"
}


object Headers {
    const val BEARER_TOKEN = "Bearer-Token"
    const val ACCEPT_LANGUAGE = "Accept-Language"
    const val AUTHORIZATION = "Authorization"
    const val USER_NAME_VALUE = "CityBeeline@2024"
    const val PASSWORD_VALUE = "CityB@@l!ne@2024"

    const val BEARER = "Bearer"

    //UMO
    const val KEY = "x-umo-iq-api-key"
}

object CustomCodes {
    const val SUCCESS = 200
    const val SERVER_ERR = 500
    const val AUTHORIZATION_ERR = 401
    const val INVALID_DATA = 400

    //UMO
    const val RESOURCE_NOT_FOUND = 404
    const val RESOURCE_GONE = 410
    const val SERVICE_UNAVAILABLE = 503
}

object QueryParams {
    const val PAGE = "page"
    const val LIMIT = "limit"
    const val ACTIVATION_KEY = "activation_key"
    const val KEY_ID = "keyID"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val MERCHANT_ID = "merchant_id"
    const val DURATION = "duration"

    const val ROUTE_ID = "route_id"

    const val TRIP_ID = "id"

    //FEEDBACK
    const val BUS_NUMBER = "bus_number"
}
