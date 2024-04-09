package com.wa.ai.emojimaker.utils.extention

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun isNetworkAvailable(): Boolean {
    return Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0
}

@SuppressLint("MissingPermission")
fun Context.checkInternetConnection(): Boolean {
    try {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

fun Context.isWifiConnect(): Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = mConnectivityManager.activeNetworkInfo
    if (info == null || !info.isConnected) {
        return true
    }
    return !isNetworkAvailable()
}

fun Context.isNetworkAvailable(): Boolean {
    var isOnline = false
    try {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            manager.getNetworkCapabilities(manager.activeNetwork) // need ACCESS_NETWORK_STATE permission
        isOnline =
            capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return isOnline
}