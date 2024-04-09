package com.wa.ai.emojimaker.utils.extention

import android.view.View
import androidx.core.view.isVisible

fun View.visible() = if (!this.isVisible) this.visibility = View.VISIBLE else {
}
fun View.gone() {
    this.visibility = View.GONE
}
fun View.invisible() = if (this.isVisible) this.visibility = View.INVISIBLE else {
}
fun View.preventTwoClick(time: Int) {
    if (isAttachedToWindow) {
        isEnabled = false
        postDelayed({ isEnabled = true }, time.toLong())
    }
}

fun View.setOnSafeClick(defaultInterval: Int = 600, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(defaultInterval) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

class SafeClickListener(
    private var defaultInterval: Int = 600,
    private val onSafeCLick: (View) -> Unit,
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = System.currentTimeMillis()
        runCatching {
            onSafeCLick(v)
        }.onFailure {
            it.printStackTrace()
        }
    }
}