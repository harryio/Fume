package io.github.sainiharry.fume

data class Filter(val pollutionLevel: PollutionLevel?, val date: Long?) {

    fun isValid() = pollutionLevel != null || date != null
}