package com.cityof.glendale.network

import okhttp3.Interceptor
import okhttp3.Response


class AuthInterceptor(private val onUnauthorized: () -> Unit = {}) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        try {
            val body = response.body
            val code = response.code
//            response.body?.string()?.let {
//                val gson = Gson()
//                val baseResponse = gson.fromJson(it, BaseResponse::class.java)
//                if (isAuthorizationErr(baseResponse.customCode)){
//                    BaseApp.mutableSharedFlow.tryEmit(true)
//                }
//
//                Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(code)
//                    .body("{${e}}".toResponseBody(null)).build()
//            }
        } catch (e:Exception){

        }

        return response
    }
}


