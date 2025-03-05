package com.example.dicodingevent.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.repository.EventRepository

class FinishedViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    fun getEvent(query: String) = eventRepository.getEventQuery(0, query)
    fun setQuery(query: String) {
        _query.value = query

    }
}