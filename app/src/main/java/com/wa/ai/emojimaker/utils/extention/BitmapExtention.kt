package com.wa.ai.emojimaker.utils.extention

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View

fun View.getBitMapFromView(): Bitmap? {
    if (width == 0 || height == 0) {
        return null
    }
    var measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
    var measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
    measuredWidth = if (measuredWidth == 0) width else measuredWidth
    measuredHeight = if (measuredHeight == 0) height else measuredHeight
    measure(measuredWidth, measuredHeight)
    layout(0, 0, measuredWidth, measuredHeight)
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(b))
    return b
}


fun Bitmap.blurImage(mContext: Context, radius: Float, ratio: Int): Bitmap? {
    return try {
        val mInBitmap =
            Bitmap.createScaledBitmap(this, this.width / ratio, this.height / ratio, true)
        val mOutBitmap = Bitmap.createBitmap(
            mInBitmap!!.width, mInBitmap.height, mInBitmap.config
        )
        val mRenderScript = RenderScript.create(mContext)
        val input = Allocation.createFromBitmap(mRenderScript, mInBitmap)
        val output = Allocation.createTyped(mRenderScript, input.type)
        val script = ScriptIntrinsicBlur.create(
            mRenderScript, Element.U8_4(mRenderScript)
        )
        script.setRadius(radius)
        script.setInput(input)
        script.forEach(output)
        output.copyTo(mOutBitmap)
        if (!mInBitmap.isRecycled) {
            mInBitmap.recycle()
        }
        mOutBitmap
    } catch (e: OutOfMemoryError) {
        null
    } catch (e: java.lang.Exception) {
        null
    }
}