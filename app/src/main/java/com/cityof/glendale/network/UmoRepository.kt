package com.cityof.glendale.network

import com.cityof.glendale.network.responses.Result
import com.cityof.glendale.network.responses.safeApiCall
import com.cityof.glendale.network.responses.toResult2
import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.UmoVehicle
import retrofit2.Response
import javax.inject.Inject

class UmoRepository @Inject constructor(
    private val apiService: UmoApiService
) {


    suspend fun routeList(): Result<List<UmoRoute>> {
        return safeApiCall {
            apiService.routeList().toResult2()
        }
    }

    suspend fun vehiclesOnRoute(routeId: String): Result<List<UmoVehicle>> {
        return safeApiCall {
            apiService.vehiclesOnRoute(routeId = routeId).toResult2()
        }
    }

}


class MockUmoApi : UmoApiService {
    override suspend fun routeList(): Response<List<UmoRoute>> {
        TODO("Not yet implemented")
    }

    override suspend fun vehicles(): Response<List<UmoVehicle>> {
        TODO("Not yet implemented")
    }

    override suspend fun vehiclesOnRoute(
        agency: String, routeId: String
    ): Response<List<UmoVehicle>> {
        TODO("Not yet implemented")
    }

}