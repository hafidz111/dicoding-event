package com.example.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.dicodingevent.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_event WHERE isfavorite = 1")
    fun getFavoriteEvent(): LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavorite(event: FavoriteEntity)

    @Update
    fun updateEvent(event: FavoriteEntity)

    @Query("DELETE FROM favorite_event WHERE id = :id")
    fun deleteEvent(id: Int)

}
