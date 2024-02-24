package com.cityof.glendale.injections.modules

import android.content.Context
import com.cityof.glendale.BuildConfig
import com.cityof.glendale.network.ApiService
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.ErrorInterceptor
import com.cityof.glendale.network.Headers
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.LangHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Provides
    fun ApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    fun provideRepository(apiService: ApiService) = AppRepository(apiService)

    @Provides
    fun retrofit(
        okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory).client(okHttpClient).build()
    }

    @Provides
    fun okHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        preferenceManager: AppPreferenceManager,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val credentials = Credentials.basic(Headers.USER_NAME_VALUE, Headers.PASSWORD_VALUE)
        val okHttpClient = OkHttpClient().newBuilder().cache(cache).addInterceptor { chain ->
            val request = chain.request().newBuilder().apply {
                header(Headers.AUTHORIZATION, credentials)
                header(Headers.ACCEPT_LANGUAGE, LangHelper.getServerLocale())
                val token: String? = runBlocking {
                    preferenceManager.token.firstOrNull()
                }
                header(Headers.BEARER_TOKEN, "${Headers.BEARER} ${token ?: ""}")

            }.build()
            chain.proceed(request)
        }.addInterceptor(errorInterceptor).configureTimeout()
            .addInterceptorsIfDebug(httpLoggingInterceptor).build()
        return okHttpClient
    }

    @Provides
    fun httpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)


    @Provides
    fun gson(): Gson = GsonBuilder().create()


    @Provides
    fun httpCache(file: File) = Cache(file, 25 * 1024 * 1024)

    @Provides
    fun errorInterceptor() = ErrorInterceptor()


    @Provides
    fun cacheFile(@ApplicationContext context: Context) = File(context.cacheDir, "http-cache").let {
        if (it.exists().not()) {
            it.mkdir()
        }
        it
    }
}


/**
 * Extension for add interceptors setting in OkhttpClient
 * @author Satnam Singh
 * @param interceptors interceptors of different type
 */
fun OkHttpClient.Builder.addInterceptorsIfDebug(vararg interceptors: Interceptor) =
    this.apply {
        if (BuildConfig.DEBUG) {
            interceptors.forEach {
                addInterceptor(it)
            }
        }
    }

/**
 * Extension for simplifying timeout setting in OkhttpClient
 * @author Satnam Singh
 * @param timeout readTimeout and connectTimeout in seconds
 */
fun OkHttpClient.Builder.configureTimeout(timeout: Long = 1 * 60) = this.apply {
    readTimeout(timeout, TimeUnit.SECONDS)
    connectTimeout(timeout, TimeUnit.SECONDS)
    writeTimeout(timeout, TimeUnit.SECONDS)
}