package com.cityof.glendale.injections.modules

import com.cityof.glendale.network.ErrorInterceptor
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.GoogleApiService
import com.cityof.glendale.network.GoogleEndPoints
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

const val namedGoogleRetrofit = "retrofit_for_google"
const val namedGoogleOkHttpClient = "okhttp_for_google"

@Module
@InstallIn(SingletonComponent::class)
class GoogleNetworkModule {


    @Provides
    fun googleApiService(
        @Named(namedGoogleRetrofit) retrofit: Retrofit
    ) = retrofit.create(GoogleApiService::class.java)

    @Provides
    fun googleRepository(apiService: GoogleApiService) = GoogleApiRepository(apiService)

    @Provides
    @Named(namedGoogleRetrofit)
    fun retrofitGoogle(
        @Named(namedGoogleOkHttpClient) okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(
            GoogleEndPoints.BASE_URL
        ).client(okHttpClient).addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Named(namedGoogleOkHttpClient)
    fun okhttpGoogle(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder().cache(cache).addInterceptor { chain ->
            val request = chain.request().newBuilder().apply {

            }.build()
            chain.proceed(request)
        }.addInterceptor(errorInterceptor).configureTimeout()
            .addInterceptorsIfDebug(httpLoggingInterceptor).build()
        return okHttpClient
    }
}