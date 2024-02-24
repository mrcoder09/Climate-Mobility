package com.cityof.glendale.network.googleresponses


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TransitDetail(
    @SerializedName("arrival_stop")
    val arrivalStop: ArrivalStop? = ArrivalStop(),
    @SerializedName("arrival_time")
    val arrivalTime: ArrivalTime? = ArrivalTime(),
    @SerializedName("departure_stop")
    val departureStop: DepartureStop? = DepartureStop(),
    @SerializedName("departure_time")
    val departureTime: DepartureTime? = DepartureTime(),
    @SerializedName("headsign")
    val headsign: String? = "",
    @SerializedName("line")
    val line: Line? = Line(),
    @SerializedName("num_stops")
    val numStops: Int? = 0
): Parcelable

@Parcelize
data class Vehicle(
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("type")
    val type: String? = null
): Parcelable

@Parcelize
data class Line(
    @SerializedName("agencies")
    val agencies: List<Agency?>? = null,
    @SerializedName("short_name")
    val shortName: String? = null,
    @SerializedName("vehicle")
    val vehicle: Vehicle? = null
): Parcelable
@Parcelize
data class DepartureTime(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("time_zone")
    val timeZone: String? = null,
    @SerializedName("value")
    val value: Int? = null
): Parcelable
@Parcelize
data class DepartureStop(
    @SerializedName("location")
    val location: Location? = Location(),
    @SerializedName("name")
    val name: String? = ""
): Parcelable

@Parcelize
data class ArrivalTime(
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("time_zone")
    val timeZone: String? = null,
    @SerializedName("value")
    val value: Long? = null
): Parcelable
@Parcelize
data class ArrivalStop(
    @SerializedName("location")
    val location: Location? = null,
    @SerializedName("name")
    val name: String? = null
): Parcelable
@Parcelize
data class Agency(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("url")
    val url: String? = null
): Parcelable