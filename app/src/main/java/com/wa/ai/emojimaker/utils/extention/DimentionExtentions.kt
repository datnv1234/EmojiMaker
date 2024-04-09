package com.wa.ai.emojimaker.utils.extention

import android.content.res.Resources
fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun Int.pxToDp(): Float {
    return (this / Resources.getSystem().displayMetrics.density)
}

fun Int.dpToPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

fun Float.dpToPx(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

fun Int.spToPx(): Float {
    return this * Resources.getSystem().displayMetrics.scaledDensity
}