package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

private const val TAG = "HourlyReceiver"

class HourlyReceiver : BroadcastReceiver() {

    /**
     * The alarm is being sent and the Log is being printed.
     * The Toast didn't work well for testing purposes because
     * it didn't let me know if the alarm was still being sent even if the
     * phone was off (it was!)
     */
    override fun onReceive(context: Context, intent: Intent) {

        Log.d(TAG, "onReceive: Called...")

    }
}