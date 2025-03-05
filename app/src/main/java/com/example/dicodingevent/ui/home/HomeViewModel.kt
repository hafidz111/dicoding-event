package com.example.dicodingevent.ui.home

import androidx.lifecycle.ViewModel
import com.example.dicodingevent.repository.EventRepository

class HomeViewModel(private val eventRepository: EventRepository) : ViewModel() {

    fun getEvent(active: Int) = eventRepository.getEvent(active, "")
}