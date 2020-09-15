package io.github.sainiharry.fume

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

private const val TIME_FORMAT = "HH:mm"

class AqiValueFormatter : ValueFormatter() {

    private val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val milliseconds = value.toLong() * 1000
        val date = Date(milliseconds)
        return timeFormat.format(date)
    }
}