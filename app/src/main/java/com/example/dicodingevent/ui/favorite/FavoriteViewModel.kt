package com.example.dicodingevent.ui.favorite

import androidx.lifecycle.ViewModel
import com.example.dicodingevent.repository.FavoriteRepository

class FavoriteViewModel(
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {
    fun getFavoriteEvent() = favoriteRepository.getFavoriteEvents()
}
