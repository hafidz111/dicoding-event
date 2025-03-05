package com.example.dicodingevent.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingevent.repository.EventRepository

class UpcomingViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    fun getEvent(query: String) = eventRepository.getEventQuery(1, query)
    fun setQuery(query: String) {
        _query.value = query

    }
}