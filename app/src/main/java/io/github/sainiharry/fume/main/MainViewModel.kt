package io.github.sainiharry.fume.main

import androidx.lifecycle.*
import io.github.sainiharry.fume.repository.AqiData
import io.github.sainiharry.fume.repository.AqiRepository
import io.github.sainiharry.fume.repository.Filter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    private val aqiRepository: AqiRepository,
    coroutineDispatcher: CoroutineDispatcher
) : ViewModel() {

    val aqiData: LiveData<List<AqiData>>

    val emptyTextVisible: LiveData<Boolean>

    val emptyFilterResultsVisible: LiveData<Boolean>

    val batteryLevel: LiveData<Int>

    val batteryLevelVisible: LiveData<Boolean>

    private val filtersLiveData = MutableLiveData<Filter?>()

    init {
        aqiData = Transformations.switchMap(filtersLiveData) {
            aqiRepository.getAqiData(it)
        }

        emptyTextVisible = Transformations.map(aqiData) {
            it.isEmpty() && filtersLiveData.value == null
        }

        emptyFilterResultsVisible = Transformations.map(aqiData) {
            it.isEmpty() && filtersLiveData.value != null
        }

        batteryLevel = aqiRepository.getBatteryLevel()

        batteryLevelVisible = Transformations.map(batteryLevel) {
            it != null
        }

        viewModelScope.launch(coroutineDispatcher) {
            aqiRepository.connect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        aqiRepository.disconnect()
    }

    fun handleFilters(filter: Filter?) {
        filtersLiveData.value = filter
    }
}