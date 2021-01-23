package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.persistence.AppDatabase

private const val TAG = "HourlyReceiver"

class HourlyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val nextHour: Int = intent.getIntExtra("nextHour", -1)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(context)

                // Evaluate HourBlock that just passed
                val evaluatedHourBlockId = nextHour - 1

                val tasks: List<Task> = database.taskDao().getTasksForHourBlock(evaluatedHourBlockId)
                val hourBlockIsComplete = Task.checkIfAllTasksAreComplete(tasks)

                // hourBlockIsComplete will determine the color of the square
                database.hourBlockDao().updateIsComplete(hourBlockIsComplete, evaluatedHourBlockId)


            }
        }

    }
}