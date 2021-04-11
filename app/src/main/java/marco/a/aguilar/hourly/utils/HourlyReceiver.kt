package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import marco.a.aguilar.hourly.enums.BlockState
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.persistence.AppDatabase

private const val TAG = "HourlyReceiver"

class HourlyReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        val nextHour: Int = intent.getIntExtra("nextHour", -1)

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(context)

                // Evaluate HourBlock that just passed, subtract 1.
                var evaluatedHourBlockId = nextHour - 1

                // Need to update for 12am, since we use 24
                if(evaluatedHourBlockId == 0) evaluatedHourBlockId = 24


                val sharedPreferences = context.getSharedPreferences("Hourly Shared Preferences Key", MODE_PRIVATE)
                var bedtime = sharedPreferences.getInt("Bedtime Start Hour Key", -1)


                /**
                 * If the user hasn't set their sleep schedule yet, then use 1am as the reset point.
                 *
                 * Todo: In MainActivity, save the "Bedtime Start Hour Key" value to 1am if the application
                 *  is being ran for the first time. Also, set the 1am hour block
                 */
                if(bedtime == -1) bedtime = 5


                if(evaluatedHourBlockId != bedtime) {
                    val tasks: List<Task> = database.taskDao().getTasksForHourBlock(evaluatedHourBlockId)

                    val hourBlockIsComplete = Task.checkIfAllTasksAreComplete(tasks)

                    val updatedState: BlockState = if (hourBlockIsComplete) BlockState.COMPLETE else BlockState.INCOMPLETE
                    database.hourBlockDao().updateState(updatedState, evaluatedHourBlockId)

                    /**
                     * Todo: Might delete this line because I don't think we'll need isComplete anymore
                     *  since we're using the state attribute to determine whether an HourBlock is complete
                     *  or not.
                     */
                    database.hourBlockDao().updateIsComplete(hourBlockIsComplete, evaluatedHourBlockId)
                } else {
                    // Clear Tasks table and turn all HourBlocks back to LIMBO
                    database.hourBlockDao().resetHourBlocksState()
                    database.taskDao().clearTaskTable()
                }

            }
        }

        /**
         * Re-create a new alarm becuase setExactAndAllowWhileIdle()
         * is only executes once.
         */
        val alarmHandler = AlarmHandler()
        alarmHandler.setAlarm(context)

    }

}