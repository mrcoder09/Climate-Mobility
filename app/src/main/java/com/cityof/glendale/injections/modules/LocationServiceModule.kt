package com.cityof.glendale.injections.modules

import android.content.Context
import com.cityof.glendale.utils.ILocationService
import com.cityof.glendale.utils.LocationService
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocationServiceModule {

    @Singleton
    @Provides
    fun provideLocationClient(
        @ApplicationContext context: Context
    ): ILocationService = LocationService(
        context, LocationServices.getFusedLocationProviderClient(context)
    )
}