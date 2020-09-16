package io.github.sainiharry.fume.repository

data class Filter(val pollutionLevel: PollutionLevel?, val date: Long?) {

    fun isValid() = pollutionLevel != null || date != null
}