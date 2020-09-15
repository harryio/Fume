package io.github.sainiharry.fume

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sainiharry.fume.repository.AqiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    private val apiRepository: AqiRepository,
    coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val aqiDataLiveData = apiRepository.getAqiData()

    init {
        viewModelScope.launch(coroutineDispatcher) {
            apiRepository.connect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        apiRepository.disconnect()
    }
}