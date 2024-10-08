package com.wa.ai.emojimaker.utils.rate

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import java.util.UUID

open class AnimationRatingBar : BaseRatingBar {
    protected var mHandler: Handler? = null

    protected var mRunnable: Runnable? = null

    protected var mRunnableToken = UUID.randomUUID().toString()

    protected constructor(context: Context?) : super(context) {
        init()
    }

    protected constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    protected constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mHandler = Handler(Looper.getMainLooper())
    }

    protected fun postRunnable(runnable: Runnable?, ANIMATION_DELAY: Long) {
        if (mHandler == null) {
            mHandler = Handler(Looper.getMainLooper())
        }
        val timeMillis = SystemClock.uptimeMillis() + ANIMATION_DELAY
        mHandler!!.postAtTime(runnable!!, mRunnableToken, timeMillis)
    }
}