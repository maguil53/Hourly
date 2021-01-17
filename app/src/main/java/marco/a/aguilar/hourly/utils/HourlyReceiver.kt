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

    /**
     * The alarm is being sent and the Log is being printed.
     * The Toast didn't work well for testing purposes because
     * it didn't let me know if the alarm was still being sent even if the
     * phone was off (it was!)
     */
    override fun onReceive(context: Context, intent: Intent) {

        val nextHour: Int = intent.getIntExtra("nextHour", -1)

        Log.d(TAG, "onReceive: NextHour: $nextHour")

        /**
         * Everything seems to be working, now I'm gonna see if I can change
         * an HourBlock via the Database using nextHour.
         *
         * Just a simple toggle from isComplete should suffice.
         *
         * THIS WORKS! The HourBlock is updated in the Database and we can see the LiveData
         * updating our UI!
         *
         * Our next task is to run one coroutine, and once we are done with that one, we use the value
         * for our next coroutine, or to continue (I don't think we need another coroutine).
         *
         * So what we're going to do is Grab an HourBlock and iterate through it's list of tasks.
         * We're going to check if all tasks have isComplete set to True.
         * Once we finish doing this, then we'll pass this onto
         *      AppDatabase.getInstance(context).hourBlockDao().updateHourBlock(isComplete, nextHour - 1)
         *
         *
         * CRAP I JUST REALIZED WE CAN'T CONTINUE UNTIL WE HAVE OUR TASKS TABLE SET UP!!!
         * Why? Because we don't store Tasks in our hour_blocks table. So we need to use
         * the nextHour - 1 id to get all the Tasks and then do our stuff....wait this might be kinda easy
         */
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "onReceive: Broadcast Received")

                val database = AppDatabase.getInstance(context)

                // Evaluate HourBlock that just passed
                val evaluatedHourBlockId = nextHour - 1
                Log.d(TAG, "onReceive: evaluatedHourBlock: $evaluatedHourBlockId")
                
                val tasks: List<Task> = database.taskDao().getTasksForHourBlock(evaluatedHourBlockId)
                var hourBlockIsComplete = Task.checkIfAllTasksAreComplete(tasks)

                // hourBlockIsComplete will determine the color of the square
                database.hourBlockDao().updateIsComplete(hourBlockIsComplete, evaluatedHourBlockId)
            }
        }

    }
}