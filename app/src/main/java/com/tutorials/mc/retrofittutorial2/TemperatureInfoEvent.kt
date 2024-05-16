package com.tutorials.mc.retrofittutorial2

sealed interface TemperatureInfoEvent {

    object SaveTemperatureInfo: TemperatureInfoEvent
    data class SetInputDate(val inputDate: String): TemperatureInfoEvent
    data class SetDate(val date: String): TemperatureInfoEvent
    data class SetMinimumTemperature(val minimumTemperature: Double): TemperatureInfoEvent
    data class SetMaximumTemperature(val maximumTemperature: Double): TemperatureInfoEvent

    data class GetTemperatureInfo(val date: String): TemperatureInfoEvent
}