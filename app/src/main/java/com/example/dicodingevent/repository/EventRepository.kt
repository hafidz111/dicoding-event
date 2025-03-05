package com.example.dicodingevent.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.room.EventDao
import com.example.dicodingevent.data.remote.response.EventResponse
import com.example.dicodingevent.data.remote.retrofit.ApiService
import com.example.dicodingevent.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutors
) {


    fun getEvent(active: Int, query: String = ""): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        val result = MediatorLiveData<Result<List<EventEntity>>>()
        val client = apiService.getEvents(active, query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful) {
                    appExecutors.diskIO.execute {
                        val events = response.body()?.listEvents
                        val eventList = events?.map { event ->
                            EventEntity(
                                event.id,
                                active,
                                event.summary,
                                event.category,
                                event.beginTime,
                                event.name,
                                event.ownerName,
                                event.quota,
                                event.registrants,
                                event.description,
                                event.link,
                                event.mediaCover,
                                false
                            )
                        }
                        eventDao.insertEvent(eventList ?: emptyList())
                    }
                }

            }

            override fun onFailure(p0: Call<EventResponse>, p1: Throwable) {
                result.value = Result.Error(p1.message.toString())
                Log.e("EventRepository", "getEvent: ${p1.message}")
            }
        })
        val localData: LiveData<Result<List<EventEntity>>> =
            eventDao.getEvent(active).map { Result.Success(it) }
        emitSource(localData)
    }

    fun getEventQuery(active: Int, query: String = ""): LiveData<Result<List<EventEntity>>> =
        liveData {
            emit(Result.Loading)
            val localData: LiveData<Result<List<EventEntity>>> =
                eventDao.getEventQuery(active, "%$query%").map { Result.Success(it) }
            emitSource(localData)
        }

    fun getDetailEvent(id: Int): LiveData<Result<EventEntity>> = liveData {
        emit(Result.Loading)
        val localData: LiveData<Result<EventEntity>> =
            eventDao.getEventDetail(id).map { Result.Success(it) }
        emitSource(localData)
    }

    fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        appExecutors.diskIO.execute {
            if (favoriteState) {
                event.isFavorite = true
                eventDao.updateEvent(event)
            } else {
                eventDao.updateEvent(event)
            }
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null
        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
            appExecutors: AppExecutors
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao, appExecutors)
            }.also {
                instance = it
            }
    }
}