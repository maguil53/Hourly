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


    }

    fun initTimePicker() {
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
     *  Not sure if we need to create an observer for both variables or how we're going
     *  to implement it yet.
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