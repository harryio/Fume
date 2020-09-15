package io.github.sainiharry.fume

import com.github.mikephil.charting.data.Entry
import io.github.sainiharry.fume.repository.AqiData

class AqiEntry(aqiData: AqiData) : Entry(aqiData.timestamp.toFloat(), aqiData.aqi.toFloat()) {

    init {
        data = aqiData
    }
}