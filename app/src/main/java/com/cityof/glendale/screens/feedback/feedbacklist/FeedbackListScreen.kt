package com.cityof.glendale.screens.feedback.feedbacklist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.DoUnauthorization
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.ProgressDialogApp
import com.cityof.glendale.composables.components.ToastApp
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.components.ratingbar.RatingBar
import com.cityof.glendale.composables.components.ratingbar.model.GestureStrategy
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.network.AppRepository
import com.cityof.glendale.network.MockApiService
import com.cityof.glendale.network.responses.Feedback
import com.cityof.glendale.network.responses.getChips
import com.cityof.glendale.network.responses.timeAgo
import com.cityof.glendale.network.umoresponses.UmoVehicle
import com.cityof.glendale.network.umoresponses.getFromAndTo
import com.cityof.glendale.network.umoresponses.getTime
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListContract.Intent
import com.cityof.glendale.screens.feedback.feedbacklist.FeedbackListContract.State
import com.cityof.glendale.screens.feedback.myfeedback.MyFeedbackIn
import com.cityof.glendale.theme.FF333333
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFEFE8FB
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.utils.AppConstants
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview
@Composable
fun FeedbackListPreview() {
    FeedbackListScreen(
        viewModel = FeedbackListViewModel(
            AppRepository(MockApiService())
        )
    )
}

@Composable
fun FeedbackListScreen(
    navHostController: NavHostController? = null, viewModel: FeedbackListViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)

    LaunchedEffect(key1 = Unit, block = {
        val temp = navHostController?.previousBackStackEntry?.savedStateHandle?.get<UmoVehicle>(
            AppConstants.DATA_BUNDLE
        )
        viewModel.initUI(temp)

    })

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            FeedbackListContract.NavActions.NavMyFeedback -> {
                val temp = state.vehicle.getFromAndTo()
                AppConstants.myFeedbackIn = MyFeedbackIn(
                    temp?.first, temp?.second, state.vehicle.id
                )
                navHostController?.navigate(Routes.MyFeedback.name)
            }

            null -> {}
        }
    })

    ProgressDialogApp(state.isLoading)
    DoUnauthorization(state.isAuthErr)
    ToastApp(state.toastMsg)

    Column(
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            AppBarWithBack(
                title = stringResource(id = R.string.feedback)
            ) {
                navHostController?.popBackStack()
            }

            BusFeedBackHeader(state.vehicle)
            CommentAndRatings(state) { filter ->
                viewModel.dispatch(Intent.SortChange(filter))
            }

            FeedbackListComposable(state) {
                viewModel.dispatch(Intent.RefreshFeedbacks)
            }
        }

        Box(
            modifier = Modifier
                .background(FFF9F9F9)
                .padding(
                    start = 22.sdp, end = 22.sdp, bottom = 22.sdp
                )
        ) {
            AppButton(title = stringResource(R.string.add_feedback)) {
                viewModel.dispatch(Intent.NavMyFeedback)
            }
        }


    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun FeedbackListComposable(
    state: State = State(), onRefresh: () -> Unit = {}
) {

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        onRefresh()
        delay(1000)
        refreshing = false
    }

    val pullState = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier
            .pullRefresh(pullState)
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .background(
                    FFF9F9F9
                )
                .fillMaxSize(), state = rememberLazyListState()
        ) {
            if (refreshing.not()) items(count = state.list.size) {
                RatingWithComment(state.list[it])
                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 22.sdp
                    )
                )
            }
        }

        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

}

@Composable
@Preview
fun RatingWithComment(feedback: Feedback = Feedback()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 22.sdp, vertical = 10.sdp
            )
    ) {
        RatingBar(rating = (feedback.rating ?: 0.0).toFloat(),
            imageVectorEmpty = ImageVector.vectorResource(R.drawable.ic_star_outlined),
            imageVectorFilled = ImageVector.vectorResource(R.drawable.ic_start_filled),
            space = 4.sdp,
            itemSize = 15.dp,
            gestureStrategy = GestureStrategy.None,
            onRatingChange = {})
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = feedback.timeAgo(), style = baseStyle().copy(
                fontSize = 12.ssp, fontWeight = FontWeight.W600
            )
        )
//        if (feedback.comment?.isNotEmpty() == true){
//            Spacer(modifier = Modifier.height(12.sdp))
//            Text(
//                text = feedback.comment ?: "", style = baseStyle2().copy(
//                    textAlign = TextAlign.Justify
//                )
//            )
//        }
        Spacer(modifier = Modifier.height(6.sdp))
        FeedbackChipsComposable(
            feedback.getChips()
        )
    }
}


@Composable
@Preview
fun FeedbackChipsComposable(
    list: List<String> = emptyList()
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
    ) {
        items(list.size) {
            val item = list[it]
            if (item.isNotEmpty()) Text(
                text = list[it], style = baseStyle().copy(
                    fontWeight = FontWeight.Normal, color = Purple, textAlign = TextAlign.Center
                ), modifier = Modifier
                    .padding(
                        top = 4.sdp, bottom = 4.sdp
                    )
                    .background(
                        color = Color(0xFFEFE8FB), shape = RoundedCornerShape(size = 16.dp)
                    )
                    .padding(
                        horizontal = 8.sdp, vertical = 8.sdp
                    ), textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, maxLines = 1
            )
        }
    }

