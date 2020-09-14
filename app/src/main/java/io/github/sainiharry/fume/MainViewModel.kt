package io.github.sainiharry.fume

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plumelabs.lib.bluetooth.FlowBleClient
import com.plumelabs.lib.bluetooth.Measure
import com.plumelabs.lib.bluetooth.MeasurementType

class MainViewModel : ViewModel() {

    private val bleClient: FlowBleClient = FlowBleClient.mockInstance()

    private var aqiData: AqiData? = null

    private var aqiDataMap = mutableMapOf<MeasurementType, Int>()

    private val _aqiLiveData = MutableLiveData<AqiData>()
    val aqiLiveData: LiveData<AqiData>
        get() = _aqiLiveData

    init {
        val flowBleSyncData = { _: FlowBleClient, syncData: List<Measure> ->
            var timestamp: Long = 0
            syncData.forEach {
                timestamp = it.timestamp
                aqiDataMap[it.type] = it.aqi.toInt()
            }

            val createNewAqiData =
                aqiData == null || (((timestamp - aqiData!!.timestamp) / 60) >= 10)

            if (createNewAqiData) {
                _aqiLiveData.value = AqiData(
                    timestamp,
                    aqiDataMap[MeasurementType.VOC] ?: 0,
                    aqiDataMap[MeasurementType.NO2] ?: 0,
                    aqiDataMap[MeasurementType.PM25] ?: 0,
                    aqiDataMap[MeasurementType.PM10] ?: 0
                )
            }
        }

        bleClient.connect { client, _ ->
            client.sync(flowBleSyncData)
        }
    }

    override fun onCleared() {
        super.onCleared()
        bleClient.disconnect()
    }

    fun getLiveData(measurementType: MeasurementType) = aqiDataMap[measurementType]
}