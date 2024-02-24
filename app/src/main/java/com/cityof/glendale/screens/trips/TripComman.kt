package com.cityof.glendale.screens.trips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Popup
import com.cityof.glendale.R
import com.cityof.glendale.composables.CircleApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.data.enums.TravelMode
import com.cityof.glendale.data.enums.transitMode
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.data.fixes.getAddressLatLng
import com.cityof.glendale.network.GoogleEndPoints
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.googleresponses.getArrivalTimeStamp
import com.cityof.glendale.network.googleresponses.getDepartureTimeStamp
import com.cityof.glendale.network.googleresponses.getDistanceMeter
import com.cityof.glendale.theme.FF333333
import com.cityof.glendale.theme.FF69A251
import com.cityof.glendale.theme.FFEFB30F
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.xtFormat
import com.cityof.glendale.utils.xtJson
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber
import java.util.Calendar


fun getTripColor(
    travelMode: TravelMode
): Color {
    return when (travelMode) {
        TravelMode.Bus -> Purple
        TravelMode.Walking -> FFEFB30F
        TravelMode.Bicycling -> FF69A251
    }
}

data class FakeSavedTrips(
    val icon: Int?, val title: String?, val address: String?
)


data class FakeRoutes(
    val color: Color?, val routeName: String?, val title: String?, val fromTo: String?
)


@Composable
fun RouteCircledComposable(
    item: String?, color: Color?, size: Int = 40, fontSize: Int = 13
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        color?.let {
            CircleApp(
                size = size, color = it
            )
        }
        Text(
            text = item ?: "", style = baseStyle().copy(
                fontWeight = FontWeight.W500, color = Color.White, fontSize = fontSize.ssp
            )
        )
    }
}

@Composable
fun OptionsForDot(
    onDismiss: () -> Unit, onOption: (Option, Int) -> Unit
) {

    val items = arrayOf(
        R.drawable.ic_edit, R.drawable.ic_delete_red
    ).zip(
        stringArrayResource(id = R.array.trip_item_options)
    ) { icon, title ->
        Option(title, icon)
    }

    Popup(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .width(134.sdp)
                .padding(
                    top = 18.sdp, end = 24.sdp
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = 12.sdp
                    ), contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_solid_white),
                    contentDescription = null,
                    modifier = Modifier.shadow(
                        elevation = 4.sdp
                    )
                )
            }

            Card(
                elevation = CardDefaults.cardElevation(4.sdp),
                colors = CardDefaults.cardColors(Color.White),
                shape = RoundedCornerShape(8.sdp)
            ) {
                Spacer(modifier = Modifier.height(8.sdp))
                items.forEachIndexed { index, option ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.sdp)
                            .padding(
                                horizontal = 14.sdp
                            )
                            .noRippleClickable {
                                onOption(option, index)
                                onDismiss()
                            }) {
                        Image(
                            painter = painterResource(id = option.icon), contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.sdp))
                        Text(
                            text = option.title, style = baseStyle().copy(
                                fontSize = 14.ssp, color = FF333333
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.sdp))
            }

        }
    }
}


/**
 * {
 *   "user_id": 11,
 *   "bus_number": "B76",
 *   "route_information": "Json encoded string",
 *   "starting_point": "Glendale",
 *   "from_address": "Glendale",
 *   "from_lat": "864864748",
 *   "from_long": "-567456746747",
 *   "end_destination": "Tranport center",
 *   "to_address": "Tranport center",
 *   "to_lat": "6757858",
 *   "to_long": "-784674674",
 *   "from_time": 1222324242,
 *   "to_time": 113425553,
 *   "transport_mode": "Bus",
 *   "label": 52,
 *   "distance": 2.6
 * }
 *
 *
 */
fun tripBody(
    route: Route?,
    origin: LocationSearched?,
    destination: LocationSearched?,
    travelMode: TravelMode?,
    date: Long?,
    time: Long?,
    label: String? = ""
): Map<String, Any> {

    val map = mutableMapOf<String, Any>()

    route?.let {
        map["route_information"] = it.xtJson()
    }
    map["distance"] = route?.getDistanceMeter() ?: ""
    origin?.let {
        map["starting_point"] = it.name ?: ""
        map["from_address"] = it.address ?: ""
        map["from_lat"] = it.latLng?.latitude ?: ""
        map["from_long"] = it.latLng?.longitude ?: ""
    }

    destination?.let {
        map["end_destination"] = it.name ?: ""
        map["to_address"] = it.address ?: ""
        map["to_lat"] = it.latLng?.latitude ?: ""
        map["to_long"] = it.latLng?.longitude ?: ""
    }

    map["label"] = label ?: ""
    map["transport_mode"] = travelMode?.transitMode() ?: ""

    if (date != null) {
        map["date"] = (date/1000)
    }
    if (time != null) {
        map["time"] = (time/1000)
    }

    route?.getDepartureTimeStamp()?.let {
        if (it > 0) map["from_time"] = it
    }

    route?.getArrivalTimeStamp()?.let {
        if (it > 0) map["to_time"] = it
    }

    Timber.d("SAVE_TRIP_DATA: ${map.xtJson()}")

    return map
}


