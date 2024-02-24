package com.cityof.glendale.screens.forgotpwd

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import ir.kaaveh.sdpcompose.ssp


object ForgotPwdStyles{

    @Composable
    fun titleStyle(): TextStyle{
        return  baseStyleLarge().copy(fontSize = 24.ssp, lineHeight = 30.ssp)
    }

    @Composable
    fun descStyle(): TextStyle{
        return baseStyle().copy(fontSize = 16.ssp, lineHeight = 22.ssp)
    }


}