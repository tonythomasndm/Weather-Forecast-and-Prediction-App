package com.tutorials.mc.retrofittutorial2.ui

data class Daily(
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val time: List<String>
)