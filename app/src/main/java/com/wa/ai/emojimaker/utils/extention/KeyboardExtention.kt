package com.wa.ai.emojimaker.utils.extention

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import timber.log.Timber

//fun Activity.hideKeyboard() {
//    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//    Timber.e("datnv: imm" + imm.isActive)
//    if (imm.isActive) {
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
//    }
//}
//
//fun Activity.showSoftKeyboard() {
//    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
//        InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY
//    )
//}


fun Activity.showSoftKeyboardEdt(edt: EditText) {
    kotlin.runCatching {
        edt.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        edt.postDelayed({
            imm.showSoftInput(edt, InputMethodManager.SHOW_IMPLICIT)
        }, 200) // Đặt một khoảng thời gian nhỏ trước khi hiển thị bàn phím

        edt.setOnFocusChangeListener { v, hasFocus ->
            Timber.e("datnv: hasFocus: " + hasFocus)
            if (hasFocus) {
                Timber.e("datnv: show soft keyborad")
                imm.showSoftInput(edt, InputMethodManager.SHOW_IMPLICIT)
            } else {
                imm.hideSoftInputFromWindow(edt.windowToken, 0)
            }
        }
    }.onFailure { it.printStackTrace() }
}
