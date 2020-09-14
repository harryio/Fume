package io.github.sainiharry.fume

import androidx.lifecycle.ViewModel
import io.github.sainiharry.fume.repository.AqiRepository

class MainViewModel(private val apiRepository: AqiRepository) : ViewModel() {

    val aqiDataLiveData = apiRepository.getAqiData()

    init {
        apiRepository.connect()
    }

    override fun onCleared() {
        super.onCleared()
        apiRepository.disconnect()
    }
}