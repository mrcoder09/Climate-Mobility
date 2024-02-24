package com.cityof.glendale.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithGlobe
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.UIStr
import com.cityof.glendale.composables.components.AnnotatedClickableText
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.isGroupEmission
import com.cityof.glendale.network.responses.isNoActivity
import com.cityof.glendale.screens.home.HomeContract.HomeScreenDimens
import com.cityof.glendale.screens.home.HomeContract.Intent
import com.cityof.glendale.screens.home.HomeContract.NavAction
import com.cityof.glendale.screens.home.HomeContract.State
import com.cityof.glendale.theme.ERR_RED
import com.cityof.glendale.theme.FF121212
import com.cityof.glendale.theme.FF228D00
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.theme.RobotoFontFamily
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.xt2Digit
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        viewModel = HomeViewModel(
            appRepository = AppRepository(MockApiService()),
            preferenceManager = AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}

@Composable
fun HomeScreen(
    navHostController: NavHostController? = null,
    viewModel: HomeViewModel = hiltViewModel(),
    onCross: (SavedTrip?) -> Unit = {}
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)


    LaunchedEffect(key1 = Unit, block = {
        viewModel.initUI()
    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.NavEmission -> navHostController?.navigate(Routes.VehicleEmission.name)
            null -> {}
        }
    })


    if (state.showDialog) {
        EmissionDialog(state) {
            viewModel.dispatch(Intent.ShowDialog(false))
            onCross(state.savedTrip)
        }
    }

    DoUnauthorization(state.isAuthErr, navHostController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FFF9F9F9)
            .padding(
                bottom = 12.sdp
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

//        Text(
//            text = "Home", modifier = Modifier
//                .fillMaxWidth()
//                .background(Purple)
//                .padding(
//                    top = 22.sdp,
//                ), style = baseStyle2().copy(
//                color = Color.White,
//                textAlign = TextAlign.Center,
//                fontSize = 15.ssp,
//                lineHeight = 22.ssp
//            )
//        )
        AppBarWithGlobe(
            modifier = Modifier.padding(
                start = 12.sdp
//                end = 74.sdp
            )
        )
        HomeHeader(UIStr.Str(stringResource(id = R.string.welcom_user, state.userName)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    bottom = 12.sdp
                ), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(HomeScreenDimens.CARD_CONTENT_PADDING.sdp))
            Image(
                painter = painterResource(id = R.drawable.globe_with_bee),
                contentDescription = null,
                modifier = Modifier.size(95.dp)
            )


            HomeCardView {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = HomeScreenDimens.CARD_CONTENT_PADDING.sdp
                        )
                ) {
                    HomeCardHeader(
                        msg = UIStr.ResStr(R.string.emissions_reduced), style = baseStyle2().copy(
                            fontSize = 16.ssp,
                            lineHeight = 22.ssp,
                            fontWeight = FontWeight.W500,
                            color = Purple,
                            textAlign = TextAlign.Center
                        )
                    )
                    CustomTabs(state) { index ->
                        viewModel.dispatch(
                            Intent.LoadEmission(
                                duration = if (index == 0) ""
                                else "30"
                            )
                        )

                    }
                }
                HorizontalDivider(
                    thickness = 0.5.dp
                )

                Column(
                    modifier = Modifier
                        .background(
                            color = Color.White //FF4B2E85
                        )
//                    .border(BorderStroke(1.dp, FFA2AAAD), shape = RoundedCornerShape(8.sdp))
                        .padding(
                            horizontal = 20.sdp, vertical = 22.sdp
                        ), horizontalAlignment = Alignment.Start
                ) {
                    HomeCardHeader(
                        msg = UIStr.Str(stringResource(R.string.mission)),
                        style = baseStyle2().copy(
                            color = Color.Black,
                            fontSize = 16.ssp,
                            lineHeight = 21.ssp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.W700
                        )
                    )
                    Spacer(modifier = Modifier.height(4.sdp))
                    Text(
                        text = stringResource(R.string.msg_home_help_lower_emission),
                        style = baseStyle2().copy(
                            color = Color.Black, textAlign = TextAlign.Justify
                        )
                    )
                }
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(
//                        top = (HORIZONTAL_SPACING - 6).sdp, bottom = HORIZONTAL_SPACING.sdp
//                    )
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_bee), contentDescription = null
//                )
//                Spacer(modifier = Modifier.height(4.sdp))
//                Text(
//                    text = "Thank you for reducing pollution!", style = baseStyle().copy()
//                )
//            }
            }

            HomeCardView {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = HomeScreenDimens.CARD_CONTENT_PADDING.sdp
                        )
                ) {
                    HomeCardHeader(msg = UIStr.ResStr(R.string.about_emissions))
                    Spacer(modifier = Modifier.height(6.sdp))
                    Text(
                        text = stringResource(R.string.did_you_know), style = baseStyle2().copy(
                            fontWeight = FontWeight.W500, color = FF121212
                        )
                    )
                    Spacer(modifier = Modifier.height(4.sdp))
                    AnnotatedClickableText(
                        text = stringResource(R.string.msg_average_gasoline),
                        clickableText = stringResource(R.string.learn_more),
                        maxLines = 4,
                        normalSpanStyle = SpanStyle(
                            fontSize = 14.ssp,
                            fontWeight = FontWeight.W400,
                            fontFamily = RobotoFontFamily,
                            color = Color.Black
                        ),
                        clickSpanStyle = SpanStyle(
                            fontSize = 14.ssp,
                            fontWeight = FontWeight.W500,
                            fontFamily = RobotoFontFamily,
                            color = ERR_RED
                        ),
                        paragraphStyle = ParagraphStyle(
                            textAlign = TextAlign.Justify
                        )
                    ) {
                        viewModel.dispatch(Intent.LearnMoreClicked)
                    }
                }
            }

            HomeCardView {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            all = HomeScreenDimens.CARD_CONTENT_PADDING.sdp
                        )
                ) {
                    HomeCardHeader(msg = UIStr.ResStr(R.string.how_to_earn_points))
                    Spacer(modifier = Modifier.height(6.sdp))
                    Text(
                        text = stringResource(R.string.every_10_trips_completed_through_the_app_will_earn_you_100_points),
                        style = baseStyle2().copy(
                            color = Color.Black, textAlign = TextAlign.Justify
                        )
                    )
                }
            }

        }
    }

}

