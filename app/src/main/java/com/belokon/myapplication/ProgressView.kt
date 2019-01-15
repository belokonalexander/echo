package com.belokon.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.support.v7.content.res.AppCompatResources
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        setBackgroundColor(Color.RED)

    }

    val animation = RotateAnimation(0f, 360f,
        Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f).apply {
        duration = 1000
        interpolator = LinearInterpolator()
        repeatMode = Animation.RESTART
        repeatCount = Animation.INFINITE
    }

    val spinner = ImageView(context).apply {
        this.setImageResource(R.drawable.l)
        addView(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        spinner.startAnimation(animation)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animation.cancel()
    }


}

class ProgressSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val length = 270f
    private var currentPos = 0f
    private val arcRect = RectF()
    private val gradient = LinearGradient(0f, 0f, 0f, 10f, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR)
    private val colorStart = resources.getColor(R.color.green1)

    private val arcPaint = Paint().apply {
        //color = Color.RED
        strokeWidth = 10f
        isAntiAlias = true
        style = Paint.Style.STROKE
        //shader = gradient
    }

    private val linePaint = Paint().apply {
        strokeWidth = 10f
        isAntiAlias = true
        style = Paint.Style.STROKE
        //shader = gradient
    }



    private val animator = ValueAnimator.ofFloat(0f, 360f).apply {
        duration = 10000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            currentPos = it.animatedValue as Float
            invalidate()
        }
    }

    var prev: Int = 0

    var spinner = AppCompatResources.getDrawable(context, R.drawable.l)!!

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /*val padding = arcPaint.strokeWidth / 2
        arcRect.set(0f + padding, 0f + padding, width - padding, height - padding)

        val w = canvas.width
        val h = canvas.height
        val halfW = w / 2
        val halfH = h / 2


        val p = Path().apply {
            arcTo(arcRect, currentPos, - length, false)
        }

        val pm = PathMeasure(p, false)
        val coordStart = floatArrayOf(0f, 0f)
        val coordFinish = floatArrayOf(0f, 0f)
        pm.getPosTan(pm.length * 0f, coordStart, null)
        pm.getPosTan(pm.length * 1f, coordFinish, null)

        Log.d("#1020", "start = ${coordStart.toList()}, finish = ${coordFinish.toList()}")

        val colors = intArrayOf(Color.RED, Color.argb(140, 255, 0, 0), Color.argb(70, 255, 0, 0), Color.TRANSPARENT)
        val positions = floatArrayOf(0f, .3f, .7f, 1f)

        // arcPaint.shader = LinearGradient(coordStart[0], coordStart[1], coordFinish[0], coordFinish[1], colors, positions, Shader.TileMode.CLAMP)
        //arcPaint.shader = RadialGradient(coordStart[0], coordStart[1], p, Color.RED, Color.TRANSPARENT, Shader.TileMode.CLAMP)

        linePaint.shader = LinearGradient(0f, halfH.toFloat(), w.toFloat(), halfH.toFloat(), colors, positions, Shader.TileMode.REPEAT)
        //canvas.drawLine(0f, halfH.toFloat(), w.toFloat(), halfH.toFloat(), linePaint)

        arcPaint.shader = LinearGradient(coordStart[0], coordStart[1], coordFinish[0], coordFinish[1], colors, positions, Shader.TileMode.CLAMP)

        canvas.drawArc(arcRect, currentPos, - length, false, arcPaint)
        canvas.drawLine(coordStart[0], coordStart[1], coordFinish[0], coordFinish[1], linePaint)
        //canvas.drawLine(0f, canvas.height / 2f, canvas.width.toFloat(), canvas.height / 2f, linePaint)*/

        //canvas.drawRect(Rect(0, 0, canvas.width, canvas.height), Paint().apply { color = Color.RED })
        spinner.setBounds(0, 0, canvas.width, canvas.height)
        spinner.draw(canvas)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // animator.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}