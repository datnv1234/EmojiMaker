package com.wa.ai.emojimaker.utils.extention

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.app.ShareCompat
import com.wa.ai.emojimaker.R
import timber.log.Timber

fun Activity.setFullScreen() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

fun Activity.setStatusBarColor(color: String) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.parseColor(color)
}

fun isAtLeastSdkVersion(versionCode: Int): Boolean {
    return Build.VERSION.SDK_INT >= versionCode
}

fun Activity.hideSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            or View.SYSTEM_UI_FLAG_IMMERSIVE)
}

fun Activity.startSetting() = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", packageName, null)
    startActivity(this)
}

fun Context.openMarket() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${packageName}")))
    } catch (anfe: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=${packageName}"
                )
            )
        )
    }
}

fun Context.sendEmail(mail: String) {
    Intent(
        Intent.ACTION_SENDTO, Uri.fromParts("mailto", mail, null)
    ).let {
        it.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
        it.putExtra(Intent.EXTRA_SUBJECT, "($packageName)")
        it.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(Intent.createChooser(it, "Send mail"))
    }

}


fun Context.startEmergencyCall() {
    Intent("com.android.phone.EmergencyDialer.DIAL").apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(this)
    }
}


fun Context.openCamera() {
    try {
        Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            flags = (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(this)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.getIconFromPackageName(pka: String): Drawable {
    val drawable: Drawable = try {
        packageManager.getApplicationIcon(pka)
    } catch (e: java.lang.Exception) {
        ColorDrawable(Color.TRANSPARENT)
    }
    return drawable
}

fun Context.openSettingsAccessibility(nameServiceClass: String) {
    try {
        val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val bundle = Bundle()
        // Cần truyển Acessibility service
        val flattenToString = ComponentName(packageName, nameServiceClass).flattenToString()
        bundle.putString(":settings:fragment_args_key", flattenToString)
        intent.putExtra(":settings:fragment_args_key", flattenToString)
        intent.putExtra(":settings:show_fragment_args", bundle)
        startActivity(intent)
    } catch (e: java.lang.Exception) {
        try {
            val intent2 = Intent("android.settings.ACCESSIBILITY_SETTINGS")
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent2)
            e.printStackTrace()
        } catch (unused: java.lang.Exception) {
            Timber.e(unused)
        }
    }
}

fun Activity.shareAppLink() {
    try {
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle("Chooser title")
            .setText(getString(R.string.app_name) + ": \t" + "http://play.google.com/store/apps/details?id=" + this.getPackageName())
            .startChooser();
    } catch (e: Exception) {
        //e.toString();
    }
}