package com.example.dicodingevent.data.remote.retrofit

import com.example.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(
        @Query("active") active: Int,
        @Query("q") query: String,
        @Query("limit") limit: Int = 40
    ): Call<EventResponse>

}