@Composable
//@Preview
fun HomeHeader(userName: UIStr) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                horizontal = HomeScreenDimens.SPACE_HORIZONTAL.sdp
            )
    ) {

//        Spacer(modifier = Modifier.height(12.sdp))

        HomeCardHeader(
            msg = userName, style = baseStyle2().copy(
                color = Purple,
                fontSize = 19.ssp,
                textAlign = TextAlign.Center,
                lineHeight = 26.ssp,
                fontWeight = FontWeight.Medium
            )
        )
        Spacer(modifier = Modifier.padding(top = 12.sdp))
//        Column(
//            modifier = Modifier
//                .background(
//                    color = FF4B2E85
//                )
//                .border(BorderStroke(1.dp, FFA2AAAD), shape = RoundedCornerShape(8.sdp))
//                .padding(
//                    horizontal = HomeScreenDimens.CARD_CONTENT_PADDING.sdp, vertical = 22.sdp
//                ), horizontalAlignment = Alignment.Start
//        ) {
//            HomeCardHeader(
//                msg = UIStr.Str("Mission"), style = baseStyle2().copy(
//                    color = Color.White,
//                    fontSize = 16.ssp,
//                    lineHeight = 21.ssp,
//                    textAlign = TextAlign.Center,
//                    fontWeight = FontWeight.W700
//                )
//            )
//            Spacer(modifier = Modifier.height(4.sdp))
//            Text(
//                text = stringResource(R.string.msg_home_help_lower_emission),
//                style = baseStyle2().copy(
//                    color = Color.White, textAlign = TextAlign.Justify
//                )
//            )
//        }
//        Spacer(modifier = Modifier.height(20.sdp))
    }
}


