package io.github.sainiharry.fume

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {

    private var pollutionLevel: PollutionLevel? = null

    private var date: Long? = null

    private val _filterLiveData = MutableLiveData<Filter?>()
    val filterLiveData: LiveData<Filter?>
        get() = _filterLiveData

    fun handlePollutionLevel(pollutionLevel: PollutionLevel?) {
        this.pollutionLevel = pollutionLevel
    }

    fun handleDate(selectedDate: Long?) {
        date = selectedDate
    }

    fun handleDoneButtonClick() {
        _filterLiveData.value = Filter(pollutionLevel, date)
    }

    fun handleClearButtonClick() {
        _filterLiveData.value = null
    }
}