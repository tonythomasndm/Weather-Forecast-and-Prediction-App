package com.tutorials.mc.retrofittutorial2

data class TemperatureInfoState(
    val temperatureInfos: List<TemperatureInfo> = emptyList(),
    val date: String = "",
    val inputDate: String = "",
    val minimumTemperature: Double = 0.0,
    val maximumTemperature: Double = 0.0,
    val isLoading: Boolean =false
)