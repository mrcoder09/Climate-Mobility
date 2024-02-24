package com.cityof.glendale.network


import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
            val msg = handleServerException(e)
            Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(999).message(msg)
                .body("{${e}}".toResponseBody(null)).build()
        }
    }


    private fun handleServerException(e: Exception): String {
        return when (e) {
            is SocketTimeoutException -> {
                "Timeout - Please check your internet connection"
            }

            is UnknownHostException -> {
                "Unable to make a connection. Please check your internet"
            }

            is ConnectionShutdownException -> {
                "Connection shutdown. Please check your internet"
            }

            is IOException -> {
                "Server is unreachable, please try again later."
            }

            is IllegalStateException -> {
                "${e.message}"
            }

            else -> {
                "${e.message}"
            }
        }
    }
}