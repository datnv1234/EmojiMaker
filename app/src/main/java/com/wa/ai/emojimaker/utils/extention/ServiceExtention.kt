package com.wa.ai.emojimaker.utils.extention

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build

fun Context.startServiceWithName(intent: Intent) {
    if (isAtLeastSdkVersion(Build.VERSION_CODES.O)) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.stopServiceWithClass(serviceClass: Class<*>) {
    if (isMyServiceRunning(serviceClass)) {
        val serviceIntent = Intent(this, serviceClass)
        stopService(serviceIntent)
    }
}

fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) return true
    }
    return false
}