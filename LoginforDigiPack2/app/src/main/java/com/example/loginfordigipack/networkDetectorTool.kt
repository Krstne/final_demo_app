package com.example.loginfordigipack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build


enum class ConnectionType {
    Wifi, Cellular
}

class networkDetectorTool(context: Context) {

    private var networkContext = context
    private lateinit var networkCallback: NetworkCallback
    lateinit var result: ((isAvailable: Boolean, type: ConnectionType?) -> Unit)

    @Suppress("DEPRECATION")
    fun register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val connectivityManager = networkContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager.activeNetwork == null) {
                result(false,null)
            }

            // Check if the type of connection have changed
            networkCallback = object : NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    result(false, null)
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    when {
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            result(true,ConnectionType.Wifi)
                        }
                        else -> {
                            result(true,ConnectionType.Cellular)
                        }
                    }
                }
            }
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            // Use Intent Filter for Android 8 and below
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            networkContext.registerReceiver(networkChangeReceiver, intentFilter)
        }
    }

    fun unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val connectivityManager =
                    networkContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } else {
            networkContext.unregisterReceiver(networkChangeReceiver)
        }
    }

    @Suppress("DEPRECATION")
    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            // Get Type of Connection
            if (activeNetworkInfo != null) {
                when (activeNetworkInfo.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        result(true, ConnectionType.Wifi)
                    }
                    else -> {
                        result(true, ConnectionType.Cellular)
                    }
                }
            } else {
                result(false, null)
            }
        }
    }
}