package com.example.dicodingevent.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingevent.data.local.entity.FavoriteEntity
import com.example.dicodingevent.data.local.room.FavoriteDao
import com.example.dicodingevent.utils.AppExecutors

class FavoriteRepository private constructor(
    private val favoriteDao: FavoriteDao,
    private val appExecutors: AppExecutors,
) {
    fun getFavoriteEvents(): LiveData<Result<List<FavoriteEntity>>> =
        liveData {
            emit(Result.Loading)
            val localData: LiveData<Result<List<FavoriteEntity>>> =
                favoriteDao.getFavoriteEvent().map { Result.Success(it) }
            emitSource(localData)
        }

    fun setFavoriteEvent(
        id: Int,
        event: FavoriteEntity,
        favoriteState: Boolean,
    ) {
        appExecutors.diskIO.execute {
            if (favoriteState) {
                event.isfavorite = true
                favoriteDao.updateEvent(event)
                favoriteDao.insertFavorite(event)
            } else {
                favoriteDao.updateEvent(event)
                favoriteDao.deleteEvent(id)
            }
        }
    }


    companion object {
        @Volatile
        private var instance: FavoriteRepository? = null

        fun getInstance(
            favoriteDao: FavoriteDao,
            appExecutors: AppExecutors,
        ): FavoriteRepository =
            instance ?: synchronized(this) {
                instance ?: FavoriteRepository(favoriteDao, appExecutors)
            }.also { instance = it }
    }
}
