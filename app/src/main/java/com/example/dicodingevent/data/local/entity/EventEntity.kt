package com.example.dicodingevent.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_dicoding")
data class EventEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Int? = null,
    @ColumnInfo(name = "active") val active: Int? = null,
    @ColumnInfo(name = "summary") val summary: String? = null,
    @ColumnInfo(name = "category") val category: String? = null,
    @ColumnInfo(name = "beginTime") val beginTime: String? = null,
    @ColumnInfo(name = "name") val name: String? = null,
    @ColumnInfo(name = "ownerName") val ownerName: String? = null,
    @ColumnInfo(name = "quota") val quota: Int? = null,
    @ColumnInfo(name = "registrants") val registrants: Int? = null,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "link") val link: String? = null,
    @ColumnInfo(name = "mediaCover") val mediaCover: String? = null,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean,
)
