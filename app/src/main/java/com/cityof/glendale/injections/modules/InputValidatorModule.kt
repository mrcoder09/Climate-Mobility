package com.cityof.glendale.injections.modules

import com.cityof.glendale.utils.InputValidator
import com.cityof.glendale.utils.InputValidatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class InputValidatorModule {
    @Provides
    fun inputValidator(): InputValidator {
        return InputValidatorImpl()
    }
}