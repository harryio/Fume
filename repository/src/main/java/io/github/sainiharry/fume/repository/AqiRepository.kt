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

    suspend fun connect()

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

    private var previousTimeStamp: Long = 0

    override suspend fun connect() {
        aqiData = aqiDao.getLatestData()

        val flowBleSyncData = { _: FlowBleClient, syncData: List<Measure> ->
            if (syncData.isNotEmpty()) {
                //Peek timestamp
                val timestamp: Long = syncData[0].timestamp
                if (previousTimeStamp == 0L) {
                    //If this is the first data collection, only send the latest data
                    previousTimeStamp = timestamp
                }

                //when timestamp is changed dump all data into a new AqiData and start collecting data for new timestamp
                val createNewAqiData = timestamp != previousTimeStamp

                if (createNewAqiData) {
                    //represents latest data for a minute-length bucket
                    val localAqiData = AqiDataEntity(
                        previousTimeStamp,
                        aqiDataMap[MeasurementType.VOC] ?: 0,
                        aqiDataMap[MeasurementType.NO2] ?: 0,
                        aqiDataMap[MeasurementType.PM25] ?: 0,
                        aqiDataMap[MeasurementType.PM10] ?: 0
                    )

                    val saveToDatabase =
                        aqiData == null || ((localAqiData.timestamp - aqiData!!.timestamp) / 60 >= 10)
                    if (saveToDatabase) {
                        coroutineScope.launch(coroutineDispatcher) {
                            aqiDao.insert(localAqiData)
                            aqiData = localAqiData
                        }
                    }
                }

                syncData.forEach {
                    aqiDataMap[it.type] = it.aqi.toInt()
                }

                previousTimeStamp = timestamp
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