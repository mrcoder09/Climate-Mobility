package com.cityof.glendale.screens.signup.personalDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.theme.Purple
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun TermConditionScreen(
    navHostController: NavHostController? = null
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        AppBarWithBack {
            navHostController?.popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.sdp, vertical = 24.sdp
                )
        ) {
            Text(
                text = stringResource(R.string.terms_of_use), style = baseStyleLarge().copy(
                    fontWeight = FontWeight.Bold, color = Purple, fontSize = 22.ssp
                ), modifier = Modifier
                    .padding(bottom = 4.sdp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(R.string.please_read_this_carefully_to_continue),
                style = baseStyle().copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(
                        bottom = 12.sdp,
                    )
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(R.string.these_app_terms_of_use), style = baseStyle().copy(
                    textAlign = TextAlign.Justify, fontWeight = FontWeight.W400
                )
            )
            Text(
                text = stringResource(R.string.user_eligibility),
                modifier = Modifier
                    .padding(vertical = 18.sdp)
                    .align(Alignment.CenterHorizontally),
                style = baseStyle().copy(fontWeight = FontWeight.Bold, fontSize = 18.ssp)
            )
            Text(
                text = stringResource(R.string.the_app_is_provided_by_beeline),
                style = baseStyle().copy(
                    textAlign = TextAlign.Justify, fontWeight = FontWeight.W400
                ),
                modifier = Modifier.padding(
                    bottom = 12.sdp
                )
            )
            Text(
                text = stringResource(R.string.by_continuing_you), style = baseStyle().copy(
                    color = Color.Black, fontWeight = FontWeight.Normal
                )
            )
        }
    }
}