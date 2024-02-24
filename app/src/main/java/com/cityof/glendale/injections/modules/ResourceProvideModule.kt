package com.cityof.glendale.injections.modules

import android.content.Context
import com.cityof.glendale.utils.ResourceProvider
import com.cityof.glendale.utils.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class ResourceProvideModule {

    @ViewModelScoped
    @Provides
    fun resourceProvider(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }
}