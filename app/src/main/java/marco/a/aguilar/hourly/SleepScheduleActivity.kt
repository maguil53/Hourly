package marco.a.aguilar.hourly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_sleep_schedule.*
import java.util.*

class SleepScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    lateinit var mTimePickerDialog: TimePickerDialog

    private val TAG = "SleepScheduleActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_schedule)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initTimePicker()

        button_bedtime.setOnClickListener {
            mTimePickerDialog.show(supportFragmentManager, "TimePickerDialog")
        }

        number_picker_hours_of_sleep.maxValue = 8
        number_picker_hours_of_sleep.minValue = 1

    }

    private fun initTimePicker() {
        // Will help us get Hour based on user's TimeZone
        val calendar = GregorianCalendar(TimeZone.getDefault())

        mTimePickerDialog = TimePickerDialog.newInstance(this,
            calendar.get(Calendar.HOUR_OF_DAY), 0, false)

        mTimePickerDialog.enableMinutes(false)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Called whenever the user presses the "ok" button
     *
     * ToDo: Create a member variable that checks if both "bedtime" and "hours of sleep"
     *  have been set. We should only write to our SharedPreferences once they are both set.
     *  Just use onTimeSet and whatever function our Number picker will use to call a function
     *  that will check if both items are set, then we'll save.
     *  For onTimeSet, I think we'll just save hourOfDay as-is since our TimeBlocks are from
     *  1-24 (I think). We might have to adjust hourOfDay to match the way we define our HourBlock
     *  Hours.
     */
    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        var timePeriod = ""
        var newHour = hourOfDay

        if (hourOfDay >= 12) {
            if (hourOfDay != 12)
                newHour = hourOfDay - 12

            timePeriod = "PM"

        } else {
            if (hourOfDay == 0)
                newHour = 12;

            timePeriod = "AM";
        }

        val bedTimeString = "$newHour $timePeriod"
        button_bedtime.text = bedTimeString
    }
}