package com.cityof.glendale.network

import com.cityof.glendale.network.responses.AddTripResponse
import com.cityof.glendale.network.responses.AuthorizationErr
import com.cityof.glendale.network.responses.BaseResponse
import com.cityof.glendale.network.responses.EmissionResponse
import com.cityof.glendale.network.responses.FareInfoResponse
import com.cityof.glendale.network.responses.FeedbackResponse
import com.cityof.glendale.network.responses.HivePointResponse
import com.cityof.glendale.network.responses.LoginResponse
import com.cityof.glendale.network.responses.Merchant
import com.cityof.glendale.network.responses.MerchantItem
import com.cityof.glendale.network.responses.MerchantItemResponse
import com.cityof.glendale.network.responses.MerchantResponse
import com.cityof.glendale.network.responses.Result
import com.cityof.glendale.network.responses.RouteMapResponse
import com.cityof.glendale.network.responses.RouteResponse
import com.cityof.glendale.network.responses.SavedTripResponse
import com.cityof.glendale.network.responses.SchoolResponse
import com.cityof.glendale.network.responses.SocialMediaTemplateResponse
import com.cityof.glendale.network.responses.TripActivityResponse
import com.cityof.glendale.network.responses.VehicleResponse
import com.cityof.glendale.network.responses.isAuthorizationErr
import com.cityof.glendale.network.responses.safeApiCall
import com.cityof.glendale.network.responses.toResult
import retrofit2.Response
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun signUp(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.signUp(map).toResult()
        }
    }

    suspend fun login(map: Map<String, Any?>): Result<LoginResponse> {

        return safeApiCall {
            apiService.login(map).toResult()
        }
    }

    suspend fun getSchoolList(page: Int, limit: Int): Result<SchoolResponse> {
        return safeApiCall {
            apiService.getSchoolList(page, limit).toResult()
        }
    }

    suspend fun getVehicleList(): Result<VehicleResponse> {
        return safeApiCall {
            apiService.getVehicleList().toResult()
        }
    }

    suspend fun forgotPassword(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.forgotPassword(map).toResult()
        }
    }

    suspend fun changePassword(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.changePassword(map).toResult()
        }
    }

    suspend fun verifyOTP(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.verifyOTP(map).toResult()
        }
    }

    suspend fun resendUserActivation(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.resendUserActivation(map).toResult()
        }
    }

    suspend fun logout(): Result<BaseResponse> {
        return safeApiCall {
            apiService.logout().toResult()
        }
    }

    suspend fun userProfile(): Result<LoginResponse> {
        return safeApiCall {
            apiService.userProfile().toResult()
        }
    }

    suspend fun profileUpdate(map: Map<String, Any?>): Result<LoginResponse> {
        return safeApiCall {
            apiService.profileUpdate(map).toResult()
        }
    }

    suspend fun profileDelete(): Result<BaseResponse> {
        return safeApiCall {
            apiService.profileDelete().toResult()
        }
    }

    suspend fun updatePassword(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            apiService.updatePassword(map).toResult()
        }
    }


    suspend fun merchantList(page: Int = 0, limit: Int = 0): Result<List<Merchant>> {
        return safeApiCall {
            when (val result = apiService.merchantList(page = page, limit = limit).toResult()) {
                is Result.Error -> Result.Error(result.err)
                is Result.Success -> {
                    if (isAuthorizationErr(result.data.customCode)) {
                        Result.Error(AuthorizationErr())
                    } else Result.Success(result.data.data ?: emptyList())
                }
            }
        }
    }

    suspend fun merchantItems(
        page: Int = 0, limit: Int = 0, id: Int = 0
    ): Result<List<MerchantItem>> {
        return safeApiCall {

            val result = apiService.merchantItems(
                page = page, limit = limit, id = id
            ).toResult()

            when (result) {
                is Result.Error -> Result.Error(result.err)
                is Result.Success -> {
                    if (isAuthorizationErr(result.data.customcode)) {
                        Result.Error(AuthorizationErr())
                    } else Result.Success(result.data.data ?: emptyList())
                }
            }

        }
    }

    suspend fun hivePoints(): Result<HivePointResponse> {
        return safeApiCall {
            val result = apiService.hivePoints().toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun redeemHivePoints(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            val result = apiService.redeemHivePoints(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun emissionDetails(duration: String = ""): Result<EmissionResponse> {
        return safeApiCall {
            val result = apiService.emissionDetails(duration).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


    suspend fun fareList(): Result<FareInfoResponse> {
        return safeApiCall {
            val result = apiService.fareInfoList().toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun routeList(): Result<RouteResponse> {
        return safeApiCall {
            val result = apiService.routeList().toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun routeMap(routeId: String): Result<RouteMapResponse> {
        return safeApiCall {
            val result = apiService.routeMap(routeId).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


    suspend fun feedbackList(busNumber:String = ""): Result<FeedbackResponse> {
        return safeApiCall {
            val result = apiService.feedbackList(busNumber).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun feedbackCreate(map:Map<String,Any?>): Result<BaseResponse> {
        return safeApiCall {
            val result = apiService.feedbackCreate(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


    suspend fun savedTrips(): Result<SavedTripResponse> {
        return safeApiCall {
            val result = apiService.getSavedTrips().toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun addTrip(map: Map<String, Any?>): Result<AddTripResponse> {
        return safeApiCall {
            val result = apiService.addTrip(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun editTrip(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            val result = apiService.editSavedTrip(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun deleteTrip(tripId:String = ""): Result<BaseResponse> {
        return safeApiCall {
            val map = mapOf(
                "id" to tripId
            )
            val result = apiService.deleteTrip(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun tripActivity(map: Map<String, Any?>): Result<TripActivityResponse> {
        return safeApiCall {
            val result = apiService.tripActivity(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customcode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }

    suspend fun tripLog(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            val result = apiService.tripLog(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


    suspend fun socialMediaTemplate(): Result<SocialMediaTemplateResponse> {
        return safeApiCall {
            val result = apiService.socialMediaTemplate().toResult()

            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


    suspend fun updateNotification(map: Map<String, Any?>): Result<BaseResponse> {
        return safeApiCall {
            val result = apiService.updateNotification(map).toResult()
            if (result is Result.Success && isAuthorizationErr(result.data.customCode)) Result.Error(
                AuthorizationErr()
            )
            else result
        }
    }


}

/**
 * This class is just for testing purpose.
 */
class MockApiService : ApiService {
    override suspend fun signUp(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun login(body: Map<String, Any?>): Response<LoginResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSchoolList(page: Int, limit: Int): Response<SchoolResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getVehicleList(): Response<VehicleResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(map: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun changePassword(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyOTP(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun resendUserActivation(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun userProfile(): Response<LoginResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun profileUpdate(body: Map<String, Any?>): Response<LoginResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun profileDelete(): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun merchantList(page: Int, limit: Int): Response<MerchantResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun merchantItems(
        page: Int, limit: Int, id: Int
    ): Response<MerchantItemResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun emissionDetails(duration: String): Response<EmissionResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun hivePoints(): Response<HivePointResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun redeemHivePoints(map: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun routeList(): Response<RouteResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun routeMap(routeId: String): Response<RouteMapResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun fareInfoList(): Response<FareInfoResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun feedbackList(busNumber:String): Response<FeedbackResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun feedbackCreate(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getSavedTrips(): Response<SavedTripResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addTrip(body: Map<String, Any?>): Response<AddTripResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun editSavedTrip(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(body: Map<String, Any>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun tripActivity(body: Map<String, Any?>): Response<TripActivityResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun tripLog(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun socialMediaTemplate(): Response<SocialMediaTemplateResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateNotification(body: Map<String, Any?>): Response<BaseResponse> {
        TODO("Not yet implemented")
    }


}