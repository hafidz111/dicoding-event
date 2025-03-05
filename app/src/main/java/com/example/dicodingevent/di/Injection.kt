package com.example.dicodingevent.di

import android.content.Context
import com.example.dicodingevent.data.local.room.EventDatabase
import com.example.dicodingevent.data.local.room.FavoriteDatabase
import com.example.dicodingevent.data.remote.retrofit.ApiConfig
import com.example.dicodingevent.repository.EventRepository
import com.example.dicodingevent.repository.FavoriteRepository
import com.example.dicodingevent.utils.AppExecutors

object Injection {
    fun provideEventRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val eventDao = database.eventDao()
        val appExecutors = AppExecutors()
        return EventRepository.getInstance(apiService, eventDao, appExecutors)
    }

    fun provideFavoriteRepository(context: Context): FavoriteRepository {
        val database = FavoriteDatabase.getInstance(context)
        val favoriteDao = database.favoriteDao()
        val appExecutors = AppExecutors()
        return FavoriteRepository.getInstance(favoriteDao, appExecutors)
    }
}