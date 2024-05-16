package com.tutorials.mc.retrofittutorial2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class TemperatureInfo(

    @PrimaryKey
    var date: String,
    var minimumTemperature: Double,
    var maximumTemperature: Double,
//    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0
)
