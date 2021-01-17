package marco.a.aguilar.hourly.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class AlarmHandler(val context: Context) {
    /**
     * Todo(): Set Alarm at a time further away from you
     */

    private var alarmManager: AlarmManager? = null
    private var alarmIntent: PendingIntent

    init {
        alarmIntent = Intent(context, HourlyReceiver::class.java).let { intent ->
            /**
             * Just for fun, here's some more info on "let"
             * https://stackoverflow.com/questions/58606651/what-is-the-purpose-of-let-keyword-in-kotlin
             */
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
    }

    fun setAlarm() {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set the alarm to start at 2:00 a.m.
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 27)
        }

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60 * 20,
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