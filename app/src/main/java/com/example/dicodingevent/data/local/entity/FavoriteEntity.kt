package com.example.dicodingevent.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_event")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = false) var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,
    var category: String? = null,
    var isfavorite: Boolean
)