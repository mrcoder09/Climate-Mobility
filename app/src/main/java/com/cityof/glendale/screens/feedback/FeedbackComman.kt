package com.cityof.glendale.screens.feedback

import com.cityof.glendale.composables.UIStr


data class Survey(
    val question: UIStr = UIStr.Str(""),
    val answers: List<SurveyOption> = emptyList(),
    val moreOption: List<SurveyOption> = emptyList()
)

data class SurveyOption(
    val option: UIStr = UIStr.Str(""),
    val value: String = "",
    val isSelected: Boolean = false
)


//data class Survey(
//    val question: String = "",
//    val answers: List<SurveyOption> = emptyList(),
//    val moreOption: List<SurveyOption> = emptyList()
//)
//
//data class SurveyOption(
//    val option: String = "",
//    val isSelected: Boolean = false
//)

