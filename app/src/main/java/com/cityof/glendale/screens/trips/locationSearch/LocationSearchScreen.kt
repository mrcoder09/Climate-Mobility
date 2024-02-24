package com.cityof.glendale.screens.trips.locationSearch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.TextFieldColor
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.data.fixes.LocationSearched
import com.cityof.glendale.network.GoogleApiRepository
import com.cityof.glendale.network.MockGoogleApiRepo
import com.cityof.glendale.theme.FF777C80
import com.cityof.glendale.theme.FFDADADA
import com.cityof.glendale.theme.FFF9F9F9
import com.cityof.glendale.utils.AppConstants
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.appDataStore
import com.cityof.glendale.utils.xtJson
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber

@Composable
@Preview
fun LocationSearchPreview() {
    LocationSearchScreen(
        viewModel = LocationSearchViewModel(
            GoogleApiRepository(
                MockGoogleApiRepo()
            ), AppPreferencesManagerImpl(LocalContext.current.appDataStore)
        )
    )
}


@Composable
fun LocationSearchScreen(
    navHostController: NavHostController? = null,
    viewModel: LocationSearchViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)


    LaunchedEffect(key1 = Unit, block = {
        viewModel.recents()
    })


    LaunchedEffect(key1 = navigation, block = {

        when (navigation) {
            is LocationSearchContract.NavAction.NavTripPlan -> {

                val loc = (navigation as LocationSearchContract.NavAction.NavTripPlan).placeDetails
                Timber.d(loc.xtJson())
                navHostController?.previousBackStackEntry?.savedStateHandle?.set(
                    AppConstants.DATA_BUNDLE, loc
                )
                AppConstants.isLocNew = true
                navHostController?.popBackStack()
            }

            null -> {}
        }

    })

    Column {
        LocationSearchBar(state.input, state.isLoading, onValueChanged = {
            viewModel.dispatch(LocationSearchContract.Intent.SearchTextChanged(it))
        }) {
            navHostController?.popBackStack()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color.White
                )
        ) {

            item {
                Column(
                    modifier = Modifier.background(Color.White)
                ) {
                    HorizontalDivider(
                        thickness = 16.sdp, color = FFF9F9F9
                    )
                    Spacer(modifier = Modifier.height(16.sdp))
                    Text(
                        modifier = Modifier.padding(start = 18.sdp),
                        text = state.title.toStr(),
                        style = baseStyleLarge().copy(
                            fontSize = 16.ssp, color = Color.Black
                        )
                    )
                }
            }

            items(state.suggestionList.size) {
                val item = state.suggestionList[it]
                SuggestionItem(item, it) { selectedPlace ->
                    viewModel.dispatch(LocationSearchContract.Intent.PlaceSelected(selectedPlace))
                }
            }
        }

    }

}

@Composable
@Preview
fun LocationSearchBar(
    input: String = "",
    isLoading: Boolean = true,
    onValueChanged: (String) -> Unit = {},
    onCrossClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .height(128.sdp)
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = 18.sdp
                )
                .background(
                    color = Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 12.dp)
                )
                .padding(
                    all = 4.sdp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(value = input, onValueChange = onValueChanged, modifier = Modifier.background(
                color = Color(0xFFF3F3F3), shape = RoundedCornerShape(size = 12.dp)
            ), placeholder = {
                Text(
                    text = stringResource(R.string.location_search), style = baseStyle2().copy(
                        color = FF777C80
                    ), modifier = Modifier
                )
            }, maxLines = 1, colors = TextFieldColor(), textStyle = baseStyle2().copy(
                fontWeight = FontWeight.Normal, fontSize = 14.ssp
            ), trailingIcon = {
                if (isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(14.sdp)
                ) else null
            })

        }
        Image(
            painter = painterResource(id = R.drawable.ic_x),
            contentDescription = null,
            modifier = Modifier
                .noRippleClickable(onCrossClicked)
                .size(18.sdp)
        )
        Spacer(modifier = Modifier.width(8.sdp))
    }
}

@Composable
fun SuggestionItem(
    item: LocationSearched, index: Int, onItemClicked: (LocationSearched) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .noRippleClickable {
                onItemClicked(item)
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.padding(
                start = 18.sdp
            ),
            painter = painterResource(id = if (index % 2 != 0) R.drawable.ic_clock_yellow else R.drawable.ic_loc_purple),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.sdp))
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(14.sdp))
            Text(
                text = item.name ?: "", style = baseStyleLarge().copy(
                    fontSize = 16.ssp, color = Color.Black
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.sdp))
            Text(
                text = item.address ?: "", style = baseStyle2().copy(
                    fontSize = 14.ssp, color = Color.Black, lineHeight = 18.ssp
                ), maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(14.sdp))
            HorizontalDivider(color = FFDADADA)
        }
    }
}
