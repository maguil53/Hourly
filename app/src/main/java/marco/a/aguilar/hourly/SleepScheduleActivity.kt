package marco.a.aguilar.hourly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_sleep_schedule.*
import java.util.*

class SleepScheduleActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {

    lateinit var mTimePickerDialog: TimePickerDialog

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

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

    }
}