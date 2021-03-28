package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.persistence.AppDatabase

private const val TAG = "HourlyReceiver"

class HourlyReceiver : BroadcastReceiver() {

    private val sharedPrefFile = "marco.a.aguilar.hourly.shared_preference"

    override fun onReceive(context: Context, intent: Intent) {
        val nextHour: Int = intent.getIntExtra("nextHour", -1)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(context)

                // Evaluate HourBlock that just passed
                val evaluatedHourBlockId = nextHour - 1

                val tasks: List<Task> = database.taskDao().getTasksForHourBlock(evaluatedHourBlockId)
                var hourBlockIsComplete = Task.checkIfAllTasksAreComplete(tasks)



                // hourBlockIsComplete will determine the color of the square
                database.hourBlockDao().updateIsComplete(hourBlockIsComplete, evaluatedHourBlockId)

                /**
                 * This part of our code is going to have to wait. Since we want to save the
                 * user's preference for their sleep schedule. Then we'll implement the logic
                 * that will determine which blocks should be evaluated. Right now this is how I
                 * see it. If evaulatedHourBlockId falls in the range of their sleeping schedule,
                 * then we'll mark it as "Complete" (Also, I think this will allow us to get rid
                 * of the TaskType attribute for our Task objects but I don't know yet). I think
                 * we'll end up just leaving it alone and since when the user saves their sleeping
                 * schedule in the preferences we should mark those blocks as "COMPLETE" so they
                 * always show up green. Then if the user ever changes their sleep preference we
                 * should update the database accordingly (mark those HourBlocks as incomplete again
                 * etc). Finally, we'll only clear the Tasks table for every HourBlock except for the
                 * last hour before the user goes to sleep because we still want them to see whether
                 * they got a green/red square. And once the first hour of them waking up is finished
                 * we'll clear that last block.
                 *
                 * Or in Task.checkIfAllTasksAreComplete, return true if the TaskType is RECOVER.
                 * Which of course will require us to save a "task" in each HourBlock meant for "sleep"
                 */

            }
        }

    }

}