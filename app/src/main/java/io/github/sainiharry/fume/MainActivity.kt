package io.github.sainiharry.fume

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.github.sainiharry.fume.repository.AqiData
import io.github.sainiharry.fume.repository.AqiRepositoryImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private val model by viewModels<MainViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(
                    AqiRepositoryImpl(
                        this@MainActivity,
                        lifecycleScope,
                        Dispatchers.IO
                    ), Dispatchers.Main.immediate
                ) as T
            }
        }
    }

    private val aqiValueFormatter = AqiValueFormatter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart.legend.isEnabled = false

        chart.xAxis.isEnabled = true
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.mAxisMaximum = 10f
        chart.xAxis.mAxisMinimum = 0f
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.valueFormatter = AqiValueFormatter()

        chart.axisLeft.isEnabled = true
        chart.axisRight.isEnabled = false

        chart.axisLeft.addLimitLine(
            getLimitLine(
                20f,
                R.string.moderate_pollution,
                R.color.aqi_moderate
            )
        )
        chart.axisLeft.addLimitLine(
            getLimitLine(
                50f,
                R.string.high_pollution,
                R.color.aqi_high
            )
        )
        chart.axisLeft.addLimitLine(
            getLimitLine(
                100f,
                R.string.very_high_pollution,
                R.color.aqi_very_high
            )
        )

        val fakeTopLineDataSet = LineDataSet(listOf(Entry(0f, 150f)), null).apply {
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawValues(false)
        }

        val fakeBottomLineDataSet = LineDataSet(listOf(Entry(0f, 0f)), null).apply {
            setDrawCircleHole(false)
            setDrawCircles(false)
            setDrawValues(false)
        }

        val lineData = LineData(fakeTopLineDataSet, fakeBottomLineDataSet)
        chart.data = lineData

        model.aqiDataLiveData.observe(this) { aqiDatum ->
            lineData.removeDataSet(2)
            lineData.addDataSet(getLineDataSet(aqiDatum))
            lineData.notifyDataChanged()
            chart.notifyDataSetChanged()
        }
    }

    private fun getLineDataSet(aqiDatum: List<AqiData>): LineDataSet {
        val aqiEntries = aqiDatum.map { AqiEntry(it) }
        return LineDataSet(aqiEntries, "AQI").apply {
            circleRadius = 2f
            circleHoleRadius = 1f

            lineWidth = 2f
            color = getColor(R.color.colorPrimaryDark)

            setDrawFilled(true)
            fillColor = getColor(R.color.colorPrimaryDark)
        }.apply {
            valueFormatter = aqiValueFormatter
        }
    }

    private fun getLimitLine(limit: Float, @StringRes label: Int, @ColorRes color: Int) =
        LimitLine(limit, getString(label)).apply {
            lineColor = ContextCompat.getColor(this@MainActivity, color)
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            textSize = 8f
            enableDashedLine(10f, 10f, 0f)
            lineWidth = 2f
        }
//    private fun setupLineGraph() {
//        val layoutInflater = LayoutInflater.from(this)
//        enumValues<MeasurementType>().forEach { measurementType ->
//            layoutInflater.inflate(R.layout.chart_layout, root, true)
//            val chart = root.getChildAt(measurementType.ordinal) as LineChart
//            chart.legend.isEnabled = false
//
//            chart.xAxis.isEnabled = true
//            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//            chart.xAxis.mAxisMaximum = 10f
//            chart.xAxis.mAxisMinimum = 0f
//            chart.xAxis.setDrawGridLines(false)
//
//            chart.axisLeft.isEnabled = false
//            chart.axisRight.isEnabled = false
//
//            chart.axisLeft.setDrawGridLinesBehindData(true)
//            chart.xAxis.setDrawGridLinesBehindData(true)
//            chart.axisLeft.addLimitLine(
//                getLimitLine(
//                    20f,
//                    R.string.moderate_pollution,
//                    R.color.aqi_moderate
//                )
//            )
//            chart.axisLeft.addLimitLine(
//                getLimitLine(
//                    50f,
//                    R.string.high_pollution,
//                    R.color.aqi_high
//                )
//            )
//            chart.axisLeft.addLimitLine(
//                getLimitLine(
//                    100f,
//                    R.string.very_high_pollution,
//                    R.color.aqi_very_high
//                )
//            )
//
//            val fakeBottomLineDataSet = LineDataSet(listOf(Entry(0f, 150f)), null).apply {
//                setDrawCircleHole(false)
//                setDrawCircles(false)
//                setDrawValues(false)
//            }
//
//            val fakeRightLineDataSet = LineDataSet(listOf(Entry(100f, 0f)), null).apply {
//                setDrawCircleHole(false)
//                setDrawCircles(false)
//                setDrawValues(false)
//            }
//
//            val lineDataSet = LineDataSet(null, measurementType.name).apply {
//                circleRadius = 2f
//                circleHoleRadius = 1f
//
//                lineWidth = 2f
//                color = getChartFillColor(measurementType)
//
//                setDrawFilled(true)
//                fillColor = getChartFillColor(measurementType)
//            }
//
//            val lineData = LineData(lineDataSet, fakeBottomLineDataSet, fakeRightLineDataSet)
//            chart.data = lineData
//
//            model.aqiLiveData.observe(this) { aqiData ->
//                val aqi = aqiData.getAqi(measurementType)
//                lineDataSet.addEntry(Entry(aqiData.timestamp.toFloat(), aqi.toFloat()))
//                lineDataSet.notifyDataSetChanged()
//                lineData.notifyDataChanged()
//
//                chart.notifyDataSetChanged()
//                chart.invalidate()
//            }
//        }
//    }
//
//    private fun getLimitLine(limit: Float, @StringRes label: Int, @ColorRes color: Int) =
//        LimitLine(limit, getString(label)).apply {
//            lineColor = ContextCompat.getColor(this@MainActivity, color)
//            labelPosition = LimitLabelPosition.RIGHT_TOP
//            textSize = 8f
//            enableDashedLine(10f, 10f, 0f)
//            lineWidth = 2f
//        }
//
//    private fun getChartFillColor(measurementType: MeasurementType): Int {
//        val colorInt = when (measurementType) {
//            MeasurementType.VOC -> R.color.pink
//            MeasurementType.NO2 -> R.color.green
//            MeasurementType.PM25 -> R.color.blue
//            MeasurementType.PM10 -> R.color.lavendar
//        }
//
//        return ContextCompat.getColor(this, colorInt)
//    }
}