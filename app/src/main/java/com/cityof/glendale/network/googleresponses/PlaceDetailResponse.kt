package com.cityof.glendale.network.googleresponses


import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(
    @SerializedName("html_attributions")
    val htmlAttributions: List<Any?>? = null,
    @SerializedName("result")
    val placeDetails: PlaceDetails? = null,
    @SerializedName("status")
    val status: String? = null
)


data class PlaceLatLng(
    @SerializedName("lat")
    val lat: Double? = null,
    @SerializedName("lng")
    val lng: Double? = null
)

data class Viewport(
    @SerializedName("northeast")
    val northeast: PlaceLatLng? = null,
    @SerializedName("southwest")
    val placeLatLng: PlaceLatLng? = null
)


data class PlaceDetails(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent?>? = null,
    @SerializedName("adr_address")
    val adrAddress: String? = null,
    @SerializedName("formatted_address")
    val formattedAddress: String? = null,
    @SerializedName("geometry")
    val geometry: Geometry? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("icon_background_color")
    val iconBackgroundColor: String? = null,
    @SerializedName("icon_mask_base_uri")
    val iconMaskBaseUri: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("photos")
    val photos: List<Photo?>? = null,
    @SerializedName("place_id")
    val placeId: String? = null,
    @SerializedName("reference")
    val reference: String? = null,
    @SerializedName("types")
    val types: List<String?>? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("utc_offset")
    val utcOffset: Int? = null,
    @SerializedName("vicinity")
    val vicinity: String? = null
)



data class Geometry(
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("viewport")
    val viewport: Viewport? = null
)

data class Photo(
    @SerializedName("height")
    val height: Int? = null,
    @SerializedName("html_attributions")
    val htmlAttributions: List<String?>? = null,
    @SerializedName("photo_reference")
    val photoReference: String? = null,
    @SerializedName("width")
    val width: Int? = null
)

data class AddressComponent(
    @SerializedName("long_name")
    val longName: String? = null,
    @SerializedName("short_name")
    val shortName: String? = null,
    @SerializedName("types")
    val types: List<String?>? = null
)

