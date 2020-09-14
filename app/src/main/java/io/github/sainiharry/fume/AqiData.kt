package io.github.sainiharry.fume

import com.plumelabs.lib.bluetooth.MeasurementType

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
}