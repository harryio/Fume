package io.github.sainiharry.fume

import com.github.mikephil.charting.data.BarEntry
import io.github.sainiharry.fume.repository.AqiData

class AqiEntry(aqiData: AqiData) :
    BarEntry(aqiData.timestamp.toFloat(), aqiData.aqi.toFloat()) {

    init {
        data = aqiData
    }
}