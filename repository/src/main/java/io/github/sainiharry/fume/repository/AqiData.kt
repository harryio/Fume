package io.github.sainiharry.fume.repository

import com.plumelabs.lib.bluetooth.MeasurementType
import kotlin.math.max

data class AqiData(
    val timestamp: Long,
    private val vocAqi: Int,
    private val no2Aqi: Int,
    private val pm25Aqi: Int,
    private val pm10Aqi: Int
) {
    fun getAqi(measurementType: MeasurementType) = when (measurementType) {
        MeasurementType.VOC -> vocAqi
        MeasurementType.NO2 -> no2Aqi
        MeasurementType.PM25 -> pm25Aqi
        MeasurementType.PM10 -> pm10Aqi
    }

    fun getAqi() = max(vocAqi, max(no2Aqi, max(pm25Aqi, pm10Aqi)))
}