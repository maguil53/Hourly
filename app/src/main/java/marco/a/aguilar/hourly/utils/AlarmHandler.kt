package marco.a.aguilar.hourly.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class AlarmHandler() {

    private var alarmManager: AlarmManager? = null
    private val nextHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1
    private val TAG = "AlarmHandler"

    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarm(context: Context) {
        val intent = Intent(context, HourlyReceiver::class.java)
        intent.putExtra("nextHour", nextHour)

        // Using FLAG_UPDATE_CURRENT didn't work for me.
        val alarmIntent = PendingIntent.getBroadcast(context, 0 , intent, PendingIntent.FLAG_CANCEL_CURRENT)

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /**
         * Set the alarm to start at next hour.
         *  Ex: If it's 2:45pm, Alarm will start at 3pm.
         *
         * If you schedule the alarm for the current hour, then the code
         * inside HourlyReceiver will be executed immediately. We don't want
         * this behavior if the user barely opens the app and sees a red square.
         * This is why we initialize nextHour by adding 1 to the current hour.
         */
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()

            set(Calendar.HOUR_OF_DAY, nextHour)
            set(Calendar.MINUTE, 0)
        }


        /**
         * setExactAndAllowWhileIdle() works even if device
         * is in Doze mode, but will only be called once. So we
         * need to re-create our alarm in HourlyReceiver
         */
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

}