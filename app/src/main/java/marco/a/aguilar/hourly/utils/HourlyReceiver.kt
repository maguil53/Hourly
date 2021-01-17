package marco.a.aguilar.hourly.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

private const val TAG = "HourlyReceiver"

class HourlyReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {

        // For Testing purposes only
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Log.d(TAG, log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }

    }
}