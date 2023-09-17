package com.example.animationbutton


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AnimateButton @JvmOverloads constructor(context: Context, attr: AttributeSet) :
    AppCompatTextView(context, attr) {

    private var typeDef: TypedArray
    private var radius: Float
    private var startColor = Color.parseColor("#4B5EFB")
    private var centerColor = Color.parseColor("#3CAAF7")
    private var endColor = Color.parseColor("#33BAF4")
    private var roundButton = false
    private var isAnimating = false
    private var animationProgress = 0f

    init {
        typeDef = context.obtainStyledAttributes(attr, R.styleable.AnimateButton)
        try {
            roundButton = typeDef.getBoolean(R.styleable.AnimateButton_roundButton, false)
            radius = typeDef.getDimension(R.styleable.AnimateButton_radiusButton, 0f)
        } finally {
            typeDef.recycle()
        }

        textAlignment = TEXT_ALIGNMENT_CENTER
        setBackgroundColor(Color.parseColor("#00000000"))
        startAnimation()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val gradient2 = LinearGradient(
            rectF.left, rectF.top,
            rectF.right, rectF.bottom,
            intArrayOf(
                startColor,
                centerColor, endColor
            ),
            null,
            Shader.TileMode.CLAMP
        )

        val shapePaint = Paint().apply {
            strokeWidth = 2f
            style = Paint.Style.FILL
            shader = gradient2
        }

        if (roundButton) {
            canvas!!.drawRoundRect(rectF, canvas.height / 2f, canvas.height / 2f, shapePaint)
        } else {
            canvas!!.drawRoundRect(rectF, radius, radius, shapePaint)
        }

        if (isAnimating) {
            drawAnimate(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawAnimate(canvas: Canvas) {
        var xOffset = width * animationProgress
        val maxOffset = canvas.width / 2f
        xOffset = xOffset.coerceIn(0f, maxOffset)
        val gradientLeft = LinearGradient(
            width / 2f - xOffset, 0f,
            width / 2f + 5f, height.toFloat(),
            intArrayOf(
                Color.parseColor("#40ffffff"),
                Color.parseColor("#40ffffff")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        val gradientRight = LinearGradient(
            width / 2f - 5f, 0f,
            width / 2f + xOffset, height.toFloat(),
            intArrayOf(
                Color.parseColor("#40ffffff"),
                Color.parseColor("#40ffffff")
            ),
            null,
            Shader.TileMode.CLAMP
        )

        val canvasPaintLeft = Paint().apply {
            strokeWidth = 1f
            style = Paint.Style.FILL
            shader = gradientLeft
        }

        val canvasPaintRight = Paint().apply {
            strokeWidth = 1f
            style = Paint.Style.FILL
            shader = gradientRight
        }

        val rectFLeft = RectF(
            width / 2f - xOffset,
            0f,
            width / 2f - 1f,
            height.toFloat()
        )

        val rectFRight = RectF(
            width / 2f - 1f,
            0f,
            width / 2f + xOffset,
            height.toFloat()
        )

        canvas.drawRect(rectFLeft, canvasPaintLeft)
        canvas.drawRect(rectFRight, canvasPaintRight)

        if (animationProgress >= 1.0) {
            animationProgress = 5f
        } else {
            animationProgress += 0.005f
            postInvalidateDelayed(16)
        }
    }


    fun startAnimation() {
        if (!isAnimating) {
            isAnimating = true
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.duration = 1500 // Animation duration in milliseconds
            animator.addUpdateListener { valueAnimator ->
                animationProgress = valueAnimator.animatedValue as Float
                invalidate()
            }
            animator.repeatCount = ValueAnimator.INFINITE // Infinite animation loop
            animator.start()
        }
    }
}


