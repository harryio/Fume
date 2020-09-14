package io.github.sainiharry.fume

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.LineDataSet

class AqiLineDataSet constructor(
    private val context: Context,
    yVals: List<BarEntry>?,
    label: String
) : LineDataSet(yVals, label) {

    init {
        resetCircleColors()
    }

    override fun getCircleColor(index: Int): Int {
        val aqi = getEntryForIndex(index).y.toInt()
        val colorResource = when {
            aqi <= 20 -> {
                R.color.aqi_low
            }
            aqi in 21..50 -> {
                R.color.aqi_moderate
            }
            aqi in 51..100 -> {
                R.color.aqi_high
            }
            else -> {
                R.color.aqi_very_high
            }
        }

        return ContextCompat.getColor(context, colorResource)
    }
}