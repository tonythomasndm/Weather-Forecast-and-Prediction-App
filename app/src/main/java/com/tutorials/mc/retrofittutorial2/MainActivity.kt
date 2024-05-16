package com.tutorials.mc.retrofittutorial2
import android.widget.Toast
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.tutorials.mc.retrofittutorial2.ui.theme.RetrofitTutorial2Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class MainActivity : ComponentActivity() {

    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            TemperatureInfoDatabase::class.java,
            "temperatureInfo.db"
        ).build()
    }
    private val viewModel by viewModels<MyViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MyViewModel(db.dao) as T
                }
            }
        }
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RetrofitTutorial2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                   WeatherData(viewModel=viewModel)
                }
            }
        }

    }
//    // Function to check network connectivity
//    private fun isNetworkAvailable(): Boolean {
//        val connectivityManager =
//            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        val activeNetworkInfo: NetworkInfo? = connectivityManager?.activeNetworkInfo
//        return activeNetworkInfo?.isConnected ?: false
//    }
//
//    // Function to display Snackbar
//    private fun showNetworkSnackbar(view: View, message: String) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
//    }




}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherData(
    modifier: Modifier = Modifier,
    viewModel: MyViewModel
) {

    var userInput by remember { mutableStateOf("") }
    var result by remember {
        mutableStateOf(Result("-1", "", 0.0, 0.0))
    }
    var c by remember {
        mutableStateOf("")
    }
    var showToast by remember { mutableStateOf(false) } // State to control toast display


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,42.dp,16.dp,16.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Temperature Finder",
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))



        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it
                c=""},
            label = { Text("Enter Date (YYYY-MM-DD)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            c="work"

        }) {
            Text("Get Min and Max Temperature")
        }
        if(c=="work"){
            Log.d("MainActivityett", "working... ")
            ShowToastForDateValidity(LocalContext.current, userInput)
            result = checkDateAndPrintMinAndMaxTemp(inputDate = userInput, viewModel = viewModel)
        }

        when (val networkAndDatabaseStatus=viewModel.networkAndDatabaseStatus.value) {

            is NetworkAndDatabaseStatus.ERROR -> {
                ShowToast("Network disconnected")

            }
            is NetworkAndDatabaseStatus.DATABASEERROR  -> {
                ShowToast("Error in retrieving data from database")
            }
            else -> {
                // Handle other cases
            }
        }


        Spacer(modifier = Modifier.height(16.dp))


        if(result.status!="-1"){
            Text(
                text = "User Input Date = ${result.date}",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = result.status,
                fontSize = 16.sp,
                color = Color(30,218,197)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        if(result.status!="Invalid date format - Try again" && result.status!="-1")
        {
            Log.d("TRYSS","---------------------${result.minTemp}")
            Text(text = "Minimum Temperature = ${String.format("%.3f", result.minTemp)} \u2103", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Maximum Temperature = ${String.format("%.3f", result.maxTemp)} \u2103", fontSize = 16.sp)
        }

    }
}
@Composable
fun isValidDate(dateStr: String): Boolean {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.isLenient = false // Disable lenient parsing

    return try {
        dateFormat.parse(dateStr)
        true // Parsing successful, date is valid
    } catch (e: ParseException) {
        false // Parsing failed, date is invalid
    }
}
@Composable
fun ShowToast(message: String) {
    val context = LocalContext.current
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
@Composable
fun ShowToastForDateValidity(context: Context, dateStr: String) {
    val isValid = isValidDate(dateStr)
    if (!isValid) {


        Toast.makeText(context, "Invalid Date: $dateStr", Toast.LENGTH_SHORT).show()
    }

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
@SuppressLint("CoroutineCreationDuringComposition")
fun checkDateAndPrintMinAndMaxTemp(inputDate: String, viewModel: MyViewModel): Result {

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val inputDateTime = try {
        sdf.parse(inputDate)
    } catch (e: Exception) {
        null
    }
    var status = ""
    var minTemp by remember{ mutableDoubleStateOf(0.0) }
    var maxTemp by remember{ mutableDoubleStateOf(0.0) }
    when {
        inputDateTime == null -> {
            status = "Invalid date format - Try again"
        }
        inputDateTime == currentDate -> {
            status = "The date is today"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchCurrentTemp(inputDate)
                Log.d("MainActivityett", "MyEntity name in coutne${viewModel._data} ")
            }
            minTemp = viewModel._data.value.minimumTemperature
            maxTemp = viewModel._data.value.maximumTemperature
            Log.d("MainActivityet", "MyEntity name ${viewModel._data} ")


        }
        isYesterday(inputDateTime, currentDate) -> {
            status = "The date is yesterday"
            Log.d("MainActivityett", "MyEntity sscc name ${viewModel._data} ")
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchPast10DaysTemp(inputDate)
                delay(1000)
            }

            Log.d("MainActivityet", "MyEntity name ${viewModel._data} ")
            minTemp = viewModel._data.value.minimumTemperature
            maxTemp = viewModel._data.value.maximumTemperature

        }
        isTomorrow(inputDateTime, currentDate) -> {
            status = "The date is tomorrow"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchCurrentTemp(inputDate)
                delay(1000)
            }
            minTemp= viewModel._data.value.minimumTemperature
            maxTemp= viewModel._data.value.maximumTemperature
        }
        inputDateTime.before(currentDate) -> {
            status = "Date is in the past"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchPreviousTemp(inputDate)
                delay(1000)
            }
            minTemp= viewModel._data.value.minimumTemperature
            maxTemp= viewModel._data.value.maximumTemperature
        }
        isWithinNextWeek(inputDateTime, currentDate) -> {
            status = "The date is within next week"
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchCurrentTemp(inputDate)
                delay(1000)
            }
            minTemp= viewModel._data.value.minimumTemperature
            maxTemp= viewModel._data.value.maximumTemperature
        }
        inputDateTime.after(currentDate) -> {
            status = "The date is in Future"
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val inputLocalDate = LocalDate.parse(inputDate, formatter)
            val currentLocalDate = LocalDate.now()
            val dates = mutableListOf<String>()

            val startYear = if (inputLocalDate.monthValue < currentLocalDate.monthValue ||
                (inputLocalDate.monthValue == currentLocalDate.monthValue && inputLocalDate.dayOfMonth < currentLocalDate.dayOfMonth)) {
                currentLocalDate.year
            } else {
                currentLocalDate.year - 1
            }

            for (i in 0 until 10) {
                val previousYear = startYear - i
                val previousDate = inputLocalDate.withYear(previousYear)
                dates.add(previousDate.format(formatter))
            }


            CoroutineScope(Dispatchers.IO).launch {
                viewModel.fetchFutureTemperature(inputDate, dates)
                //delay(3000)
            }
            minTemp = viewModel._data.value.minimumTemperature
            maxTemp = viewModel._data.value.maximumTemperature

        }
        else -> {
            status="Unknown status"
        }
    }
            Log.d("TRYSS", "Possible dates 10" +
                    "" +
                    "" +
                    " years min:${minTemp}  max:${maxTemp}")

    return Result(status,date=inputDate, minTemp = minTemp, maxTemp = maxTemp)
}

fun isYesterday(inputDate: java.util.Date, currentDate: java.util.Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = currentDate
    cal.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = cal.time
    return inputDate == yesterday
}

fun isTomorrow(inputDate: java.util.Date, currentDate: java.util.Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = currentDate
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = cal.time
    return inputDate == tomorrow
}

fun isWithinNextWeek(inputDate: java.util.Date, currentDate: java.util.Date): Boolean {
    val cal = Calendar.getInstance()
    cal.time = currentDate
    cal.add(Calendar.DAY_OF_YEAR, 7)
    val nextWeek = cal.time
    return inputDate.before(nextWeek)
}