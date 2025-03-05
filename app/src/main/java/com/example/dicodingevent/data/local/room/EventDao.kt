package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingevent.data.local.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM event_dicoding WHERE active = :active ORDER BY beginTime ASC")
    fun getEvent(active: Int): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event_dicoding WHERE active = :active AND name LIKE :query ORDER BY beginTime ASC")
    fun getEventQuery(active: Int, query: String): LiveData<List<EventEntity>>

    @Query("SELECT * FROM event_dicoding Where id = :id")
    fun getEventDetail(id: Int): LiveData<EventEntity>

    @Update
    fun updateEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertEvent(event: List<EventEntity>)

}