fun directionsApiBody(
    origin: LocationSearched?,
    destination: LocationSearched?,
    travelMode: TravelMode?,
    date: Long?,
    time: Long?,
): Map<String, Any?> {

    val mode = travelMode?.transitMode()
    val map = mutableMapOf<String, Any>()
    map["destination"] = "${origin?.getAddressLatLng()}"
    map["origin"] = "${destination?.getAddressLatLng()}"
    map["mode"] = mode ?: ""
    map["key"] = GoogleEndPoints.GOOGLE_API_KEY
    map["alternatives"] = true

    if (travelMode == TravelMode.Bus) { //THESE ARE OPTION FIELDS
        map["transit_mode"] = travelMode.transitMode()
        val calendar = Calendar.getInstance().apply {
            date?.let { date ->
                timeInMillis = date
            }
            if (time != null) {
                set(Calendar.HOUR_OF_DAY, time.xtFormat("HH").toInt())
                set(Calendar.MINUTE, time.xtFormat("mm").toInt())
            }
        }

        map["departure_time"] = "${calendar.timeInMillis / 1000}"
    }

    Timber.d(
        "DIRECTION_API: ${map.xtJson()}"
    )

    return map
}


fun fakeTrips(): List<FakeSavedTrips> {
    return listOf(
        FakeSavedTrips(
            icon = R.drawable.ic_circled_home,
            title = "Home",
            address = "112 E Mountain St building, Glendale, CA 91"
        ), FakeSavedTrips(
            icon = R.drawable.ic_school,
            title = "School",
            address = "Hoover High School 651 Glenwood Rd, Glendale"
        ), FakeSavedTrips(
            icon = R.drawable.ic_office,
            title = "Office",
            address = "500 N Brand Blvd, Glendale, CA 91203"
        )
    )
}


data class RouteData(
    val title: String,
    val description: String,
    val time: String?,
    val type: RouteType,
    val stoppages: List<RouteStoppage> = emptyList()
)


data class RouteStoppage(
    val title: String?, val time: String?
)

enum class RouteType {
    ORIGIN_ROUTE,

    //    WALKING,
//    ORIGIN_BUS,
//    ORIGIN_BUS_SUB_STEPS,
//    END_BUS,
    END_ROUTE
}

fun fakeRouteList() = listOf(
    RouteData("W Milford St", "Glendale, CA 91203, USA", "4:20 AM", RouteType.ORIGIN_ROUTE),
//    RouteData("Walk", "About 1 min, 374 ft", null, RouteType.WALKING),
//    RouteData("Pacific / Lexington", "", "4:32 AM", RouteType.ORIGIN_BUS),
//    RouteData("Pacific Park", "5 min (9 stops)", null, RouteType.ORIGIN_BUS_SUB_STEPS, listOf(
//        RouteStoppage("Glenoaks / Central", "4:33 AM"),
//        RouteStoppage("Brand / Glenoaks", "4:34 AM"),
//        RouteStoppage("Brand at Monterey", "4:34 AM"),
//        RouteStoppage("Brand / Doran", "4:35 AM"),
//        RouteStoppage("Brand at Lexington", "4:36 AM"),
//        RouteStoppage("Brand at Broadway", "4:36 AM"),
//        RouteStoppage("Brand / Colorado", "4:47 AM"),
//        RouteStoppage("Brand / Colorado", "4:49 AM"),
//        RouteStoppage("Brand / Lomita", "4:41 AM")
//    )),
//    RouteData("Central at Windsor", "", "4:42 AM", RouteType.END_BUS),
//    RouteData("Walk", "About 3 min, 0.2 mi", null, RouteType.WALKING),
    RouteData("W Windsor Rd", "Glendale, CA 91204, USA", "4:50 AM", RouteType.END_ROUTE)
)