@Composable
fun HomeCardHeader(
    msg: UIStr, style: TextStyle = baseStyle2().copy(
        fontSize = 16.ssp,
        lineHeight = 22.ssp,
        fontWeight = FontWeight.W500,
        color = Color.Black,
        textAlign = TextAlign.Center
    )
) {
    Text(
        text = msg.toStr(), style = style, modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CustomTabs(state: State, onIndexChange: (Int) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val list = listOf(stringResource(R.string.lifetime), stringResource(R.string.this_month))

    Column {
        TabRow(selectedTabIndex = selectedIndex,
            containerColor = Color.White,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 8.sdp)
                .clip(RoundedCornerShape(50)),
            divider = { Box {} },
            indicator = { tabPositions: List<TabPosition> ->
                Box {}
            }) {
            list.forEachIndexed { index, text ->
                val selected = selectedIndex == index
                Tab(modifier = if (selected) Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        FFEFE8FB
                    )
                else Modifier.clip(RoundedCornerShape(50)), selected = selected, onClick = {
                    Timber.d("$selected aksjdf")
                    selectedIndex = index
                    onIndexChange(selectedIndex)
                }, text = {
                    Text(
                        text = text, style = baseStyle2().copy(
                            color = Purple,//if (selected) Color.White else Purple,
                            fontSize = 14.ssp,
                            lineHeight = 20.ssp,
                            fontWeight = if (selected) FontWeight.W700 else FontWeight.W400
                        ), maxLines = 1
                    )
                })
            }
        }
        when (selectedIndex) {
            0 -> {
                LifeTimeComposable(state)
                Spacer(modifier = Modifier.height(12.sdp))
            }

            1 -> {
                ThisMonthComposable(state)
                Spacer(modifier = Modifier.height(2.sdp))
            }
        }

    }
}

@Composable
fun ThisMonthComposable(state: State) {

    val emission = state.monthEmission

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = 100.sdp
            )
    ) {

        if (state.isLoading.not()){
            if (emission?.isNoActivity() == true){
                NoActivityComposable()
            } else{
                Column {
                    EmissionComposable(
                        title = UIStr.Str(state.userName),
                        value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    if (emission?.isGroupEmission() == true)  EmissionComposable(
                        title = UIStr.Str(state.group),
                        value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    EmissionComposable(
                        title = UIStr.ResStr(R.string.community),
                        value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    EmissionComposable(
                        title = UIStr.ResStr(R.string.climate_mobility),
                        value = UIStr.Str(emission?.availablePoints?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.hive_points)
                    )
                }
            }
        }

//        if (state.isLoading.not()) Column {
//            EmissionComposable(
//                title = UIStr.Str(state.userName),
//                value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            if (emission?.isGroupEmission() == true) EmissionComposable(
//                title = UIStr.Str(state.group),
//                value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            EmissionComposable(
//                title = UIStr.ResStr(R.string.community),
//                value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            EmissionComposable(
//                title = UIStr.ResStr(R.string.climate_mobility),
//                value = UIStr.Str(state.hivePoint),
//                desc = UIStr.ResStr(R.string.hive_points)
//            )
//        }

        if (state.isLoading) CircularProgressIndicator()
    }
}


@Composable
fun LifeTimeComposable(
    state: State
) {

    val emission = state.lifeTimeEmission

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(
                minHeight = 100.sdp
            )
    ) {

        if (state.isLoading.not()){
            if (emission?.isNoActivity() == true){
                NoActivityComposable()
            } else{
                Column {
                    EmissionComposable(
                        title = UIStr.Str(state.userName),
                        value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    if (emission?.isGroupEmission() == true)  EmissionComposable(
                        title = UIStr.Str(state.group),
                        value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    EmissionComposable(
                        title = UIStr.ResStr(R.string.community),
                        value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.gram_of_co)
                    )
                    EmissionComposable(
                        title = UIStr.ResStr(R.string.climate_mobility),
                        value = UIStr.Str(emission?.availablePoints?.xt2Digit() ?: ""),
                        desc = UIStr.ResStr(R.string.hive_points)
                    )
                }
            }
        }

//        if (state.isLoading.not()) Column {
//            EmissionComposable(
//                title = UIStr.Str(state.userName),
//                value = UIStr.Str(emission?.personalEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            if (emission?.isGroupEmission() == true)  EmissionComposable(
//                title = UIStr.Str(state.group),
//                value = UIStr.Str(emission.groupEmission?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            EmissionComposable(
//                title = UIStr.ResStr(R.string.community),
//                value = UIStr.Str(emission?.communityEmmision?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.gram_of_co)
//            )
//            EmissionComposable(
//                title = UIStr.ResStr(R.string.climate_mobility),
//                value = UIStr.Str(emission?.availablePoints?.xt2Digit() ?: ""),
//                desc = UIStr.ResStr(R.string.hive_points)
//            )
//        }

        if (state.isLoading) CircularProgressIndicator()
    }


}

@Composable
@Preview
fun NoActivityComposable() {
    Text(text = stringResource(R.string.msg_start_trip_to_see),
        style = baseStyle2(),
        textAlign = TextAlign.Center)
}

@Composable
fun EmissionComposable(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(
            top = 10.sdp
        ), title: UIStr, value: UIStr, desc: UIStr
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title.toStr(), style = baseStyle2().copy(
                color = Color.Black, fontSize = 14.ssp, lineHeight = 19.ssp
            ), modifier = Modifier.weight(1f)
        )
        Column(
            modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value.toStr(), style = baseStyle2().copy(
                    color = Color.Black, fontWeight = FontWeight.W600,
                    fontSize = 14.ssp,
                )
            )
            Text(
                text = desc.toStr(), style = baseStyle2().copy(
                    color = FF228D00, fontSize = 12.ssp
                )
            )
        }
    }
}


