package com.cityof.glendale.composables.components

import android.graphics.Bitmap
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.cityof.glendale.composables.AppBarWithBack
import ir.kaaveh.sdpcompose.sdp


@Composable
@Preview
fun WebViewScreen(
    title: String = "",
    url: String = "",
    navHostController: NavHostController? = null,

    ) {
    var loader = remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
//            .verticalScroll(rememberScrollState())
    ) {

        AppBarWithBack(title = title) {
            navHostController?.popBackStack()
        }
        Box(
            modifier = Modifier
//                .fillMaxSize()
                .padding(
                    horizontal = 12.sdp, vertical = 16.sdp
                )
        ) {

            AndroidView(factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    settings.javaScriptEnabled = true
                    settings.builtInZoomControls = true
                    settings.setSupportZoom(true)
                    webViewClient = object : WebViewClient() {

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            // show dialog
                            loader.value = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // hide dialog
                            loader.value = false
                        }

                        override fun onReceivedSslError(
                            view: WebView?, handler: SslErrorHandler?, error: SslError?
                        ) {
                            super.onReceivedSslError(view, handler, error)
                        }
                    }
                }

            }, update = {
                it.loadUrl(url)
            })

            ProgressDialogApp(show = loader.value)
        }

    }
}