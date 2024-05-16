package com.tutorials.mc.retrofittutorial2

import com.tutorials.mc.retrofittutorial2.ui.TempValuesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenMeteoAPIService {
    @GET("/v1/forecast?latitude=52.52&longitude=13.41&daily=temperature_2m_min,temperature_2m_max")
    suspend fun getCurrentTemperature(): TempValuesDto

    @GET("/v1/era5")
    suspend fun getPreviousTemperature(
        @Query("latitude") latitude: Double=52.52,
        @Query("longitude") longitude: Double=13.41,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") fields: String = "temperature_2m_max,temperature_2m_min"
    ): TempValuesDto

    @GET("/v1/forecast?latitude=52.52&longitude=13.41&past_days=10&daily=temperature_2m_min,temperature_2m_max")
    suspend fun getPat10DaysTemperature(): TempValuesDto
}