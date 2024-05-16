package com.tutorials.mc.retrofittutorial2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

data class minAndMaxTemperature(val min: Double, val max: Double)
@Dao
interface TemperatureInfoDao {
    @Upsert
    suspend fun upsertTemperatureInfo(temperatureInfo: TemperatureInfo)

    @Query("SELECT * FROM temperatureInfo WHERE date = :inputDate")
    suspend fun getTemperatureInfo(inputDate: String): List<TemperatureInfo>

    @Query("SELECT AVG(minimumTemperature) as min, AVG(maximumTemperature) as max FROM temperatureInfo WHERE date in (:dates)")
    suspend fun getTemperatureInfos(dates: List<String>): List<minAndMaxTemperature>

}