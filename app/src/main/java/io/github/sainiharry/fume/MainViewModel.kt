package io.github.sainiharry.fume

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

    val aqiDataLiveData: LiveData<List<AqiData>>

    private val filtersLiveData = MutableLiveData<Filter?>()

    init {
        aqiDataLiveData = Transformations.switchMap(filtersLiveData) {
            aqiRepository.getAqiData(it)
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