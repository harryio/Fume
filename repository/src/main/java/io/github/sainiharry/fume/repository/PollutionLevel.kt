package io.github.sainiharry.fume.repository

enum class PollutionLevel {

    LOW, MODERATE, HIGH, VERY_HIGH
}

fun getSQLiteQuery(pollutionLevel: PollutionLevel) = when (pollutionLevel) {
    PollutionLevel.LOW -> "WHERE aqi < 20"
    PollutionLevel.MODERATE -> "WHERE aqi > 20 AND aqi <= 50"
    PollutionLevel.HIGH -> "WHERE aqi > 50 AND aqi <= 100"
    PollutionLevel.VERY_HIGH -> "WHERE aqi > 100"
}