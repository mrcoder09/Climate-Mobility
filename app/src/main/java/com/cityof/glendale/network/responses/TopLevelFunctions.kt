package com.cityof.glendale.network.responses

import com.cityof.glendale.network.CustomCodes
import retrofit2.Response
import timber.log.Timber

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val err: Throwable) : Result<Nothing>()
}

class AuthorizationErr : Exception("Authorization Error")

fun <T : Any> Response<T>.toResult(): Result<T> {
    return body()?.let {
        Result.Success(it)
    } ?: kotlin.run {
        Timber.d("${this.code()}")
        Result.Error(Throwable(this.message()))
    }
}

fun <T : Any> Response<T>.toResult2(): Result<T> {

    return if (isSuccess(code())) {
        body()?.let {
            Result.Success(it)
        } ?: kotlin.run {
            Result.Error(Throwable(this.message()))
        }
    } else {
        val err = errorBody()?.string() ?: "UNKNOWN ERROR!"
        Result.Error(Throwable(err))
    }
}

suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> = try {
    call.invoke()
} catch (e: Exception) {
    e.printStackTrace()
    Result.Error(e)
}

// Extension function to map a Result<T> to another Result<R>
inline fun <T : Any, R : Any> Result<T>.map(transform: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> Result.Success(transform(data))
        is Result.Error -> this
    }
}

// Extension function to handle success cases of Result<T>
inline fun <T : Any> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(data)
    }
    return this
}

// Extension function to handle error cases of Result<T>
inline fun <T : Any> Result<T>.onError(action: (Throwable) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(err)
    }
    return this
}

// Extension function to handle both success and error cases of Result<T>
inline fun <T : Any> Result<T>.onComplete(action: (Result<T>) -> Unit): Result<T> {
    action(this)
    return this
}

fun isSuccess(customCode: Int?) = (customCode == CustomCodes.SUCCESS)
fun isInvalidData(code: Int?) = (code == CustomCodes.INVALID_DATA)
fun isAuthorizationErr(code: Int?) = (code == CustomCodes.AUTHORIZATION_ERR)
fun isServerErr(code: Int?) = (code == CustomCodes.SERVER_ERR)

fun isUmoError(code: Int) =
    (isInvalidData(code) || isAuthorizationErr(code) || (code == CustomCodes.RESOURCE_NOT_FOUND) || (code == CustomCodes.RESOURCE_GONE) || (code == CustomCodes.SERVICE_UNAVAILABLE))