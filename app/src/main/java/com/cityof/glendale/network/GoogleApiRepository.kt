package com.cityof.glendale.network

import com.cityof.glendale.network.googleresponses.DirectionResponse
import com.cityof.glendale.network.googleresponses.PlaceDetailResponse
import com.cityof.glendale.network.googleresponses.PlaceSuggestion
import com.cityof.glendale.network.responses.Result
import com.cityof.glendale.network.responses.safeApiCall
import com.cityof.glendale.network.responses.toResult2
import retrofit2.Response
import javax.inject.Inject

class GoogleApiRepository @Inject constructor(
    private val apiService: GoogleApiService
) {

    /**
     * destination:Magnolia, 1501 N Victory Pl, Burbank, CA 91502, United States
     * origin:W Dryden St, Glendale, CA 91202, USA
     * mode:walking
     * key:AIzaSyBoqY_DrBsMotSEOc2SRTfzFgiCRxs5mQ4
     * transit_mode:Walking
     * alternatives:true
     */
    suspend fun directions(map: Map<String, Any>): Result<DirectionResponse> {
      return safeApiCall {
            apiService.directions(map).toResult2()
        }
    }

    suspend fun placeDetails(map: Map<String, Any>): Result<PlaceDetailResponse> {
        return safeApiCall {
            apiService.placesDetails(map).toResult2()
        }
    }


    suspend fun placeSuggestions(map: Map<String, Any>): Result<PlaceSuggestion>{
        return safeApiCall {
            apiService.placeSuggestion(map).toResult2()
        }
    }


}


class MockGoogleApiRepo: GoogleApiService{
    override suspend fun directions(map: Map<String, Any>): Response<DirectionResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun placeSuggestion(map: Map<String, Any>): Response<PlaceSuggestion> {
        TODO("Not yet implemented")
    }

    override suspend fun placesDetails(map: Map<String, Any>): Response<PlaceDetailResponse> {
        TODO("Not yet implemented")
    }

}