//    FlowRow(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
//    ) {
//        list.forEach {
//            Text(
//                text = it, style = baseStyle().copy(
//                    fontWeight = FontWeight.Normal, color = Purple, textAlign = TextAlign.Center
//                ), modifier = Modifier
//                    .padding(
//                        top = 4.sdp, bottom = 4.sdp
//                    )
//                    .background(
//                        color = Color(0xFFEFE8FB), shape = RoundedCornerShape(size = 16.dp)
//                    )
//                    .padding(
//                        horizontal = 8.sdp, vertical = 8.sdp
//                    ), textAlign = TextAlign.Center, overflow = TextOverflow.Ellipsis, maxLines = 1
//            )
//        }
//    }
}


@Composable
fun CommentAndRatings(
    state: State, onSortChanged: (FeedbackSort) -> Unit
) {
//    Row(
//        horizontalArrangement = Arrangement.SpaceBetween,
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(FFF9F9F9)
//            .padding(horizontal = 22.sdp, vertical = 10.sdp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "Comments & Rating", style = baseStyleLarge().copy(
//                color = FF333333, fontSize = 16.ssp
//            )
//        )
//        Spacer(modifier = Modifier.width(22.sdp))
//
//        AppDropDown2(
//            list = state.filters,
//            selectedValue = state.selectedSort.title.toStr(),
//            selectedIndex = state.filterIndex,
//            onItemSelected = { index, item ->
//                onSortChanged(index, item)
//            },
//            modifier = Modifier
//                .width(90.sdp)
//                .height(44.sdp)
//        ) {
//            DropDownText(
//                text = it.title.toStr(),
//                style = TextInputStyle().copy(
//                    fontSize = 10.ssp
//                ),
//            )
//        }
//    }

    Column(
        Modifier
            .fillMaxWidth()
            .background(FFF9F9F9)
            .padding(horizontal = 22.sdp, vertical = 8.sdp)
    ) {
        Text(
            text = stringResource(R.string.comments_rating), style = baseStyleLarge().copy(
                color = FF333333, fontSize = 16.ssp
            )
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = stringResource(R.string.sort_by), style = baseStyleLarge().copy(
                color = FF333333, fontSize = 12.ssp
            )
        )
        Spacer(modifier = Modifier.height(4.sdp))
        SortComposable(
            filters = state.filters,
            selectedSort = state.selectedSort,
            onSortChanged = onSortChanged
        )
        Spacer(modifier = Modifier.height(8.sdp))
        HorizontalDivider()
    }
}

@Composable
fun BusFeedBackHeader(vehicle: UmoVehicle) {

    val temp = vehicle.getFromAndTo()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White
            )
            .padding(
                horizontal = 22.sdp, vertical = 22.sdp
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "To ${temp?.second ?: ""}", style = baseStyle2().copy(
                fontWeight = FontWeight.W600
            )
        )
        Spacer(modifier = Modifier.height(4.sdp))
        Text(
            text = "From Rte ${temp?.first ?: ""}", style = baseStyle2().copy(
                fontSize = 12.ssp
            )
        )
        Spacer(modifier = Modifier.height(8.sdp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            RouteInfoComposable(
                title = stringResource(R.string.route), desc = vehicle.route?.id ?: ""
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_yellow_arched_bus),
                    contentDescription = null
                )
                Text(
                    text = vehicle.getTime(), modifier = Modifier.defaultMinSize(
                        minWidth = 90.sdp
                    ), textAlign = TextAlign.Center, style = baseStyle2().copy()
                )
            }
            RouteInfoComposable(
                title = stringResource(R.string.bus_no), desc = vehicle.id ?: ""
            )
        }


//        BusDetailsHeader()
    }
}

@Composable
@Preview
fun BusInfoComposable(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_yellow_arched_bus),
            contentDescription = null
        )
        Text(
            text = "4:20 AM", modifier = Modifier.defaultMinSize(
                minWidth = 90.sdp
            ), textAlign = TextAlign.Center, style = baseStyle2().copy()
        )
    }
}

@Composable
fun RouteInfoComposable(
    modifier: Modifier = Modifier, title: String = "", desc: String = ""
) {

    Column(
        modifier = modifier
    ) {
        Text(
            text = title, style = baseStyle2().copy(
                fontSize = 12.ssp, textAlign = TextAlign.Center
            ), modifier = Modifier.defaultMinSize(minWidth = 90.sdp)
        )
        Spacer(modifier = Modifier.height(10.sdp))
        Text(
            text = desc, style = baseStyle2().copy(
                fontSize = 12.ssp, fontWeight = FontWeight.W600, textAlign = TextAlign.Center
            ), modifier = Modifier.defaultMinSize(minWidth = 90.sdp)
        )
    }
}


@Composable
fun SortComposable(
    filters: List<FeedbackSort>, selectedSort: FeedbackSort, onSortChanged: (FeedbackSort) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(filters.size) {
            SortItemComposable(
                filters[it], selectedSort == filters[it], onSortChanged = onSortChanged
            )
            Spacer(modifier = Modifier.width(8.sdp))
        }
    }
}


@Composable
fun SortItemComposable(
    sort: FeedbackSort, isSelected: Boolean = true, onSortChanged: (FeedbackSort) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .height(24.sdp)
            .then(
                if (isSelected) Modifier.background(
                    color = FFEFE8FB, shape = RoundedCornerShape(size = 5.dp)
                )
                else Modifier.border(
                    width = 1.dp, color = FF777C80, shape = RoundedCornerShape(size = 5.dp)
                )
            )
            .padding(horizontal = 6.dp)
            .noRippleClickable {
                onSortChanged(sort)
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isSelected) {
            Image(
                painter = painterResource(id = R.drawable.ic_tick),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = sort.title.toStr(), modifier = Modifier.padding(
                horizontal = 8.sdp
            ), style = baseStyle2().copy(
                fontSize = 11.ssp
            )
        )
    }
}
