package com.tutorials.mc.retrofittutorial2

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [TemperatureInfo::class],
    version = 1
)
abstract class TemperatureInfoDatabase: RoomDatabase() {
    abstract val dao:TemperatureInfoDao


}