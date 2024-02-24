package com.cityof.glendale.injections.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.cityof.glendale.utils.AppPreferenceManager
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    fun dataStorePreference(dataStore: DataStore<Preferences>, gson: Gson): AppPreferenceManager {
        return AppPreferencesManagerImpl(dataStore, gson)
    }

    @Provides
    fun dataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appDataStore
    }
}