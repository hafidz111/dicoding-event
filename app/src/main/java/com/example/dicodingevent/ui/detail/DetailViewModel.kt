package com.example.dicodingevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.entity.FavoriteEntity
import com.example.dicodingevent.repository.EventRepository
import com.example.dicodingevent.repository.FavoriteRepository
import kotlinx.coroutines.launch

class DetailViewModel(
    private val eventRepository: EventRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {
    fun getDetailEvent(id: Int) = eventRepository.getDetailEvent(id)
    fun setEvent(event: EventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, favoriteState)
        }
    }

    fun setFavoriteEvent(id: Int, event: FavoriteEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            favoriteRepository.setFavoriteEvent(id, event, favoriteState)
        }
    }
}