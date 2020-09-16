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

    val emptyTextVisible: LiveData<Boolean>

    val emptyFilterResultsVisible: LiveData<Boolean>

    private val filtersLiveData = MutableLiveData<Filter?>()

    init {
        aqiDataLiveData = Transformations.switchMap(filtersLiveData) {
            aqiRepository.getAqiData(it)
        }

        emptyTextVisible = Transformations.map(aqiDataLiveData) {
            it.isEmpty() && filtersLiveData.value == null
        }

        emptyFilterResultsVisible = Transformations.map(aqiDataLiveData) {
            it.isEmpty() && filtersLiveData.value != null
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