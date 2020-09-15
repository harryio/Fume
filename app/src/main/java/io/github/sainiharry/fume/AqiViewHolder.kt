package io.github.sainiharry.fume

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.github.sainiharry.fume.repository.AqiData
import java.text.SimpleDateFormat
import java.util.*

class AqiViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_aqi, parent, false)
) {

    private val sdf = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    private val aqiTextView = itemView.findViewById<TextView>(R.id.aqi)
    private val pollutionLevelTextView = itemView.findViewById<TextView>(R.id.pollution_level)
    private val dateTextView = itemView.findViewById<TextView>(R.id.date)
    private val vocChip = itemView.findViewById<Chip>(R.id.voc_chip)
    private val no2Chip = itemView.findViewById<Chip>(R.id.no2_chip)
    private val pm25Chip = itemView.findViewById<Chip>(R.id.pm25_chip)
    private val pm10Chip = itemView.findViewById<Chip>(R.id.pm10_chip)

    fun bind(aqiData: AqiData) {
        aqiTextView.text = aqiData.aqi.toString()
        aqiTextView.setTextColor(getAqiColor(aqiData.aqi))
        pollutionLevelTextView.text = getPollutionLevel(aqiData.aqi)
        dateTextView.text = sdf.format(Date(aqiData.timestamp * 1000))
        vocChip.text = itemView.context.getString(
            R.string.voc,
            aqiData.vocAqi
        )
        no2Chip.text = itemView.context.getString(R.string.no2, aqiData.no2Aqi)
        pm25Chip.text = itemView.context.getString(R.string.pm25, aqiData.pm25Aqi)
        pm10Chip.text = itemView.context.getString(R.string.pm10, aqiData.pm10Aqi)
    }

    private fun getAqiColor(aqi: Int): Int {
        val colorResource = when {
            aqi <= 20 -> {
                R.color.aqi_low
            }
            aqi in 21..50 -> {
                R.color.aqi_moderate
            }
            aqi in 51..100 -> {
                R.color.aqi_high
            }
            else -> {
                R.color.aqi_very_high
            }
        }

        return ContextCompat.getColor(itemView.context, colorResource)
    }

    private fun getPollutionLevel(aqi: Int): String {
        val stringResource = when {
            aqi <= 20 -> {
                R.string.low_pollution
            }
            aqi in 21..50 -> {
                R.string.moderate_pollution
            }
            aqi in 51..100 -> {
                R.string.high_pollution
            }
            else -> {
                R.string.very_high_pollution
            }
        }

        return itemView.context.getString(stringResource)
    }
}