package io.github.sainiharry.fume.repository

data class AqiData(
    val timestamp: Long,
    val aqi: Int,
    val vocAqi: Int,
    val no2Aqi: Int,
    val pm25Aqi: Int,
    val pm10Aqi: Int
)