package com.cityof.glendale.network

import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.UmoVehicle
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
@JvmSuppressWildcards
interface UmoApiService {
    @GET(UmoEndPoints.ROUTE_LIST)
    suspend fun routeList(): Response<List<UmoRoute>>

    @GET(UmoEndPoints.VEHICLE_LIST)
    suspend fun vehicles(): Response<List<UmoVehicle>>

    @GET(UmoEndPoints.VEHICLE_LIST_ON_ROUTE)
    suspend fun vehiclesOnRoute(
        @Path("agency") agency:String = "glendale",
        @Path("route") routeId:String,
    ): Response<List<UmoVehicle>>
}


