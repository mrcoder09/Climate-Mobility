package com.cityof.glendale.network

import com.cityof.glendale.network.googleresponses.DirectionResponse
import com.cityof.glendale.network.googleresponses.PlaceDetailResponse
import com.cityof.glendale.network.googleresponses.PlaceSuggestion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
@JvmSuppressWildcards
interface GoogleApiService {


    @GET(GoogleEndPoints.DIRECTION)
    suspend fun directions(
        @QueryMap map: Map<String, Any>
    ): Response<DirectionResponse>



    @GET(GoogleEndPoints.PLACE_API)
    suspend fun placeSuggestion(
        @QueryMap map: Map<String, Any>
    ): Response<PlaceSuggestion>


    @GET(GoogleEndPoints.PLACE_DETAIL)
    suspend fun placesDetails(
        @QueryMap map: Map<String, Any>
    ): Response<PlaceDetailResponse>

}