@Composable
//@Preview
fun EmissionDialog(
    state: State, onDismiss: () -> Unit = {}
) {
    ShareContentScreen(state = state, onDismiss)
}


/*@Composable
//@Preview
fun EmissionDialog(
    state: State, onDismiss: () -> Unit = {}
) {
    DialogApp(onDismiss = { }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = null,
                    modifier = Modifier
                        .noRippleClickable(onDismiss)
                        .padding(10.sdp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = HomeScreenDimens.SPACE_HORIZONTAL.sdp
                    )
            ) {

                Image(
                    painter = painterResource(id = R.drawable.globe_with_bee),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )
                Spacer(modifier = Modifier.height(12.sdp))
                Text(
                    text = "Emissions Reduced\nThis Month", style = baseStyleLarge().copy(
                        fontSize = 16.ssp, color = FF333333, textAlign = TextAlign.Center
                    )
                )
                Spacer(modifier = Modifier.height(12.sdp))
                LifeTimeComposable(state = state)
                Spacer(modifier = Modifier.height(12.sdp))
            }
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.sdp))
            Text(
                text = "Let others know!", style = baseStyle().copy(
                    color = FF333333,

                    )
            )
            Spacer(modifier = Modifier.height(12.sdp))
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Image(painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable {

                    })
                Spacer(modifier = Modifier.width(12.sdp))
                Image(painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable {

                    })
                Spacer(modifier = Modifier.width(12.sdp))
                Image(painter = painterResource(id = R.drawable.ic_twitter_x),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable {

                    })
            }
            Spacer(modifier = Modifier.height(12.sdp))
        }
    }
}*/

@Composable
fun HomeCardView(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.padding(
            top = HomeScreenDimens.SPACE_BETWEEN.sdp,
            start = HomeScreenDimens.SPACE_HORIZONTAL.sdp,
            end = HomeScreenDimens.SPACE_HORIZONTAL.sdp
        ),
        elevation = CardDefaults.cardElevation(HomeScreenDimens.CARD_ELEVATION.sdp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.sdp),
        content = content
    )
}
