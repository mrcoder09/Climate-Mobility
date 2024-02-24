package com.cityof.glendale.data.enums

import androidx.compose.runtime.Composable
import com.cityof.glendale.R
import com.cityof.glendale.utils.ResourceArrayAsList


enum class Genders {
    NONE, MALE, FEMALE, PREFER_NOT_TO_SAY
}

data class Gender(
    val name: String, val type: Genders
)

fun Gender.toId(): Int {
    return when (type) {
        Genders.NONE -> -1
        Genders.MALE -> 1
        Genders.FEMALE -> 2
        Genders.PREFER_NOT_TO_SAY -> 0
    }
}

fun idToGenderType(id: Int): Genders {
    return when (id) {
        1 -> Genders.MALE
        2 -> Genders.FEMALE
        0 -> Genders.PREFER_NOT_TO_SAY
        else -> Genders.NONE
    }
}

fun idToGender(id: Int): String {
    return when (id) {
        1 -> "Male"
        2 -> "Female"
        0 -> "Prefer Not to Say"
        else -> ""
    }
}


@Composable
fun getGenders(): List<Gender> {
    return ResourceArrayAsList(arr = R.array.gender_array).zip(
        arrayOf(
            Genders.NONE, Genders.MALE, Genders.FEMALE, Genders.PREFER_NOT_TO_SAY
        )
    ) { gender, type ->
        Gender(
            name = gender, type = type
        )
    }
}



