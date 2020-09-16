package io.github.sainiharry.fume.info

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import io.github.sainiharry.fume.R

class DotBulbView : View {

    private val paint = Paint()

    private val radius = 10f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        context?.let {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DotBulbView,
                defStyleAttr, 0
            ).apply {
                try {
                    paint.color = getColor(R.styleable.DotBulbView_dotColor, Color.RED)
                } finally {
                    recycle()
                }

            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val cx = width.toFloat() / 2
            val cy = height.toFloat() / 2

            val numOfCircles = 12
            val rotationDegrees = 360 / numOfCircles.toFloat()
            val skipVal = 180 / rotationDegrees.toInt()

            for (i in 0 until numOfCircles) {
                if (i != skipVal) {
                    canvas.drawCircle(cx, radius, radius, paint)
                }
                canvas.rotate(rotationDegrees, cx, cy)
            }
        }
    }
}