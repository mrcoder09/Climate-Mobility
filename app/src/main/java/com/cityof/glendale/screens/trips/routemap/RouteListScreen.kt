package com.cityof.glendale.screens.trips.routemap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.components.CardComposable
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.MockUmoApi
import com.cityof.glendale.network.UmoRepository
import com.cityof.glendale.network.umoresponses.UmoRoute
import com.cityof.glendale.network.umoresponses.toComposeColor
import com.cityof.glendale.screens.trips.RouteCircledComposable
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.utils.AppConstants
import ir.kaaveh.sdpcompose.sdp
import timber.log.Timber
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
@Preview
fun RouteMapPreview() {
    RouteListScreen(
        viewModel = RouteListViewModel(
            AppRepository(MockApiService()),
            UmoRepository(MockUmoApi()),
        )
    )
}

@Composable
fun RouteListScreen(
    navHostController: NavHostController? = null, viewModel: RouteListViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    val title = stringResource(id = R.string.routes_maps)

    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUi()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            is RouteListContract.NavAction.NavWebView -> {

                val url = (navigation as RouteListContract.NavAction.NavWebView).url
                val encodedUrl = URLEncoder.encode(
                    "${AppConstants.PDF_VIEWER_LINK}${url}", StandardCharsets.UTF_8.toString()
                )
                navHostController?.navigate("WebView/$title/$encodedUrl")
            }

            null -> {}
        }
    })


    ToastApp(state.toastMsg)
    ProgressDialogApp(state.isLoading)
    DoUnauthorization(state.isAuthErr)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
    ) {

        AppBarWithBack(
            title = title
        ) {
            navHostController?.popBackStack()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 22.sdp
                )
        ) {
            item {
                Spacer(modifier = Modifier.height(18.sdp))
                Text(
                    text = stringResource(R.string.timetables_route_maps),
                    style = baseStyle2().copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(3.sdp))
                Text(
                    text = stringResource(R.string.msg_view_detailed_timeline),
                    style = baseStyle().copy(
                        fontWeight = FontWeight.Normal
                    )
                )
                Spacer(modifier = Modifier.height(14.sdp))
            }

            items(state.routes) {
                RouteCard(it) { route ->
                    viewModel.dispatch(RouteListContract.Intent.OpenRouteMap("${route.id}"))
                }
                Spacer(modifier = Modifier.height(8.sdp))
            }


        }
    }

}


@Composable
@Preview
fun RouteCard(route: UmoRoute = UmoRoute(), onClick: (UmoRoute) -> Unit = {}) {

    CardComposable(modifier = Modifier.noRippleClickable {
        onClick(route)
    }) {

        Spacer(modifier = Modifier.height(18.sdp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.sdp)
        ) {
//            Column {
//                BasicText(
//                    text = "Route", style = baseStyle().copy(
//                        fontWeight = FontWeight.Normal, textAlign = TextAlign.Center
//                    ), modifier = Modifier.defaultMinSize(
//                        minWidth = 50.sdp
//                    )
//                )
//                BasicText(
//                    text = "${route.id}", style = baseStyle().copy(
//                        textAlign = TextAlign.Center
//                    ), modifier = Modifier.defaultMinSize(
//                        minWidth = 50.sdp
//                    )
//                )
//            }

            Timber.d("${route.id} -- ${route.color}")
            RouteCircledComposable(route.id, route.toComposeColor())
            Spacer(modifier = Modifier.width(12.sdp))
            Column {
                BasicText(
                    text = route.title ?: "", style = baseStyle().copy(
                        fontWeight = FontWeight.Normal, textDecoration = TextDecoration.Underline
                    )
                )
                Spacer(modifier = Modifier.height(6.sdp))
//                BasicText(
//                    text = "via ${route.endPointCode}", style = baseStyle().copy(
//                        fontWeight = FontWeight.Normal, fontSize = 12.ssp
//                    ), modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(
//                            end = 22.sdp
//                        )
//                )
            }

        }


//        Spacer(modifier = Modifier.height(8.sdp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(
//                    start = 64.sdp, end = 22.sdp
//                ), verticalAlignment = Alignment.CenterVertically
//        ) {
//            BasicText(
//                text = "Effective:", style = baseStyle().copy(
//                    fontWeight = FontWeight.Normal, fontSize = 12.ssp
//                )
//            )
//
//            Spacer(modifier = Modifier.width(6.sdp))
//            BasicText(
//                text = "11/15/20",
//                modifier = Modifier
//                    .background(
//                        color = Color(0xFFEFB30F), shape = RoundedCornerShape(size = 16.dp)
//                    )
//                    .padding(start = 12.sdp, top = 4.sdp, end = 12.sdp, bottom = 4.sdp),
//                style = baseStyle().copy(
//                    color = Color.White,
//                    fontWeight = FontWeight.Normal
//                )
//
//            )
//        }
        Spacer(modifier = Modifier.height(18.sdp))
    }

}


