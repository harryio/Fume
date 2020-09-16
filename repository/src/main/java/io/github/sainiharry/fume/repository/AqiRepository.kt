package io.github.sainiharry.fume.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.plumelabs.lib.bluetooth.FlowBleClient
import com.plumelabs.lib.bluetooth.FlowBleException
import com.plumelabs.lib.bluetooth.Measure
import com.plumelabs.lib.bluetooth.MeasurementType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

interface AqiRepository {

    suspend fun connect()

    fun getAqiData(filter: Filter?): LiveData<List<AqiData>>

    fun getBatteryLevel(): LiveData<Int>

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

    private val batteryLevelLiveData = MutableLiveData<Int>()

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

        val batteryLevelRead = { _: FlowBleClient, batteryLevel: Int?, _: FlowBleException? ->
            batteryLevelLiveData.value = batteryLevel
        }

        val batteryObserver = { _: FlowBleClient, batteryLevel: Int ->
            batteryLevelLiveData.value = batteryLevel
        }

        bleClient.connect { client, _ ->
            client.sync(flowBleSyncData)
            client.readBatteryLevel(batteryLevelRead)
            client.observeBatteryLevel(batteryObserver)
        }
    }

    override fun getAqiData(filter: Filter?): LiveData<List<AqiData>> {
        val prefixQuery =
            "SELECT timestamp, MAX(vocAqi, no2Aqi, pm25Aqi, pm10Aqi) AS aqi, vocAqi, no2Aqi, pm25Aqi, pm10Aqi FROM AqiDataEntity"
        val suffixQuery = "ORDER BY timestamp DESC"
        val sqliteQuery = if (filter == null) {
            SimpleSQLiteQuery("$prefixQuery $suffixQuery", null)

        } else if (filter.pollutionLevel != null && filter.date == null) {
            SimpleSQLiteQuery("$prefixQuery ${getSQLiteQuery(filter.pollutionLevel)} $suffixQuery")

        } else if (filter.pollutionLevel == null && filter.date != null) {
            val start = filter.date / 1000
            val end = getEndOfDayTimestamp(filter.date) / 1000
            SimpleSQLiteQuery("$prefixQuery WHERE timestamp >= $start AND timestamp <= $end $suffixQuery")

        } else if (filter.pollutionLevel == null && filter.date == null) {
            SimpleSQLiteQuery("$prefixQuery $suffixQuery", null)

        } else {
            val start = filter.date!! / 1000
            val end = getEndOfDayTimestamp(filter.date) / 1000
            SimpleSQLiteQuery("$prefixQuery ${getSQLiteQuery(filter.pollutionLevel!!)} AND timestamp >= $start AND timestamp <= $end $suffixQuery")
        }

        return aqiDao.getAqiData(sqliteQuery)
    }

    override fun disconnect() {
        bleClient.disconnect()
    }

    override fun getBatteryLevel(): LiveData<Int> = batteryLevelLiveData

    private fun getEndOfDayTimestamp(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.timeInMillis
    }
}