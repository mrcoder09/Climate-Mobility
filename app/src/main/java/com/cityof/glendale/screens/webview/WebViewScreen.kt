package com.cityof.glendale.screens.webview

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.cityof.glendale.composables.AppBarWithBack
import com.cityof.glendale.composables.UIStr


@SuppressLint("SetJavaScriptEnabled")
@Composable
@Preview
fun WebViewScreen(url:UIStr = UIStr.Str("https://cityofglendale-api.mobileprogramming.net/api/v1/info/pageInfo?page=privacy")){


    Column {
        AppBarWithBack(
            title = "Title here"
        )
        AndroidView(
            modifier = Modifier.fillMaxSize(),factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                settings.allowContentAccess = true
                settings.allowFileAccess = true
                this.loadUrl(url.toStr(it))
            }
        })
    }

}