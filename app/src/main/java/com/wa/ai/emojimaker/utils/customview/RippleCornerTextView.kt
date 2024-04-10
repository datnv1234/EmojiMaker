package com.wa.ai.emojimaker.utils.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.wa.ai.emojimaker.R

class RippleCornerTextView : AppCompatTextView {

    private var paint: Paint? = null

    private var strokePaint: Paint? = null

    var strokeWidth: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    var strokeColor: String = "#00000000"
        set(value) {
            field = value
            invalidate()
        }

    private var cornerRadius = 0f

    private var gradient: LinearGradient? = null

    var startGradientColor: String? = null
        set(value) {
            field = value
            invalidate()
        }

    var endGradientColor: String? = null
        set(value) {
            field = value
            invalidate()
        }


    var backgroundColorView: String? = null
        set(value) {
            field = value
            invalidate()
        }


    val path = Path()

    fun setDrawableLeft(image: Int) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(image, 0, 0, 0)
        invalidate()
    }

    fun setDrawableRight(image: Int) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, image, 0)
        invalidate()
    }

    fun setDrawableTop(image: Int) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, image, 0, 0)
        invalidate()
    }

    fun setDrawableBottom(image: Int) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, image)
        invalidate()
    }

    fun setDrawableTv(left: Int, top: Int, right: Int, bottom: Int) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom)
        invalidate()
    }


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet? = null) {
        paint = Paint()
        strokePaint = Paint()


        attrs?.let {
            val typedArray: TypedArray =
                context.obtainStyledAttributes(
                    it,
                    R.styleable.RippleCornerTextView
                )
            cornerRadius =
                typedArray.getDimension(
                    R.styleable.RippleCornerTextView_cornerRadius,
                    0f
                )
            startGradientColor =
                typedArray.getString(R.styleable.RippleCornerTextView_startGradientColor)
            endGradientColor =
                typedArray.getString(R.styleable.RippleCornerTextView_endGradientColor)
            backgroundColorView =
                typedArray.getString(R.styleable.RippleCornerTextView_backgroundColorView)

            strokeWidth =
                typedArray.getFloat(R.styleable.RippleCornerTextView_strokeWidthT, strokeWidth)
            strokeColor =
                typedArray.getString(R.styleable.RippleCornerTextView_strokeColorT) ?: strokeColor
            typedArray.recycle()
        }
    }


    override fun onDraw(canvas: Canvas) {

        if (gradient == null) {
            startGradientColor?.let { start ->
                endGradientColor?.let { end ->
                    gradient = LinearGradient(
                        0f,
                        0f,
                        width.toFloat(),
                        0f,
                        intArrayOf(Color.parseColor(start), Color.parseColor(end)),
                        null,
                        Shader.TileMode.CLAMP
                    )

                }
            }
        }
        path.reset()
        path.addRoundRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            cornerRadius,
            cornerRadius,
            Path.Direction.CW
        )

        paint?.let {
            if (gradient != null) {
                gradient?.let { linearGradient ->
                    it.shader = linearGradient
                }
            } else {
                backgroundColorView?.let { color ->
                    it.color = Color.parseColor(backgroundColorView)
                }
            }
            canvas.clipPath(path)
            canvas.drawPaint(it)
        }

        val drawables = compoundDrawablesRelative
        drawables[2]?.let { drawableRight ->
            val drawableRightX = width - paddingRight - drawableRight.intrinsicWidth
            val drawableRightY = (height - drawableRight.intrinsicHeight) / 2
            drawableRight.setBounds(
                drawableRightX,
                drawableRightY,
                drawableRightX + drawableRight.intrinsicWidth,
                drawableRightY + drawableRight.intrinsicHeight
            )
            canvas.let { drawableRight.draw(it) }
        }
        // Vẽ text với stroke

        strokePaint?.let {
            it.color = Color.parseColor(strokeColor)
            it.style = Paint.Style.STROKE
            it.strokeWidth = strokeWidth
            canvas?.drawPath(path, it)
        }
        super.onDraw(canvas)
    }


}