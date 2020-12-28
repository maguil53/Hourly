package marco.a.aguilar.hourly.utils

import android.content.ContentValues.TAG
import android.util.Log
import marco.a.aguilar.hourly.models.HourBlock

class TaskUtil {

    companion object {
        /**
         * hourBlock.tasks could potentially be null because at the beginnig of the
         * app we have nothing
         */
        fun calculateIsComplete(hourBlock: HourBlock): Boolean {
            val tasks = hourBlock.tasks
            var isComplete = false;

            isComplete = tasks?.all { isComplete }!!

            tasks.forEach {
                Log.d(TAG, "calculateIsComplete: taskdescription ${it.description}")
                Log.d(TAG, "calculateIsComplete: isComplete ${it.isComplete}")
            }

            Log.d(TAG, "calculateIsComplete: checking... $isComplete")


            return isComplete
        }
    }


}