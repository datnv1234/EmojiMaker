package com.wa.ai.emojimaker.utils.customview.customsticker

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MotionEventCompat
import androidx.core.view.ViewCompat
import com.wa.ai.emojimaker.R
import timber.log.Timber
import java.io.File
import java.util.Arrays
import java.util.Collections
import kotlin.math.abs

open class StickerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    i2: Int = 0
) : FrameLayout(context, attributeSet, i2) {
    private val bitmapPoints: FloatArray
    private val borderPaint: Paint
    private val bounds: FloatArray
    private var bringToFrontCurrentSticker = false
    private var isConstrained = false
    private val currentCenterPoint: PointF
    private var currentIcon: BitmapStickerIcon? = null
    private var currentMode: Int
    private val downMatrix: Matrix
    private var downX = 0f
    private var downY = 0f
    private var currentSticker: Sticker? = null
    private val icons: MutableList<BitmapStickerIcon?>
    private var lastClickTime: Long
    private val listUndoTemp: MutableList<Sticker?>
    private var isLocked = false
    private var midPoint: PointF
    private var minClickDelayTime: Int
    private val moveMatrix: Matrix
    private var oldDistance: Float
    private var oldRotation: Float
    private var onStickerOperationListener: OnStickerOperationListener? = null
    private val point: FloatArray
    private var showBorder = false
    private var showIcons = false
    private val sizeMatrix: Matrix
    private val stickerRect: RectF
    private val stickers: ArrayList<Sticker?> = ArrayList()
    private val tmp: FloatArray
    private val touchSlop: Int
    private val undoList: ArrayList<ArrayList<DrawableSticker?>?>

    @Retention(AnnotationRetention.SOURCE)
    protected annotation class ActionMode {
        companion object {
            const val CLICK = 4
            const val DRAG = 1
            const val ICON = 3
            const val NONE = 0
            const val ZOOM_WITH_TWO_FINGER = 2
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    protected annotation class Flip
    interface OnStickerOperationListener {
        fun onReplaceSticker(sticker: Sticker?)
        fun onStickerAdded(sticker: Sticker?)
        fun onStickerClicked(sticker: Sticker?)
        fun onStickerDeleted(sticker: Sticker?)
        fun onStickerDoubleTapped(sticker: Sticker?)
        fun onStickerDragFinished(sticker: Sticker?)
        fun onStickerFlipped(sticker: Sticker?)
        fun onStickerHideOptionIcon()
        fun onStickerTouchedDown(sticker: Sticker?)
        fun onStickerZoomFinished(sticker: Sticker?)
        fun onUndoDeleteAll()
        fun onUndoDeleteSticker(list: List<Sticker>?)
        fun onUndoUpdateSticker(list: List<Sticker>?)
    }

    init {
        icons = ArrayList(4)
        undoList = ArrayList()
        listUndoTemp = ArrayList()
        val paint = Paint()
        borderPaint = paint
        stickerRect = RectF()
        sizeMatrix = Matrix()
        downMatrix = Matrix()
        moveMatrix = Matrix()
        bitmapPoints = FloatArray(8)
        bounds = FloatArray(8)
        point = FloatArray(2)
        currentCenterPoint = PointF()
        tmp = FloatArray(2)
        midPoint = PointF()
        oldDistance = 0.0f
        oldRotation = 0.0f
        currentMode = 0
        lastClickTime = 0
        minClickDelayTime = 200
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        attributeSet?.let {
            val typedArray: TypedArray = context.obtainStyledAttributes(it, R.styleable.StickerView)
            showIcons = typedArray.getBoolean(R.styleable.StickerView_showIcons, false)
            showBorder = typedArray.getBoolean(R.styleable.StickerView_showBorder, false)
            bringToFrontCurrentSticker = typedArray.getBoolean(R.styleable.StickerView_bringToFrontCurrentSticker, false)
            paint.isAntiAlias = true
            paint.color = typedArray.getColor(R.styleable.StickerView_borderColor, -1)
            paint.alpha = typedArray.getColor(R.styleable.StickerView_borderAlpha, 128)
            paint.strokeWidth = 5.0f
            paint.setPathEffect(DashPathEffect(floatArrayOf(28.0f, 10.0f), 0.0f))
            configDefaultIcons()
            typedArray.recycle()
        }
    }

    fun configDefaultIcons() {
        val bitmapStickerIcon =
            BitmapStickerIcon(ContextCompat.getDrawable(context, R.drawable.ic_close_emoji), 1)
        bitmapStickerIcon.iconEvent = DeleteIconEvent()
        val bitmapStickerIcon2 =
            BitmapStickerIcon(ContextCompat.getDrawable(context, R.drawable.ic_copy_emoji2), 0)
        bitmapStickerIcon2.iconEvent = CopyIconEvent(context)
        val bitmapStickerIcon3 = BitmapStickerIcon(
            ContextCompat.getDrawable(context, R.drawable.ic_scale_and_rotate2),
            3
        )
        bitmapStickerIcon3.iconEvent = ZoomIconEvent()
        icons.clear()
        icons.add(bitmapStickerIcon)
        icons.add(bitmapStickerIcon2)
        icons.add(bitmapStickerIcon3)
    }

    fun swapLayers(i2: Int, i3: Int) {
        if (stickers.size >= i2 && stickers.size >= i3) {
            Collections.swap(stickers, i2, i3)
            saveStickerState()
            Timber.tag("showSwapLayer").d("oldPos= $i2 newPos= $i3")
            invalidate()
        }
    }

    fun hideOrShowSticker(sticker: Sticker, i2: Int) {
        if (stickers.size > 0) {
            sticker.setHide(!sticker.isHide)
            saveStickerState()
            invalidate()
        }
    }

    fun sendToLayer(i2: Int, i3: Int) {
        if (stickers.size >= i2 && stickers.size >= i3) {
            stickers.removeAt(i2)
            stickers.add(i3, stickers[i2])
            invalidate()
        }
    }

    /* access modifiers changed from: protected */
    public override fun onLayout(z: Boolean, i2: Int, i3: Int, i4: Int, i5: Int) {
        super.onLayout(z, i2, i3, i4, i5)
        if (z) {
            stickerRect.left = i2.toFloat()
            stickerRect.top = i3.toFloat()
            stickerRect.right = i4.toFloat()
            stickerRect.bottom = i5.toFloat()
        }
    }

    /* access modifiers changed from: protected */
    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawStickers(canvas)
    }

    /* access modifiers changed from: protected */
    fun drawStickers(canvas: Canvas) {
        val f2: Float
        val f3: Float
        val f4: Float
        val f5: Float
        var c2: Char
        var i2 = 0
        for (i3 in stickers.indices) {
            val sticker = stickers[i3]
            if (sticker != null && !sticker.isHide) {
                sticker.draw(canvas)
            }
        }
        val sticker2 = currentSticker
        if (sticker2 != null && !isLocked) {
            if (showBorder || showIcons) {
                getStickerPoints(sticker2, bitmapPoints)
                val fArr = bitmapPoints
                val f6 = fArr[0]
                var i4 = 1
                val f7 = fArr[1]
                val f8 = fArr[2]
                val f9 = fArr[3]
                val f10 = fArr[4]
                val f11 = fArr[5]
                val f12 = fArr[6]
                val f13 = fArr[7]
                if (showBorder) {
                    f5 = f13
                    f4 = f12
                    f3 = f11
                    f2 = f10
                    canvas.drawLine(f6, f7, f8, f9, borderPaint)
                    canvas.drawLine(f6, f7, f2, f3, borderPaint)
                    canvas.drawLine(f8, f9, f4, f5, borderPaint)
                    canvas.drawLine(f4, f5, f2, f3, borderPaint)
                } else {
                    f5 = f13
                    f4 = f12
                    f3 = f11
                    f2 = f10
                }
                if (showIcons) {
                    val calculateRotation = calculateRotation(f4, f5, f2, f3)
                    while (i2 < icons.size) {
                        val bitmapStickerIcon = icons[i2]
                        val position = bitmapStickerIcon!!.position
                        if (position == 0) {
                            c2 = 3.toChar()
                            configIconMatrix(bitmapStickerIcon, f6, f7, calculateRotation)
                        } else if (position == i4) {
                            c2 = 3.toChar()
                            configIconMatrix(bitmapStickerIcon, f8, f9, calculateRotation)
                        } else if (position != 2) {
                            c2 = 3.toChar()
                            if (position == 3) {
                                configIconMatrix(bitmapStickerIcon, f4, f5, calculateRotation)
                            }
                        } else {
                            c2 = 3.toChar()
                            configIconMatrix(bitmapStickerIcon, f2, f3, calculateRotation)
                        }
                        bitmapStickerIcon.draw(canvas, borderPaint)
                        i2++
                        val c3 = c2
                        i4 = 1
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    fun configIconMatrix(bitmapStickerIcon: BitmapStickerIcon?, f2: Float, f3: Float, f4: Float) {
        bitmapStickerIcon!!.x = f2
        bitmapStickerIcon.y = f3
        bitmapStickerIcon.matrix.reset()
        bitmapStickerIcon.matrix.postRotate(
            f4,
            (bitmapStickerIcon.width / 2).toFloat(),
            (bitmapStickerIcon.height / 2).toFloat()
        )
        bitmapStickerIcon.matrix.postTranslate(
            f2 - (bitmapStickerIcon.width / 2).toFloat(),
            f3 - (bitmapStickerIcon.height / 2).toFloat()
        )
    }

    override fun onInterceptTouchEvent(motionEvent: MotionEvent): Boolean {
        if (isLocked) {
            Timber.tag("checkLockEmojiCurrent").d("1.Lock")
            return super.onInterceptTouchEvent(motionEvent)
        }
        Timber.tag("checkLockEmojiCurrent").d("1.Un_Lock")
        if (motionEvent.action != 0) {
            return super.onInterceptTouchEvent(motionEvent)
        }
        downX = motionEvent.x
        downY = motionEvent.y
        return if (findCurrentIconTouched() == null && findHandlingSticker() == null) false else true
    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        var sticker: Sticker?
        var onStickerOperationListener2: OnStickerOperationListener
        if (isLocked) {
            Timber.tag("checkLockEmojiCurrent").d("2.Lock")
            return super.onTouchEvent(motionEvent)
        }
        Timber.tag("checkLockEmojiCurrent").d("2.Un_Lock")
        val actionMasked = MotionEventCompat.getActionMasked(motionEvent)
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                onTouchUp(motionEvent)
            } else if (actionMasked != 2) {
                if (actionMasked == 5) {
                    oldDistance = calculateDistance(motionEvent)
                    oldRotation = calculateRotation(motionEvent)
                    midPoint = calculateMidPoint(motionEvent)
                    val sticker2 = currentSticker
                    if (sticker2 != null
                        && isInStickerArea(sticker2, motionEvent.getX(1), motionEvent.getY(1))
                        && findCurrentIconTouched() == null
                    ) {
                        currentMode = 2
                    }
                    Timber.tag("checkMotionFinger").d("ACTION_POINTER_DOWN: ")
                } else if (actionMasked == 6) {
                    if (currentMode == 2 && currentSticker!= null && onStickerOperationListener != null) {
                        onStickerOperationListener2 = onStickerOperationListener as OnStickerOperationListener
                        sticker = currentSticker
                        onStickerOperationListener2.onStickerZoomFinished(sticker)
                        saveStickerState()
                    }
                    currentMode = 0
                    Timber.tag("checkMotionFinger").d("ACTION_POINTER_UP: ")
                }
            } else if (currentSticker != null && !isLockCurrent) {
                handleCurrentMode(motionEvent)
                invalidate()
            }
        } else if (!onTouchDown(motionEvent)) {
            return false
        }
        return true
    }

    /* access modifiers changed from: protected */
    fun onTouchDown(motionEvent: MotionEvent): Boolean {
        currentMode = 1
        downX = motionEvent.x
        downY = motionEvent.y
        val calculateMidPoint = calculateMidPoint()
        midPoint = calculateMidPoint
        oldDistance = calculateDistance(calculateMidPoint.x, midPoint.y, downX, downY)
        oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY)
        val findCurrentIconTouched = findCurrentIconTouched()
        currentIcon = findCurrentIconTouched
        if (findCurrentIconTouched != null) {
            currentMode = 3
            findCurrentIconTouched.onActionDown(this, motionEvent)
        } else {
            currentSticker = findHandlingSticker()
        }
        val sticker = currentSticker
        if (sticker != null) {
            onStickerOperationListener!!.onStickerTouchedDown(sticker)
            downMatrix.set(currentSticker!!.matrix)
            if (bringToFrontCurrentSticker) {
                stickers.remove(currentSticker)
                stickers.add(currentSticker)
            }
        }
        if (currentIcon == null && currentSticker == null) {
            return false
        }
        invalidate()
        return true
    }

    /* access modifiers changed from: protected */
    fun onTouchUp(motionEvent: MotionEvent) {
        var sticker: Sticker?
        var onStickerOperationListener2: OnStickerOperationListener
        val sticker2: Sticker?
        var onStickerOperationListener3: OnStickerOperationListener
        val bitmapStickerIcon: BitmapStickerIcon
        val uptimeMillis = SystemClock.uptimeMillis()
        if (currentMode == 3 && currentIcon != null && currentSticker != null) {
            bitmapStickerIcon = currentIcon as BitmapStickerIcon
            bitmapStickerIcon.onActionUp(this, motionEvent)
        }
        if (currentMode == 1
            && abs(motionEvent.x - downX) < touchSlop.toFloat()
            && abs(motionEvent.y - downY) < touchSlop.toFloat()
            && currentSticker != null
        ) {
            sticker2 = currentSticker
            currentMode = 4
            val onStickerOperationListener4 = onStickerOperationListener
            onStickerOperationListener4?.onStickerClicked(sticker2)
            if (uptimeMillis - lastClickTime < minClickDelayTime.toLong()
                && onStickerOperationListener != null) {
                onStickerOperationListener3 = onStickerOperationListener as OnStickerOperationListener
                onStickerOperationListener3.onStickerDoubleTapped(currentSticker)
            }
        }
        if (currentMode == 1 && currentSticker != null && onStickerOperationListener != null) {
            onStickerOperationListener2 = onStickerOperationListener as OnStickerOperationListener
            sticker = currentSticker
            onStickerOperationListener2.onStickerDragFinished(sticker)
            Timber.tag("checkDragCurrent").d("--------------")
            Timber.tag("checkDragCurrent").d("onTouchUp: 1, %s", currentSticker!!.matrix.toString())
            for (i2 in stickers.indices) {
                Timber.tag("checkDragCurrent").d("onTouchUp: 2, %s", stickers[i2]!!.matrix.toString())
            }
            saveStickerState()
        }
        currentMode = 0
        lastClickTime = uptimeMillis
    }

    /* access modifiers changed from: protected */
    fun handleCurrentMode(motionEvent: MotionEvent) {
        var bitmapStickerIcon: BitmapStickerIcon
        val i2 = currentMode
        if (i2 != 0) {
            if (i2 == 1) {
                if (currentSticker != null) {
                    moveMatrix.set(downMatrix)
                    moveMatrix.postTranslate(motionEvent.x - downX, motionEvent.y - downY)
                    currentSticker!!.setMatrix(moveMatrix)
                    if (isConstrained) {
                        constrainSticker(currentSticker)
                        Timber.tag("checkMoveSticker").d("1.DRAG")
                    }
                }
                Timber.tag("checkMoveSticker").d("DRAG")
                return
            } else if (i2 == 2) {
                if (currentSticker != null) {
                    val calculateDistance = calculateDistance(motionEvent)
                    val calculateRotation = calculateRotation(motionEvent)
                    moveMatrix.set(downMatrix)
                    val matrix = moveMatrix
                    val f2 = oldDistance
                    matrix.postScale(
                        calculateDistance / f2,
                        calculateDistance / f2,
                        midPoint.x,
                        midPoint.y
                    )
                    moveMatrix.postRotate(calculateRotation - oldRotation, midPoint.x, midPoint.y)
                    currentSticker!!.setMatrix(moveMatrix)
                }
                Timber.tag("checkMoveSticker").d("ZOOM_WITH_TWO_FINGER")
                return
            } else if (i2 == 3) {
                if (currentSticker != null && currentIcon != null) {
                    bitmapStickerIcon = currentIcon as BitmapStickerIcon
                    bitmapStickerIcon.onActionMove(this, motionEvent)
                }
                Timber.tag("checkMoveSticker").d("ICON")
                return
            } else if (i2 != 4) {
                return
            }
        }
        Timber.tag("checkMoveSticker").d("CLICK")
    }

    fun zoomAndRotateCurrentSticker(motionEvent: MotionEvent) {
        zoomAndRotateSticker(currentSticker, motionEvent)
    }

    fun zoomAndRotateSticker(sticker: Sticker?, motionEvent: MotionEvent) {
        if (sticker != null) {
            val calculateDistance =
                calculateDistance(midPoint.x, midPoint.y, motionEvent.x, motionEvent.y)
            val calculateRotation =
                calculateRotation(midPoint.x, midPoint.y, motionEvent.x, motionEvent.y)
            Timber.tag("HVV1312").e("OK ???: ${midPoint.x} va ${motionEvent.x}")
            moveMatrix.set(downMatrix)
            val matrix = moveMatrix
            val f2 = oldDistance
            matrix.postScale(calculateDistance / f2, calculateDistance / f2, midPoint.x, midPoint.y)
            moveMatrix.postRotate(calculateRotation - oldRotation, midPoint.x, midPoint.y)
            currentSticker!!.setMatrix(moveMatrix)
        }
    }

    fun customeZoomAndRotateSticker(sticker: Sticker?, i2: Int, i3: Int, i4: Int, i5: Int) {
        if (sticker != null) {
            val calculateDistance =
                calculateDistance(midPoint.x, midPoint.y, i4.toFloat(), i5.toFloat())
            Timber.tag("HVV1312")
                .e("mid Point: ${midPoint.x} va ${midPoint.y} va va va m: $i4 va n: i5")
            val calculateDistance2 =
                calculateDistance(midPoint.x, midPoint.y, i2.toFloat(), i3.toFloat())
            moveMatrix.set(downMatrix)
            val f2 = calculateDistance2 / calculateDistance
            moveMatrix.postScale(f2, f2, midPoint.x, midPoint.y)
            val sticker2 = currentSticker
            sticker2?.setMatrix(moveMatrix)
            invalidate()
        }
    }

    /* access modifiers changed from: protected */
    fun constrainSticker(sticker: Sticker?) {
        val width = width
        val height = height
        sticker!!.getMappedCenterPoint(currentCenterPoint, point, tmp)
        var f2 = 0.0f
        var f3 = if (currentCenterPoint.x < 0.0f) -currentCenterPoint.x else 0.0f
        val f4 = width.toFloat()
        if (currentCenterPoint.x > f4) {
            f3 = f4 - currentCenterPoint.x
        }
        if (currentCenterPoint.y < 0.0f) {
            f2 = -currentCenterPoint.y
        }
        val f5 = height.toFloat()
        if (currentCenterPoint.y > f5) {
            f2 = f5 - currentCenterPoint.y
        }
        sticker.matrix.postTranslate(f3, f2)
    }

    /* access modifiers changed from: protected */
    fun findCurrentIconTouched(): BitmapStickerIcon? {
        for (next in icons) {
            val x = next!!.x - downX
            val y = next.y - downY
            if ((x * x + y * y).toDouble() <= Math.pow(
                    (next.iconRadius + next.iconRadius).toDouble(),
                    2.0
                )
            ) {
                return next
            }
        }
        return null
    }

    /* access modifiers changed from: protected */
    fun findHandlingSticker(): Sticker? {
        for (size in stickers.indices.reversed()) {
            if (isInStickerArea(stickers[size], downX, downY)) {
                return stickers[size]
            }
        }
        return null
    }

    /* access modifiers changed from: protected */
    fun isInStickerArea(sticker: Sticker?, f2: Float, f3: Float): Boolean {
        val fArr = tmp
        fArr[0] = f2
        fArr[1] = f3
        return sticker!!.contains(fArr)
    }

    /* access modifiers changed from: protected */
    fun calculateMidPoint(motionEvent: MotionEvent?): PointF {
        if (motionEvent == null || motionEvent.pointerCount < 2) {
            midPoint[0.0f] = 0.0f
            return midPoint
        }
        midPoint[(motionEvent.getX(0) + motionEvent.getX(1)) / 2.0f] =
            (motionEvent.getY(0) + motionEvent.getY(1)) / 2.0f
        return midPoint
    }

    /* access modifiers changed from: protected */
    fun calculateMidPoint(): PointF {
        val sticker = currentSticker
        if (sticker == null) {
            midPoint[0.0f] = 0.0f
            return midPoint
        }
        sticker.getMappedCenterPoint(midPoint, point, tmp)
        return midPoint
    }

    /* access modifiers changed from: protected */
    fun calculateRotation(motionEvent: MotionEvent?): Float {
        return if (motionEvent == null || motionEvent.pointerCount < 2) {
            0.0f
        } else calculateRotation(
            motionEvent.getX(0),
            motionEvent.getY(0),
            motionEvent.getX(1),
            motionEvent.getY(1)
        )
    }

    /* access modifiers changed from: protected */
    fun calculateRotation(f2: Float, f3: Float, f4: Float, f5: Float): Float {
        return Math.toDegrees(Math.atan2((f3 - f5).toDouble(), (f2 - f4).toDouble())).toFloat()
    }

    /* access modifiers changed from: protected */
    fun calculateDistance(motionEvent: MotionEvent?): Float {
        return if (motionEvent == null || motionEvent.pointerCount < 2) {
            0.0f
        } else calculateDistance(
            motionEvent.getX(0),
            motionEvent.getY(0),
            motionEvent.getX(1),
            motionEvent.getY(1)
        )
    }

    /* access modifiers changed from: protected */
    fun calculateDistance(f2: Float, f3: Float, f4: Float, f5: Float): Float {
        val d2 = (f2 - f4).toDouble()
        val d3 = (f3 - f5).toDouble()
        java.lang.Double.isNaN(d2)
        java.lang.Double.isNaN(d2)
        java.lang.Double.isNaN(d3)
        java.lang.Double.isNaN(d3)
        return Math.sqrt(d2 * d2 + d3 * d3).toFloat()
    }

    /* access modifiers changed from: protected */
    fun calculateDistance2(f2: Float, f3: Float, f4: Float, f5: Float): Float {
        val d2 = (f2 - f4).toDouble()
        val d3 = (f3 - f5).toDouble()
        return Math.sqrt(d2 * d2 + d3 * d3).toFloat()
    }

    /* access modifiers changed from: protected */
    public override fun onSizeChanged(i2: Int, i3: Int, i4: Int, i5: Int) {
        super.onSizeChanged(i2, i3, i4, i5)
    }

    /* access modifiers changed from: protected */
    fun transformSticker(sticker: Sticker?) {
        if (sticker == null) {
            Timber.tag(TAG).e("transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null")
            return
        }
        sizeMatrix.reset()
        val width = width.toFloat()
        val height = height.toFloat()
        val width2 = sticker.width.toFloat()
        val height2 = sticker.height.toFloat()
        sizeMatrix.postTranslate((width - width2) / 2.0f, (height - height2) / 2.0f)
        val f2 = (if (width < height) width / width2 else height / height2) / 2.0f
        sizeMatrix.postScale(f2, f2, width / 2.0f, height / 2.0f)
        sticker.matrix.reset()
        sticker.setMatrix(sizeMatrix)
        invalidate()
    }

    fun flipCurrentSticker(i2: Int) {
        flip(currentSticker, i2)
    }

    fun flip(sticker: Sticker?, i2: Int) {
        if (sticker != null && !sticker.isLock) {
            sticker.getCenterPoint(midPoint)
            if (i2 and 1 > 0) {
                sticker.matrix.preScale(-1.0f, 1.0f, midPoint.x, midPoint.y)
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally)
            }
            if (i2 and 2 > 0) {
                sticker.matrix.preScale(1.0f, -1.0f, midPoint.x, midPoint.y)
                sticker.setFlippedVertically(!sticker.isFlippedVertically)
            }
            val onStickerOperationListener2 = onStickerOperationListener
            onStickerOperationListener2?.onStickerFlipped(sticker)
            saveStickerState()
            invalidate()
        }
    }

    @JvmOverloads
    fun replace(sticker: Sticker?, z: Boolean = true): Boolean {
        val f2: Float
        if (currentSticker == null || sticker == null) {
            return false
        }
        val width = width.toFloat()
        val height = height.toFloat()
        if (z) {
            sticker.setMatrix(currentSticker!!.matrix)
            sticker.setFlippedVertically(currentSticker!!.isFlippedVertically)
            sticker.setFlippedHorizontally(currentSticker!!.isFlippedHorizontally)
        } else {
            currentSticker!!.matrix.reset()
            sticker.matrix.postTranslate(
                (width - currentSticker!!.width.toFloat()) / 2.0f,
                (height - currentSticker!!.height.toFloat()) / 2.0f
            )
            f2 = if (width < height) {
                width / currentSticker!!.drawable.intrinsicWidth.toFloat()
            } else {
                height / currentSticker!!.drawable.intrinsicHeight.toFloat()
            }
            val f3 = f2 / 2.0f
            sticker.matrix.postScale(f3, f3, width / 2.0f, height / 2.0f)
        }
        stickers[stickers.indexOf(currentSticker)] = sticker
        currentSticker = sticker
        invalidate()
        return true
    }

    fun replaceSticker(sticker: Sticker?, sticker2: Sticker?) {
        if (sticker != null && sticker2 != null) {
            sticker2.setMatrix(sticker.matrix)
            sticker2.setFlippedVertically(sticker.isFlippedVertically)
            sticker2.setFlippedHorizontally(sticker.isFlippedHorizontally)
            sticker2.setLock(sticker.isLock)
            stickers[stickers.indexOf(sticker)] = sticker2
            currentSticker = sticker2
            val onStickerOperationListener2 = onStickerOperationListener
            onStickerOperationListener2?.onReplaceSticker(sticker)
            saveStickerState()
            invalidate()
        }
    }

    fun remove(sticker: Sticker?): Boolean {
        if (stickers.contains(sticker)) {
            stickers.remove(sticker)
            val onStickerOperationListener2 = onStickerOperationListener
            onStickerOperationListener2?.onStickerDeleted(sticker)
            if (currentSticker === sticker) {
                currentSticker = null
            }
            saveStickerState()
            invalidate()
            return true
        }
        Timber.tag(TAG).d("remove: the sticker is not in this StickerView")
        return false
    }

    fun removeCurrentSticker(): Boolean {
        val onStickerOperationListener2 = onStickerOperationListener
        onStickerOperationListener2?.onStickerHideOptionIcon()
        return remove(currentSticker)
    }

    fun hideSelect() {
        val onStickerOperationListener2 = onStickerOperationListener
        onStickerOperationListener2?.onStickerHideOptionIcon()
        currentSticker = null
        invalidate()
    }

    fun removeAllStickers() {
        stickers.clear()
        val sticker = currentSticker
        if (sticker != null) {
            sticker.release()
            currentSticker = null
        }
        invalidate()
    }

    fun clone(sticker: Sticker) {
        if (sticker is DrawableSticker) {
            val drawableSticker = sticker
            val drawableSticker2 = DrawableSticker(
                drawableSticker.drawable.constantState!!.newDrawable().mutate(),
                drawableSticker.drawablePath
            )
            val matrix = Matrix(sticker.getMatrix())
            matrix.postTranslate(0.0f, 30.0f)
            drawableSticker2.setMatrix(matrix)
            drawableSticker2.setPagerSelect(sticker.getPagerSelect())
            drawableSticker2.setPosSelect(sticker.getPosSelect())
            drawableSticker2.setHide(sticker.isHide())
            drawableSticker2.setLock(sticker.isLock())
            drawableSticker2.setFlippedVertically(sticker.isFlippedVertically())
            drawableSticker2.setFlippedHorizontally(sticker.isFlippedHorizontally())
            stickers.add(drawableSticker2)
            invalidate()
        } else if (sticker is TextSticker) {
            val textSticker = TextSticker(context)
            val textSticker2 = sticker
            textSticker.setText(textSticker2.text)
            textSticker.setDrawable(textSticker2.drawable.constantState!!.newDrawable().mutate())
            textSticker.resizeText()
            addSticker(textSticker)
            val matrix2 = Matrix(sticker.getMatrix())
            matrix2.postTranslate(0.0f, 30.0f)
            textSticker.setMatrix(matrix2)
        }
        saveStickerState()
    }

    @JvmOverloads
    fun addSticker(sticker: Sticker, i2: Int = 1): StickerView {
        if (ViewCompat.isLaidOut(this)) {
            addStickerImmediately(sticker, i2)
        } else {
            post { addStickerImmediately(sticker, i2) }
        }
        return this
    }

    /* access modifiers changed from: protected */
    fun addStickerImmediately(sticker: Sticker, i2: Int) {
        setStickerPosition(sticker, i2)
        var width = width.toFloat() / sticker.drawable.intrinsicWidth.toFloat()
        val height = height.toFloat() / sticker.drawable.intrinsicHeight.toFloat()
        if (width > height) {
            width = height
        }
        val f2 = width / 2.0f
        sticker.matrix.postScale(f2, f2, (getWidth() / 2).toFloat(), (getHeight() / 2).toFloat())
        currentSticker = sticker
        stickers.add(sticker)
        saveStickerState()
        val onStickerOperationListener2 = onStickerOperationListener
        onStickerOperationListener2?.onStickerAdded(sticker)
        invalidate()
    }

    /* access modifiers changed from: protected */
    fun setStickerPosition(sticker: Sticker, i2: Int) {
        val width = width.toFloat() - sticker.width.toFloat()
        val height = height.toFloat() - sticker.height.toFloat()
        sticker.matrix.postTranslate(
            if (i2 and 4 > 0) width / 4.0f else if (i2 and 8 > 0) width * 0.75f else width / 2.0f,
            if (i2 and 2 > 0) height / 4.0f else if (i2 and 16 > 0) height * 0.75f else height / 2.0f
        )
    }

    fun getStickerPoints(sticker: Sticker?): FloatArray {
        val fArr = FloatArray(8)
        getStickerPoints(sticker, fArr)
        return fArr
    }

    fun getStickerPoints(sticker: Sticker?, fArr: FloatArray?) {
        if (sticker == null) {
            if (fArr != null) {
                Arrays.fill(fArr, 0.0f)
            }
            return
        }
        sticker.getBoundPoints(bounds)
        sticker.getMappedPoints(fArr, bounds)
    }

    fun save(file: File?) {
        try {
            StickerUtils.saveImageToGallery(file, createBitmap())
            StickerUtils.notifySystemGallery(context, file)
        } catch (_: IllegalArgumentException) {
        } catch (_: IllegalStateException) {
        }
    }

    @Throws(OutOfMemoryError::class)
    fun createBitmap(): Bitmap {
        currentSticker = null
        val createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        draw(Canvas(createBitmap))
        return createBitmap
    }

    val stickerCount: Int
        get() = stickers.size

    fun getStickers(): List<Sticker?> {
        return stickers
    }

    val reverseSticker: ArrayList<*>
        get() {
            val arrayList: ArrayList<*> = ArrayList<Any?>(stickers)
            arrayList.reverse()
            return arrayList
        }

    fun setStickers(list: List<Sticker?>) {
        for (next in list) {
            if (next is DrawableSticker) {
                val drawableSticker = next
                val drawableSticker2 = DrawableSticker(
                    drawableSticker.originalDrawable.constantState!!.newDrawable().mutate(),
                    drawableSticker.drawablePath
                )
                drawableSticker2.setMatrix(drawableSticker.matrix)
                drawableSticker2.setPagerSelect(drawableSticker.pagerSelect)
                drawableSticker2.setPosSelect(drawableSticker.posSelect)
                drawableSticker2.setLock(drawableSticker.isLock)
                drawableSticker2.setFlippedHorizontally(drawableSticker.isFlippedHorizontally)
                drawableSticker2.setFlippedVertically(drawableSticker.isFlippedVertically)
                stickers.add(drawableSticker2)
                Timber.tag("MatrixEmojiEdit").d("1.add: ${drawableSticker.matrix.toString()}")
            } else {
                val z = next is TextSticker
            }
        }
        for (i2 in stickers.indices) {
            Timber.tag("MatrixEmojiEdit").d("3.size: ${stickers[i2]!!.matrix.toString()}")
        }
        invalidate()
    }

    fun unSelectStickerCurrent() {
        currentSticker = null
        invalidate()
    }

    fun selectStickerCurrent(sticker: Sticker?) {
        currentSticker = sticker
        invalidate()
    }

    val stickerFace: Sticker?
        get() {
            if (stickers.size <= 0) {
                return null
            }
            for (i2 in stickers.indices) {
                if (stickers[i2]!!.pagerSelect == 0) {
                    return stickers[i2]
                }
            }
            return null
        }
    val isNoneSticker: Boolean
        get() = stickerCount == 0

    fun setLocked(z: Boolean): StickerView {
        isLocked = z
        invalidate()
        return this
    }

    val isLockCurrent: Boolean
        get() = currentSticker!!.isLock

    fun setLockedCurrent(z: Boolean): StickerView {
        currentSticker!!.setLock(z)
        saveStickerState()
        invalidate()
        return this
    }

    fun setMinClickDelayTime(i2: Int): StickerView {
        minClickDelayTime = i2
        return this
    }

    fun setConstrained(z: Boolean): StickerView {
        isConstrained = z
        postInvalidate()
        return this
    }

    fun setOnStickerOperationListener(onStickerOperationListener2: OnStickerOperationListener?): StickerView {
        onStickerOperationListener = onStickerOperationListener2
        return this
    }

    fun getIcons(): List<BitmapStickerIcon?> {
        return icons
    }

    fun setIcons(list: List<BitmapStickerIcon?>?) {
        icons.clear()
        icons.addAll(list!!)
        invalidate()
    }

    fun saveStickerState() {
        val arrayList: ArrayList<DrawableSticker?> = ArrayList()
        for (next in stickers) {
            Timber.tag("checkDragCurrent").d("3,matrix: ${next!!.matrix.toString()}")
            val drawableSticker = next as DrawableSticker?
            val drawableSticker2 = DrawableSticker(
                drawableSticker!!.originalDrawable.constantState!!.newDrawable().mutate(),
                drawableSticker.drawablePath
            )
            drawableSticker2.setMatrix(next.matrix)
            drawableSticker2.setPagerSelect(next.pagerSelect)
            drawableSticker2.setPosSelect(next.posSelect)
            drawableSticker2.setHide(next.isHide)
            drawableSticker2.setLock(next.isLock)
            drawableSticker2.setFlippedHorizontally(next.isFlippedHorizontally)
            drawableSticker2.setFlippedVertically(next.isFlippedVertically)
            arrayList.add(drawableSticker2)
        }
        undoList.add(arrayList)
        Timber.tag("checkDragCurrent").d("4,matrix: ${undoList.size}")
        for (i2 in 0 until undoList.size) {
            for (i3 in 0 until undoList[i2]!!.size) {
                Timber.tag("checkDragCurrent")
                    .d("5,matrix: ${undoList[i2]!![i3]!!.matrix.toString()}")
            }
        }
    }

    fun undo() {
        if (undoList.size > 1) {
            listUndoTemp.clear()
            val list = listUndoTemp
            val list2: ArrayList<ArrayList<DrawableSticker?>?> = undoList
            list.addAll(list2[list2.size - 1]!!)
            val list3: ArrayList<ArrayList<DrawableSticker?>?> = undoList
            list3.removeAt(list3.size - 1)
            val list4: List<List<Sticker?>?> = undoList
            val list5 = list4[list4.size - 1]
            removeAllStickers()
            val findModelsNotInList2 = findModelsNotInList2(listUndoTemp, list5)
            val findModelsInList2NotInList1 = findModelsInList2NotInList1(
                listUndoTemp, list5
            )
            Timber.tag("StickerDeleteUndo")
                .d("----------------------%s, %s", listUndoTemp.size, list5!!.size)
            if (!findModelsNotInList2.isEmpty()) {
                Timber.tag("StickerDeleteUndo").d("Các sticker có trong list1 mà không có trong list2 là")
                val onStickerOperationListener2 = onStickerOperationListener
                onStickerOperationListener2?.onUndoDeleteSticker(findModelsNotInList2)
                for (next in findModelsNotInList2) {
                    Timber.tag("StickerDeleteUndo").d("pager: ${next.pagerSelect} pos: ${next.posSelect}")
                }
            } else {
                Timber.tag("StickerDeleteUndo")
                    .d("Không có sticker nào trong list1 mà không có trong list2. Size= %s", findModelsInList2NotInList1.size)
                val onStickerOperationListener3 = onStickerOperationListener
                onStickerOperationListener3?.onUndoUpdateSticker(findModelsInList2NotInList1)
            }
            Timber.tag("checkDragCurrent").d("undo: 1.")
            for (sticker in list5) {
                Timber.tag("checkDragCurrent").d("undo: 2. ${sticker!!.matrix.toString()}")
                val drawableSticker = sticker as DrawableSticker?
                val drawableSticker2 = DrawableSticker(
                    drawableSticker!!.originalDrawable.constantState!!.newDrawable().mutate(),
                    drawableSticker.drawablePath
                )
                drawableSticker2.setMatrix(sticker.matrix)
                drawableSticker2.setPagerSelect(sticker.pagerSelect)
                drawableSticker2.setPosSelect(sticker.posSelect)
                drawableSticker2.setHide(sticker.isHide)
                drawableSticker2.setLock(sticker.isLock)
                drawableSticker2.setFlippedHorizontally(sticker.isFlippedHorizontally)
                drawableSticker2.setFlippedVertically(sticker.isFlippedVertically)
                stickers.add(drawableSticker2)
                invalidate()
            }
            return
        }
        removeAllStickers()
        undoList.clear()
        val onStickerOperationListener4 = onStickerOperationListener
        onStickerOperationListener4?.onUndoDeleteAll()
    }

    fun setListUndo(list: List<List<Sticker>>) {
        for (i2 in list.indices) {
            val arrayList: ArrayList<DrawableSticker?> = ArrayList()
            for (i3 in list[i2].indices) {
                if (list[i2][i3] is DrawableSticker) {
                    val drawableSticker = list[i2][i3] as DrawableSticker
                    val drawableSticker2 = DrawableSticker(
                        drawableSticker.originalDrawable.constantState!!.newDrawable().mutate(),
                        drawableSticker.drawablePath
                    )
                    drawableSticker2.setMatrix(list[i2][i3].matrix)
                    drawableSticker2.setPagerSelect(list[i2][i3].pagerSelect)
                    drawableSticker2.setPosSelect(list[i2][i3].posSelect)
                    drawableSticker2.setLock(list[i2][i3].isLock)
                    drawableSticker2.setFlippedHorizontally(list[i2][i3].isFlippedHorizontally)
                    drawableSticker2.setFlippedVertically(list[i2][i3].isFlippedVertically)
                    arrayList.add(drawableSticker2)
                } else {
                    val z = list[i2][i3] is TextSticker
                }
            }
            undoList.add(arrayList)
        }
    }

    fun getUndoList(): List<List<Sticker?>?> {
        return undoList
    }

    fun recentSticker(list: List<List<Sticker?>?>) {
        removeAllStickers()
        stickers.addAll(list[list.size - 1]!!)
        invalidate()
    }

    companion object {
        private const val DEFAULT_MIN_CLICK_DELAY_TIME = 200
        const val FLIP_HORIZONTALLY = 1
        const val FLIP_VERTICALLY = 2
        private const val TAG = "StickerView"
        fun findModelsNotInList2(list: List<Sticker?>, list2: List<Sticker?>?): List<Sticker> {
            val arrayList: ArrayList<Sticker> = ArrayList()
            for (next in list) {
                var z = false
                val it = list2!!.iterator()
                while (true) {
                    if (!it.hasNext()) {
                        break
                    }
                    val next2 = it.next()
                    if (next!!.pagerSelect == next2!!.pagerSelect && next.posSelect == next2.posSelect) {
                        z = true
                        break
                    }
                }
                if (!z) {
                    if (next != null) {
                        arrayList.add(next)
                    }
                }
            }
            return arrayList
        }

        fun findModelsInList2NotInList1(
            list: List<Sticker?>,
            list2: List<Sticker?>?
        ): List<Sticker> {
            val arrayList: ArrayList<Sticker> = ArrayList()
            for (next in list2!!) {
                var z = false
                val it = list.iterator()
                while (true) {
                    if (!it.hasNext()) {
                        break
                    }
                    val next2 = it.next()
                    if (next!!.pagerSelect == next2!!.pagerSelect && next.posSelect == next2.posSelect) {
                        z = true
                        break
                    }
                }
                if (!z) {
                    if (next != null) {
                        arrayList.add(next)
                    }
                }
            }
            return arrayList
        }
    }
}
