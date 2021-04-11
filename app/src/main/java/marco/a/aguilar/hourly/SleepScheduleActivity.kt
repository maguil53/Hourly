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
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.repository.HourBlockRepository
import java.util.*

class SleepScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    lateinit var mTimePickerDialog: TimePickerDialog

    private val TAG = "SleepScheduleActivity"

    private var mBedTime: Int = -1
    private var mHoursOfSleep: Int = 1

    private lateinit var mRepository: HourBlockRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep_schedule)

        mRepository = HourBlockRepository.getInstance(this)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                saveSleepSchedule()
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
     * TimePicker makes 0 as 12am, so we just need to change this value to 24
     * since that's the value we associated with 12am
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
        if(mBedTime == 0) mBedTime = 24
    }

    /**
     * Save to the DB. Going to clear all tasks under the hour blocks that
     * are meant for sleeping and we'll change those hourblocks' BlockType to RECOVER
     */
    private fun saveSleepSchedule() {
        Log.d(TAG, "saveSleepSchedule: mBedTime: $mBedTime")
        Log.d(TAG, "saveSleepSchedule: mHoursOfSleep: $mHoursOfSleep")

        val sleepHours = HourBlock.getRangeOfSleepHours(mBedTime, mHoursOfSleep)

        mRepository.setSleepHourBlocks(sleepHours)

        saveBedtimeIntoSharedPreferences()
    }

    /**
     * Saving mBedTime in SharedPreferences so the HourlyReceiver
     * knows when to clear the "Tasks" table, since it's a new day.
     */
    private fun saveBedtimeIntoSharedPreferences() {
        val sharedPreferences = this.getSharedPreferences(getString(R.string.marco_a_aguilar_hourly_shared_preference_key),
            Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putInt(getString(R.string.bedtime_start_hour_key), mBedTime)
            apply()
        }
    }

}