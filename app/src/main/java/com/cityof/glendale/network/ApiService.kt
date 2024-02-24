package com.cityof.glendale.network

import com.cityof.glendale.network.responses.AddTripResponse
import com.cityof.glendale.network.responses.BaseResponse
import com.cityof.glendale.network.responses.EmissionResponse
import com.cityof.glendale.network.responses.FareInfoResponse
import com.cityof.glendale.network.responses.FeedbackResponse
import com.cityof.glendale.network.responses.HivePointResponse
import com.cityof.glendale.network.responses.LoginResponse
import com.cityof.glendale.network.responses.MerchantItemResponse
import com.cityof.glendale.network.responses.MerchantResponse
import com.cityof.glendale.network.responses.RouteMapResponse
import com.cityof.glendale.network.responses.RouteResponse
import com.cityof.glendale.network.responses.SavedTripResponse
import com.cityof.glendale.network.responses.SchoolResponse
import com.cityof.glendale.network.responses.SocialMediaTemplateResponse
import com.cityof.glendale.network.responses.TripActivityResponse
import com.cityof.glendale.network.responses.VehicleResponse
import retrofit2.Response
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {
    // User APIs
    @POST(Endpoints.SIGN_UP)
    suspend fun signUp(@Body body: Map<String, Any?>): Response<BaseResponse>

    @POST(Endpoints.LOGIN)
    suspend fun login(@Body body: Map<String, Any?>): Response<LoginResponse>

    @GET(Endpoints.GET_SCHOOL_LIST)
    suspend fun getSchoolList(
        @Query(QueryParams.PAGE) page: Int, @Query(QueryParams.LIMIT) limit: Int
    ): Response<SchoolResponse>

    @GET(Endpoints.GET_VEHICLE_LIST)
    suspend fun getVehicleList(): Response<VehicleResponse>

    @POST(Endpoints.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Body map: Map<String, Any?>): Response<BaseResponse>

    @POST(Endpoints.CHANGE_PASSWORD)
    suspend fun changePassword(@Body body: Map<String, Any?>): Response<BaseResponse>

    @POST(Endpoints.VERIFY_OTP)
    suspend fun verifyOTP(@Body body: Map<String, Any?>): Response<BaseResponse>

    @POST(Endpoints.RESEND_USER_ACTIVATION)
    suspend fun resendUserActivation(@Body body: Map<String, Any?>): Response<BaseResponse>

    @GET(Endpoints.LOGOUT)
    suspend fun logout(): Response<BaseResponse>

    @GET(Endpoints.USER_PROFILE)
    suspend fun userProfile(): Response<LoginResponse>

    @PUT(Endpoints.PROFILE_UPDATE)
    suspend fun profileUpdate(@Body body: Map<String, Any?>): Response<LoginResponse>

    @DELETE(Endpoints.PROFILE_DELETE)
    suspend fun profileDelete(): Response<BaseResponse>

    @POST(Endpoints.UPDATE_PASSWORD)
    suspend fun updatePassword(@Body body: Map<String, Any?>): Response<BaseResponse>

    @GET(Endpoints.MERCHANT_LIST)
    suspend fun merchantList(
        @Query(QueryParams.PAGE) page: Int, @Query(QueryParams.LIMIT) limit: Int
    ): Response<MerchantResponse>

    @GET(Endpoints.MERCHANT_ITEMS)
    suspend fun merchantItems(
        @Query(QueryParams.PAGE) page: Int,
        @Query(QueryParams.LIMIT) limit: Int,
        @Query(QueryParams.MERCHANT_ID) id: Int
    ): Response<MerchantItemResponse>

    @GET(Endpoints.EMISSION_DETAILS)
    suspend fun emissionDetails(
        @Query(QueryParams.DURATION) duration: String
    ): Response<EmissionResponse>

    @GET(Endpoints.HIVE_POINTS)
    suspend fun hivePoints(): Response<HivePointResponse>

    @POST(Endpoints.REDEEM_HIVE_POINTS)
    suspend fun redeemHivePoints(@Body map: Map<String, Any?>): Response<BaseResponse>

    @GET(Endpoints.ROUTE_LIST)
    suspend fun routeList(): Response<RouteResponse>

    @GET(Endpoints.ROUTE_MAP)
    suspend fun routeMap(
        @Query(QueryParams.ROUTE_ID) routeId: String
    ): Response<RouteMapResponse>

    @GET(Endpoints.FARE_INFO)
    suspend fun fareInfoList(): Response<FareInfoResponse>


    @GET(Endpoints.FEEDBACK_LISTING)
    suspend fun feedbackList(
        @Query(QueryParams.BUS_NUMBER) busNumber: String
    ): Response<FeedbackResponse>

    @POST(Endpoints.FEEDBACK_CREATE)
    suspend fun feedbackCreate(@Body body: Map<String, Any?>): Response<BaseResponse>


    @GET(Endpoints.SAVED_TRIP_LIST)
    suspend fun getSavedTrips(): Response<SavedTripResponse>
    @POST(Endpoints.ADD_TRIP)
    suspend fun addTrip(@Body body: Map<String, Any?>): Response<AddTripResponse>
    @PUT(Endpoints.EDIT_SAVED_TRIP)
    suspend fun editSavedTrip(@Body body: Map<String, Any?>): Response<BaseResponse>

    @HTTP(method = "DELETE", path = Endpoints.DELETE_TRIP, hasBody = true)
    suspend fun deleteTrip(@Body id: Map<String,Any>): Response<BaseResponse>


    @PUT(Endpoints.TRIP_ACTIVITY)
    suspend fun tripActivity(@Body body: Map<String, Any?>): Response<TripActivityResponse>

    @POST(Endpoints.TRIP_LOG)
    suspend fun tripLog(@Body body: Map<String, Any?>): Response<BaseResponse>

    @GET(Endpoints.SOCIAL_MEDIA_TEMPLATE)
    suspend fun socialMediaTemplate(): Response<SocialMediaTemplateResponse>

    @PUT(Endpoints.UPDATE_NOTIFICATION)
    suspend fun updateNotification(@Body body: Map<String, Any?>): Response<BaseResponse>
}