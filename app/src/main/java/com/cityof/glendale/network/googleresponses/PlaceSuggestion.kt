package com.cityof.glendale.network.googleresponses


import com.google.gson.annotations.SerializedName

data class PlaceSuggestion(
    @SerializedName("predictions")
    val placePredictions: List<PlacePrediction>? = null,
    @SerializedName("status")
    val status: String? = null
)

data class PlacePrediction(
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("matched_substrings")
    val matchedSubstrings: List<MatchedSubstring?>? = null,
    @SerializedName("place_id")
    val placeId: String? = null,
    @SerializedName("reference")
    val reference: String? = null,
    @SerializedName("structured_formatting")
    val structuredFormatting: StructuredFormatting? = null,
    @SerializedName("terms")
    val terms: List<Term?>? = null,
    @SerializedName("types")
    val types: List<String?>? = null,
)

data class MatchedSubstring(
    @SerializedName("length")
    val length: Int? = null,
    @SerializedName("offset")
    val offset: Int? = null
)
data class StructuredFormatting(
    @SerializedName("main_text")
    val mainText: String? = null,
    @SerializedName("main_text_matched_substrings")
    val mainTextMatchedSubstrings: List<MatchedSubstring?>? = null,
    @SerializedName("secondary_text")
    val secondaryText: String? = null
)


data class Term(
    @SerializedName("offset")
    val offset: Int? = null,
    @SerializedName("value")
    val value: String? = null
)