package com.tutorials.mc.retrofittutorial2

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorials.mc.retrofittutorial2.ui.TempValuesDto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.http.Query

class MyViewModel(
    private val dao: TemperatureInfoDao
) : ViewModel() {
    val _data = mutableStateOf(TemperatureInfo( "",0.0, 0.0))
    val back = mutableStateOf(TemperatureInfo( "",0.0, 0.0))
    val networkAndDatabaseStatus = mutableStateOf<NetworkAndDatabaseStatus>(NetworkAndDatabaseStatus.IDLE)

    suspend fun fetchCurrentTemp(inputDate: String) {
        try {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.LOADING
            val currentTemp = RetrofitClient.openMeteoAPIService.getCurrentTemperature()
            _data.value = mapToTemperatureInfo(currentTemp, inputDate)
            Log.d("MainActivityee", "Updated data: ${_data.value}")
            saveToDatabase(_data.value)
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.SUCCESS
        } catch (e: Exception) {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.ERROR
            Log.e("MainActivityee", "Error fetching current temperature: ${e.message}")
            fetchFromDatabase(inputDate)

        }
    }

    suspend fun fetchPast10DaysTemp(inputDate: String){
        Log.d("MainActivityee", "MyEntity name past10 ")
        try {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.LOADING
            val currentTemp = RetrofitClient.openMeteoAPIService.getPat10DaysTemperature()
            _data.value = mapToTemperatureInfo(currentTemp, inputDate)
            Log.d("MainActivityee", "Updated data: ${_data.value}")
            saveToDatabase(_data.value)
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.SUCCESS

        } catch (e: Exception) {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.ERROR
            Log.e("MainActivityee", "Error fetching past 10 days temperature: ${e.message}")
            fetchFromDatabase(inputDate)
        }
    }

    suspend fun fetchPreviousTemp(inputDate: String){
        Log.d("MainActivityee", "MyEntity name previous")
        try {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.LOADING
            val currentTemp = RetrofitClient2.openMeteoAPIService.getPreviousTemperature(latitude=52.52,
            longitude=13.41,startDate=inputDate,endDate=inputDate,
            fields = "temperature_2m_max,temperature_2m_min")
            _data.value = mapToTemperatureInfo(currentTemp, inputDate)
            Log.d("MainActivityee", "Updated data: ${_data.value}")
            saveToDatabase(_data.value)
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.SUCCESS
        } catch (e: Exception) {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.ERROR
            Log.e("MainActivityee", "Error fetching previous temperature: ${e.message}")
            fetchFromDatabase(inputDate)
        }
    }

    suspend fun fetchFutureTemperature(inputDate: String, dates: List<String>){
        Log.d("MainActivityee", "MyEntity name future")
        try {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.LOADING
            for (i in dates){
                val currentTemp = RetrofitClient2.openMeteoAPIService.getPreviousTemperature(latitude=52.52,
                longitude=13.41,startDate=i,endDate=i,
                fields = "temperature_2m_max,temperature_2m_min")
                back.value = mapToTemperatureInfo(currentTemp, i)
                Log.d("MainActivityee", "Updated data: ${back.value}")


                saveToDatabase(back.value)
            }
            _data.value.maximumTemperature = dao.getTemperatureInfos(dates)[0].max
            _data.value.minimumTemperature = dao.getTemperatureInfos(dates)[0].min
            _data.value.date = inputDate
            Log.d("MainActivityee", "Updated data future: ${_data.value}")
            saveToDatabase(_data.value)
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.SUCCESS
        } catch (e: Exception) {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.ERROR
            Log.e("MainActivityee", "Error fetching future temperature: ${e.message}")
            fetchFromDatabase(inputDate)
        }
    }

    private suspend fun saveToDatabase(value: TemperatureInfo) {
        Log.d("MainActivityee", "MyEntity name save: ${value.date}")
        dao.upsertTemperatureInfo(value)
    }

    private suspend fun fetchFromDatabase(inputDate: String): TemperatureInfo{
        val temperatureInfo = dao.getTemperatureInfo(inputDate)
        if (temperatureInfo.isEmpty()) {
            networkAndDatabaseStatus.value = NetworkAndDatabaseStatus.DATABASEERROR
            return TemperatureInfo("", 0.0, 0.0)
        }
        Log.d("MainActivityee", "MyEntity name fetchedfromdatabase: ${_data.value.date}")
        _data.value = temperatureInfo.first()
        return temperatureInfo.first()

    }


    private fun mapToTemperatureInfo(temperatureInfo: TempValuesDto, inputDate: String): TemperatureInfo {
        val daily = temperatureInfo.daily
        val timeList = daily.time
        val minTempList = daily.temperature_2m_min
        val maxTempList = daily.temperature_2m_max

        // Loop through the time list to find the index where the date matches the inputDate
        for (index in timeList.indices) {
            if (timeList[index] == inputDate) {
                val date = timeList[index]
                val minTemp = if (index < minTempList.size) minTempList[index] else 0.0
                val maxTemp = if (index < maxTempList.size) maxTempList[index] else 0.0
                return TemperatureInfo(date, minTemp, maxTemp)
            }
        }

         //If no match is found, return a default TemperatureInfo object with empty values
        return TemperatureInfo("", 0.0, 0.0)
    }



}

sealed class NetworkAndDatabaseStatus {
    data object IDLE : NetworkAndDatabaseStatus()
    data object LOADING : NetworkAndDatabaseStatus()
    data object SUCCESS : NetworkAndDatabaseStatus()
    data object ERROR : NetworkAndDatabaseStatus()

    data object DATABASEERROR : NetworkAndDatabaseStatus()
}