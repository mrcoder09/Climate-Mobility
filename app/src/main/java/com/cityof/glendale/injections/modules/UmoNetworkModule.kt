package com.cityof.glendale.injections.modules

import com.cityof.glendale.BuildConfig
import com.cityof.glendale.network.ErrorInterceptor
import com.cityof.glendale.network.Headers
import com.cityof.glendale.network.UmoApiService
import com.cityof.glendale.network.UmoRepository
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

const val namedRetrofit = "retrofit_for_umo"
const val namedOkHttpClient = "okhttp_for_umo"

@Module
@InstallIn(SingletonComponent::class)
class UmoNetworkModule {


    @Provides
    fun umoApiService(
        @Named(namedRetrofit) retrofit: Retrofit
    ) = retrofit.create(UmoApiService::class.java)

    @Provides
    fun umoRepository(apiService: UmoApiService) = UmoRepository(apiService)

    @Provides
    @Named(namedRetrofit)
    fun retrofitWithUmoConfig(
      @Named(namedOkHttpClient) okHttpClient: OkHttpClient, gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL_UMO).client(okHttpClient)
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Named(namedOkHttpClient)
    fun okhttpForUmo(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        errorInterceptor: ErrorInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder().cache(cache).addInterceptor { chain ->
            val request = chain.request().newBuilder().apply {
                header(Headers.KEY, BuildConfig.UMO_API_KEY)
            }.build()
            chain.proceed(request)
        }.addInterceptor(errorInterceptor).configureTimeout()
            .addInterceptorsIfDebug(httpLoggingInterceptor).build()
        return okHttpClient
    }
}