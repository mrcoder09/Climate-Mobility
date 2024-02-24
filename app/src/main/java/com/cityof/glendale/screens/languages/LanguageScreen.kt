package com.cityof.glendale.screens.languages

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.components.AppButton
import com.cityof.glendale.composables.components.TitleWithDesc
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.navigation.Routes
import com.cityof.glendale.theme.FF333333
import com.cityof.glendale.utils.AppPreferencesManagerImpl
import com.cityof.glendale.utils.LangHelper
import com.cityof.glendale.utils.LanguageItem
import com.cityof.glendale.utils.appDataStore
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp


private const val TAG = "LanguageScreen"


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LanguagePreview() {
    LanguageScreen(
        viewModel = LanguageViewModel(
            AppPreferencesManagerImpl(
                LocalContext.current.appDataStore
            )
        )
    )
}

@Composable
fun LanguageScreen(
    navHostController: NavHostController? = null, viewModel: LanguageViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)
    val list = LangHelper.appLanguages(context)
//    viewModel.setLanguages(list)

    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            LanguageContract.NavAction.NavLogin -> {
                navHostController?.navigate(Routes.Landing.name)
            }

            null -> {}
        }
    })

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        // Create references for the composables to constrain
        val (column, btn) = createRefs()
        Column(
            modifier = Modifier.constrainAs(column) {
                bottom.linkTo(btn.top)
                top.linkTo(parent.top)
            }
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Image(painter = painterResource(id = R.drawable.ic_lang), contentDescription = null,
                modifier = Modifier.size(60.sdp))
            Spacer(modifier = Modifier.height(8.sdp))
            TitleWithDesc(
                title = R.string.choose_lang, titleStyle = baseStyleLarge().copy(
                    fontSize = 17.ssp, lineHeight = 20.ssp,
                ), desc = R.string.msg_choose_lang
            )
            Spacer(modifier = Modifier.height(12.sdp))
            list.forEachIndexed { index, item ->
                if (index != 0) HorizontalDivider()
                LanguageComposable(item) { selectedItem ->
                    viewModel.dispatch(LanguageContract.Intent.LanguageSelected(selectedItem))
//                Log.d(TAG, "LanguageScreen: $selectedItem")
                    LangHelper.setLocale(context, selectedItem.locale)
                    (context as Activity).recreate()
                }
            }
            Spacer(modifier = Modifier.weight(1f))

        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.constrainAs(btn) {
                bottom.linkTo(parent.bottom, margin = 16.dp)
            }
                .fillMaxWidth()
                .padding(24.dp),

            ) {
            AppButton(title = stringResource(id = R.string.msg_continue)) {
                viewModel.dispatch(LanguageContract.Intent.ContinueClicked)
            }
            Spacer(modifier = Modifier.height(4.sdp))
        }



    }



}


@Composable
fun LanguageComposable(item: LanguageItem = LanguageItem(), onClick: (LanguageItem) -> Unit) {

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onClick(item)
            }
            .padding(
                vertical = 14.sdp
            )) {
        Text(
            text = item.nativeName, style = baseStyle().copy(
                color = FF333333, fontWeight = FontWeight.Normal,
                fontSize = 14.ssp
            )
        )
        RadioButton(selected = item.isSelected, onClick = {
            onClick(item)
        })
    }

}