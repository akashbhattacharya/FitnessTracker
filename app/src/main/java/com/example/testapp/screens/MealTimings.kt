import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testapp.viewmodel.StepCounterViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SetMealTimingsScreen(navController: NavController) {
    val mealTimeViewModel: StepCounterViewModel = viewModel()
    val context = LocalContext.current

    val breakfastTime by mealTimeViewModel.breakfastTime.observeAsState()
    val lunchTime by mealTimeViewModel.lunchTime.observeAsState()
    val snackTime by mealTimeViewModel.snackTime.observeAsState()
    val dinnerTime by mealTimeViewModel.dinnerTime.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Meal Timings") },
                backgroundColor = MaterialTheme.colors.primarySurface,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MealTimeSettingButton("Breakfast", breakfastTime, { newTime -> mealTimeViewModel.breakfastTime.value = newTime }, context)
            MealTimeSettingButton("Lunch", lunchTime, { newTime -> mealTimeViewModel.lunchTime.value = newTime }, context)
            MealTimeSettingButton("Snack", snackTime, { newTime -> mealTimeViewModel.snackTime.value = newTime }, context)
            MealTimeSettingButton("Dinner", dinnerTime, { newTime -> mealTimeViewModel.dinnerTime.value = newTime }, context)
            Button(
                onClick = { mealTimeViewModel.saveMealTimes(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("Save Meal Times", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MealTimeSettingButton(
    mealName: String,
    time: LocalTime?,
    setTime: (LocalTime) -> Unit,
    context: Context
) {
    val timeString = time?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Not Set"
    Button(
        onClick = {
            showTimePicker(time, setTime, context)
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
    ) {
        Icon(Icons.Filled.Schedule, contentDescription = "Set Time", tint = MaterialTheme.colors.onPrimary)
        Spacer(Modifier.width(8.dp))
        Text("$mealName Time: $timeString", modifier = Modifier.weight(1f))
    }
}

fun showTimePicker(time: LocalTime?, onTimeSet: (LocalTime) -> Unit, context: Context) {
    val timePicker = time ?: LocalTime.now()
    TimePickerDialog(
        context,
        { _, hour, minute -> onTimeSet(LocalTime.of(hour, minute)) },
        timePicker.hour,
        timePicker.minute,
        false  // Change this to 'true' for 24-hour format
    ).show()
}
