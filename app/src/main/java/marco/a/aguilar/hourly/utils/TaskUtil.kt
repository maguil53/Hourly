package marco.a.aguilar.hourly.utils

import android.content.ContentValues.TAG
import android.util.Log
import marco.a.aguilar.hourly.models.HourBlock

class TaskUtil {

    companion object {
        fun calculateIsComplete(hourBlock: HourBlock): Boolean {
            val tasks = hourBlock.taskList
            var isComplete = false;

            // This is so clean!!
            isComplete = tasks.all { isComplete }

            tasks.forEach {
                Log.d(TAG, "calculateIsComplete: taskdescription ${it.description}")
                Log.d(TAG, "calculateIsComplete: isComplete ${it.isComplete}")
            }

            Log.d(TAG, "calculateIsComplete: checking... $isComplete")


            return isComplete
        }
    }


}