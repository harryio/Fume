package io.github.sainiharry.fume.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.plumelabs.lib.bluetooth.FlowBleClient
import com.plumelabs.lib.bluetooth.Measure
import com.plumelabs.lib.bluetooth.MeasurementType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface AqiRepository {

    fun connect()

    fun getAqiData(): LiveData<List<AqiData>>

    fun disconnect()
}

class AqiRepositoryImpl(
    context: Context,
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher
) : AqiRepository {

    private val aqiDao =
        Room.databaseBuilder(context, AqiDatabase::class.java, DATABASE_NAME).build().aqiDao()

    private val bleClient: FlowBleClient = FlowBleClient.mockInstance()

    private var aqiDataMap = mutableMapOf<MeasurementType, Int>()

    private var aqiData: AqiDataEntity? = null

    override fun connect() {
        val flowBleSyncData = { _: FlowBleClient, syncData: List<Measure> ->
            var timestamp: Long = 0
            syncData.forEach {
                timestamp = it.timestamp
                aqiDataMap[it.type] = it.aqi.toInt()
            }

            // TODO: 14/09/20 rethink this logic
            val createNewAqiData =
                aqiData == null || (((timestamp - aqiData!!.timestamp) / 60) >= 10)

            if (createNewAqiData) {
                aqiData = AqiDataEntity(
                    timestamp,
                    aqiDataMap[MeasurementType.VOC] ?: 0,
                    aqiDataMap[MeasurementType.NO2] ?: 0,
                    aqiDataMap[MeasurementType.PM25] ?: 0,
                    aqiDataMap[MeasurementType.PM10] ?: 0
                )

                coroutineScope.launch(coroutineDispatcher) {
                    aqiData?.let {
                        aqiDao.insert(it)
                    }
                }
            }
        }

        bleClient.connect { client, _ ->
            client.sync(flowBleSyncData)
        }
    }

    override fun getAqiData() = aqiDao.getAqiData()

    override fun disconnect() {
        bleClient.disconnect()
    }
}