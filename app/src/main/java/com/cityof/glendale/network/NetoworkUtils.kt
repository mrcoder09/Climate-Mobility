package com.cityof.glendale.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.cityof.glendale.BaseApp

class NetworkReceiver : BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) connectivityReceiverListener?.onNetworkConnectionChanged(
            isConnectedOrConnecting()
        )
    }

    private fun isConnectedOrConnecting(): Boolean {
        return false
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}

@Suppress("DEPRECATION")
fun isNetworkConnected(context: Context = BaseApp.INSTANCE): Boolean {
    var result = false
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return result
}


fun doIfNetwork(
    noNet: () -> Unit = {},
    block: () -> Unit = {}
) {
    if (isNetworkConnected()) {
        block.invoke()
    } else {
        noNet.invoke()
    }
}


