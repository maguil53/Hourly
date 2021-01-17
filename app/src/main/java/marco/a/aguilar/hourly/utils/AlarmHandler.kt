package marco.a.aguilar.hourly.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class AlarmHandler(val context: Context) {

    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent
    private val nextHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1

    init {
        val intent = Intent(context, HourlyReceiver::class.java)
        intent.putExtra("nextHour", nextHour)

        alarmIntent = PendingIntent.getBroadcast(context, 0 , intent, 0)
    }

    fun setAlarm() {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm to start at next hour
        /**
         * Set the alarm to start at next hour.
         *  Ex: If it's 2:45pm, Alarm will start at 3pm and should
         *  update HourBlock for Hour 2
         */
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()

            Log.d(ContentValues.TAG, "AlarmHandler: Next Hour Coming up: $nextHour")

            set(Calendar.HOUR_OF_DAY, nextHour)
            set(Calendar.MINUTE, 0)
        }

        /**
         * setRepeating() helps specify a custom interval in this case, every Hour
         *
         * Formula: 1000 * 60 * mins
         */
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 60,
            alarmIntent
        )
    }

    /**
     * Make sure alarmIntent is initialized before calling cancelAlarm()
     * or you'll get an exception
     */
    fun cancelAlarm() {
        // If the alarm has been set, cancel it.
        alarmManager?.cancel(alarmIntent)
    }

}