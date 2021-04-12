package marco.a.aguilar.hourly

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import marco.a.aguilar.hourly.repository.HourBlockRepository
import marco.a.aguilar.hourly.utils.AlarmHandler
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var mainFragmentAdapter: MainFragmentAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var mRepository: HourBlockRepository

    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        val fragmentList: ArrayList<Fragment> = arrayListOf(ProgressFragment(), TasksFragment())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        mRepository = HourBlockRepository.getInstance(this)

        /**
         * If the app is being opened for the first time, initialize
         * the alarm service and update SharedPreference's isFirstRun value to false.
         */
        val sharedPreferences = this.getSharedPreferences(getString(R.string.marco_a_aguilar_hourly_shared_preference_key),
            Context.MODE_PRIVATE
        )
        val isFirstRun: Boolean = sharedPreferences.getBoolean(getString(R.string.is_first_run_key), true)

        if(isFirstRun) {
            initHourlyAlarm()
            saveDefaultSleepSchedule()

            with(sharedPreferences.edit()) {
                putBoolean(getString(R.string.is_first_run_key), false)
                apply()
            }
        }

        mainFragmentAdapter = MainFragmentAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = mainFragmentAdapter

        /**
         * Attaching TabLayout to ViewPager2. You have to set up the text
         * and drawable here because the icon/text won't show up if you just
         * put it in the XML file.
         */
        tab_layout.setBackgroundColor(Color.parseColor("#1F1F1F"))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setTint(Color.parseColor("#D6D6D6"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do Nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setTint(Color.parseColor("#838383"))
            }
        })


        TabLayoutMediator(tab_layout, viewPager) { tab, position ->

            when(position) {
                0 -> {
                    tab.text = "Progress"
                    tab.icon = getDrawable(R.drawable.ic_progress)

                    viewPager.setCurrentItem(tab.position, true)
                }

                1 -> {
                    tab.text = "Tasks"
                    tab.icon = getDrawable(R.drawable.ic_tasks)
                    // For initializing purposes only.
                    tab.icon?.setTint(Color.parseColor("#838383"))
                }
            }

        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        menu?.findItem(R.id.action_recovery)?.icon?.let {
            DrawableCompat.setTint(
                it,
                ContextCompat.getColor(this, R.color.white)
            )
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_recovery -> {
                val intent = Intent(this, SleepScheduleActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initHourlyAlarm() {
        val alarmHandler = AlarmHandler()
        alarmHandler.setAlarm(this)
    }

    /**
     * When the user first opens the app, their
     * sleep schedule will be set from 12am - 7am (8hrs)
     */
    private fun saveDefaultSleepSchedule() {
        val bedtime = 24
        val sleepHours: List<Int> = listOf(bedtime, 1, 2, 3, 4, 5, 6, 7)
        mRepository.setSleepHourBlocks(sleepHours)

        // Store betime in SharedPreferences
        val sharedPreferences = this.getSharedPreferences(getString(R.string.marco_a_aguilar_hourly_shared_preference_key),
            Context.MODE_PRIVATE)

        with(sharedPreferences.edit()) {
            putInt(getString(R.string.bedtime_start_hour_key), bedtime)
            apply()
        }
    }
}
