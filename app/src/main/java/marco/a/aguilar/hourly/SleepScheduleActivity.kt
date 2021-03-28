package marco.a.aguilar.hourly

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_sleep_schedule.*
import java.util.*

class SleepScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    lateinit var mTimePickerDialog: TimePickerDialog

    private val TAG = "SleepScheduleActivity"

    private var mBedTime: Int = -1
    private var mHoursOfSleep: Int = 1

    lateinit var mSharedPreferences: SharedPreferences
    private val sharedPrefFile = "marco.a.aguilar.hourly.shared_preference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_schedule)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**
         * For some reason, getSharedPreferences() with the specific filename worked,
         * but using getPreferences() didn't.
         */
        mSharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        initTimePicker()
        initNumberPicker()

        button_bedtime.setOnClickListener {
            mTimePickerDialog.show(supportFragmentManager, "TimePickerDialog")
        }

        button_set_sleep_schedule.setOnClickListener {
            if(mBedTime == -1) {
                Toast.makeText(this, "Need to set your Bedtime!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Your Sleep Schedule has been set!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initTimePicker() {
        // Will help us get Hour based on user's TimeZone
        val calendar = GregorianCalendar(TimeZone.getDefault())

        mTimePickerDialog = TimePickerDialog.newInstance(this,
            calendar.get(Calendar.HOUR_OF_DAY), 0, false)

        mTimePickerDialog.enableMinutes(false)
    }

    private fun initNumberPicker() {
        number_picker_hours_of_sleep.maxValue = 8
        number_picker_hours_of_sleep.minValue = 1

        number_picker_hours_of_sleep.setOnValueChangedListener { _, _, newValue ->
            mHoursOfSleep = newValue

            // Do not save until mBedTime is set
            if(mBedTime != -1)
                saveSleepSchedule()
        }
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
     * ToDo:
     *  For onTimeSet, I think we'll just save hourOfDay as-is since our TimeBlocks are from
     *  1-24 (I think). We might have to adjust hourOfDay to match the way we define our HourBlock
     *  Hours.
     *
     *  hourOfDay is from 0-23 where 0 = 12am
     *  However, we have 1-24 where 24 = 12am
     *
     *  So if mBedTime is 0, we should just change it to 24 before storing it
     *  in SharedPreferences.
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

        mBedTime = hourOfDay
        saveSleepSchedule()
    }

    /**
     *
     * Saves when a user goes to sleep and last hour they should be asleep.
     *
     * TimePicker makes 0 as 12am, so we just need to change this value to 24
     * since that's the value we associated with 12am
     */
    private fun saveSleepSchedule() {
        if(mBedTime == 0) mBedTime = 24

        Log.d(TAG, "saveSleepSchedule: mBedTime: $mBedTime")
        Log.d(TAG, "saveSleepSchedule: mHoursOfSleep: $mHoursOfSleep")

//        val lastHourOfSleep = calculateLastHourOfSleep(mBedTime, mHoursOfSleep)

        with(mSharedPreferences.edit()) {

            putInt(getString(R.string.bedtime_start_hour_key), mBedTime)
            putInt(getString(R.string.hours_of_sleep_key), mHoursOfSleep)

//            putInt(getString(R.string.bedtime_end_hour), lastHourOfSleep)
            apply()
        }
    }

    /**
     * If the sum of bedTime and hoursOfSleep exceed 24, then we mod 24
     *  and I believe we should subtract 1 to calculate last hour awake.
     *
     * In both situations we want to subtract 1 because we only want to mark
     * the hours that the user is asleep as "COMPLETE"
     *
     *  Ex:
     *      bedtime = 18 (6pm)
     *      hoursOfSleep = 8
     *
     *      18 + 8 = 26
     *      26 mod 24 = 2 (2am)
     *      2 - 1 = 1 (1am)     // Last hour asleep
     *
     *      6pm, 7pm, 8pm, 9pm, 10pm, 11pm, 12am, 1am (8 total blocks to mark as "COMPLETE")
     *
     *
     *  Ex #2:
     *
     *      bedtime = 23
     *      hoursOfSleep = 7
     *
     *      23 + 7 = 30
     *      30 mod 24 = 6 (6am)
     *      6 - 1 = 5 (5am) // Last Hour to be asleep
     *
     *      11pm, 12am, 1am, 2am, 3am, 4am, 5am (7 hrs of sleep, 7 blocks to mark as "COMPLETE")
     *
     */
    private fun calculateLastHourOfSleep(bedtime: Int, hoursOfSleep: Int): Int {
        var lastHourOfSleep = bedtime + hoursOfSleep

        if(lastHourOfSleep > 24) lastHourOfSleep %= 24

        return lastHourOfSleep - 1
    }
}