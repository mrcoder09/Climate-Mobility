package com.cityof.glendale.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource

//val Genders = R.array.gender_array
//val Groups = R.array.school_names
//val Vehicles = R.array.vehicle_types



@Composable
fun ResourceArrayAsList(arr: Int) =
    stringArrayResource(id = arr).toList()

fun merge2Arr(){

}

fun mergedArr(){

}
