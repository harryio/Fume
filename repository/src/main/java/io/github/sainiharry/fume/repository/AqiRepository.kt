package io.github.sainiharry.fume.repository

import androidx.lifecycle.LiveData
import com.plumelabs.lib.bluetooth.FlowBleClient
import com.plumelabs.lib.bluetooth.Measure
import com.plumelabs.lib.bluetooth.MeasurementType

interface AqiRepository {

    fun connect()

    fun getAqiData(): LiveData<List<AqiData>>

    fun disconnect()
}

internal class AqiRepositoryImpl(): AqiRepository {

    private val bleClient: FlowBleClient = FlowBleClient.mockInstance()

    private var aqiDataMap = mutableMapOf<MeasurementType, Int>()

    private var aqiData: AqiData? = null

    override fun connect() {
        val flowBleSyncData = { _: FlowBleClient, syncData: List<Measure> ->
            var timestamp: Long = 0
            syncData.forEach {
                timestamp = it.timestamp
                aqiDataMap[it.type] = it.aqi.toInt()
            }

            val createNewAqiData =
                aqiData == null || (((timestamp - aqiData!!.timestamp) / 60) >= 10)

            if (createNewAqiData) {
                aqiData = AqiData(
                    timestamp,
                    aqiDataMap[MeasurementType.VOC] ?: 0,
                    aqiDataMap[MeasurementType.NO2] ?: 0,
                    aqiDataMap[MeasurementType.PM25] ?: 0,
                    aqiDataMap[MeasurementType.PM10] ?: 0
                )

                // TODO: 14/09/20 save value to database here
            }
        }

        bleClient.connect { client, _ ->
            client.sync(flowBleSyncData)
        }
    }

    override fun getAqiData(): LiveData<List<AqiData>> {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        bleClient.disconnect()
    }
}