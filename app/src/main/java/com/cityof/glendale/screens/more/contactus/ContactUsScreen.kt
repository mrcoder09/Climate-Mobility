package com.cityof.glendale.screens.more.contactus

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.cityof.glendale.R
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.components.UnderlinedClickableText
import com.cityof.glendale.composables.components.baseStyle
import com.cityof.glendale.composables.components.baseStyle2
import com.cityof.glendale.composables.components.baseStyleLarge
import com.cityof.glendale.composables.noRippleClickable
import com.cityof.glendale.network.Endpoints
import com.cityof.glendale.screens.more.contactus.ContactUsContract.ContactUsItem
import com.cityof.glendale.screens.more.contactus.ContactUsContract.NavAction
import com.cityof.glendale.theme.Purple
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Preview
@Preview(locale = "es-rES")
@Preview(locale = "hy")
@Composable
fun ContactUsScreen(
    navController: NavHostController? = null,
    mobileNumber: String = "888 548 3960",
    supportEmail: String = stringResource(id = R.string.support_email),
    title: String = stringResource(R.string.customer_report_form),
    subject: String = stringResource(id = R.string.email_subject),
    viewModel: ContactUsViewModel = hiltViewModel()
) {


    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val navigation by viewModel.navigation.collectAsState(initial = null)


    LaunchedEffect(key1 = navigation, block = {
        when (navigation) {
            NavAction.Dialer -> {
                val intent = Intent()
                intent.action = Intent.ACTION_DIAL // Action for what intent called for
                intent.data =
                    Uri.parse("tel: $mobileNumber") // Data with intent respective action on intent
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                context.startActivity(intent)
            }

            NavAction.Email -> {
                Timber.d("EMAIL INTENT: OPEN EMAIL COMPOSER")
                val urlString =
                    "mailto:" + Uri.encode(supportEmail) + "?subject=" + Uri.encode("Mobile App Feedback")
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse(urlString)
                }
                context.startActivity(Intent.createChooser(emailIntent, ""))
//                viewModel.dispatch(ContactUsContract.Intent.ResetNav)
            }

            NavAction.Form -> {
                val encodedUrl =
                    URLEncoder.encode(Endpoints.CONTACT_US_FORM, StandardCharsets.UTF_8.toString())
                navController?.navigate("WebView/$title/$encodedUrl")
//                viewModel.dispatch(ContactUsContract.Intent.ResetNav)
            }

            null, NavAction.None -> return@LaunchedEffect
        }
        viewModel.dispatch(ContactUsContract.Intent.ResetNav)
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppBarWithBack(
            color = Color.White, title = stringResource(R.string.contact_us)
        ) {
            navController?.popBackStack()
        }
//        ToolbarWithImage(
//            modifier = Modifier.padding(
//                start = 22.sdp,
//                end = 44.sdp
//            )
//        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.sdp)
                .verticalScroll(state = rememberScrollState())
        ) {
//            Spacer(modifier = Modifier.height(16.sdp))
            Spacer(modifier = Modifier.height(22.sdp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.globe_with_bee),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.sdp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.sdp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ContactUsComposable(
                    modifier = Modifier
                        .width(195.dp)
                        .fillMaxHeight()
                        .weight(1f)
                        .background(
                            color = state.call.bgColor, shape = RoundedCornerShape(size = 8.dp)
                        )
                        .noRippleClickable {
                            viewModel.dispatch(ContactUsContract.Intent.EmailClicked)
                        }, state.email
                )
                Spacer(modifier = Modifier.width(8.sdp))
                ContactUsComposable(
                    modifier = Modifier
                        .width(180.dp)
                        .fillMaxHeight()
                        .weight(1f)
                        .background(

                            color = state.email.bgColor, shape = RoundedCornerShape(size = 8.dp)
                        )
                        .noRippleClickable {
                            viewModel.dispatch(ContactUsContract.Intent.CallClicked)
                        }, state.call
                )
            }
            Spacer(modifier = Modifier.height(12.sdp))
            Text(
                text = stringResource(R.string.customer_report_form), style = baseStyleLarge().copy(
                    fontSize = 18.ssp, color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.sdp))

//            AnnotatedClickableText(text = stringResource(R.string.msg_customer_report_form), clickableText =
//            stringResource(id = R.string.support_email_with_dot), maxLines = 4,
//                normalSpanStyle = normalSpanStyle().copy(
//                    fontSize = 13.ssp,
//                    fontWeight = FontWeight.Normal,
//                    color = Color.Black
//
//                ),
//                clickSpanStyle = clickSpanStyle().copy(
//                    textDecoration = TextDecoration.Underline,
//                    color = Color(0xFF326DDA),
//                    fontWeight = FontWeight.Normal
//                ),paragraphStyle = ParagraphStyle(
//                    textAlign = TextAlign.Start
//                )
//            ) {
//
//            }
//            Text(
//                text = , style = baseStyle().copy(
//                    fontSize = 13.ssp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Left
//                )
//            )

            Text(
                text = stringResource(R.string.msg_customer_report_form),
                style = baseStyle2().copy(fontSize = 13.ssp)
            )
            UnderlinedClickableText(
                text = stringResource(id = R.string.support_email_with_dot), spanStyle = SpanStyle(
                    fontSize = 13.ssp, color = Color(0xFF326DDA), fontWeight = FontWeight.Medium
                )
            ) {
                viewModel.dispatch(ContactUsContract.Intent.EmailClicked)
            }

            Spacer(modifier = Modifier.height(12.sdp))
            Text(
                text = stringResource(R.string.msg_if_you_have_any_questions),
                style = baseStyle2().copy(fontSize = 13.ssp)
            )
            UnderlinedClickableText(
                text = stringResource(id = R.string.go_to_form), spanStyle = SpanStyle(
                    fontSize = 16.ssp, color = Purple, fontWeight = FontWeight.Medium
                )
            ) {
                viewModel.dispatch(ContactUsContract.Intent.FormClicked)
            }
        }

    }
}


@Composable
fun ContactUsComposable(modifier: Modifier, item: ContactUsItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Image(painter = painterResource(id = item.id), contentDescription = null)
        Spacer(modifier = Modifier.height(2.sdp))
        Text(
            modifier = Modifier.padding(
                horizontal = 4.sdp
            ), text = item.value.toStr(), style = baseStyle().copy(
                fontSize = 13.ssp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            ), maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}