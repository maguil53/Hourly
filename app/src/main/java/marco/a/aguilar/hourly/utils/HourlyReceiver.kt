package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
         */
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                /**
                 * We're subtracting 1 from nextHour because this will give us the hour
                 * that just finished, which is the one we're trying to evaluate.
                 */
                AppDatabase.getInstance(context).hourBlockDao().updateHourBlock(nextHour - 1)
            }
        }

    }
}