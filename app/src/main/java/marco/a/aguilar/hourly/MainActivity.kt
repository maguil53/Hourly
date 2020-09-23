package marco.a.aguilar.hourly

import android.content.ContentValues.TAG
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mainFragmentAdapter: MainFragmentAdapter
    private lateinit var viewPager: ViewPager2

    companion object {
        val fragmentList: ArrayList<Fragment> = arrayListOf(ProgressFragment(), TasksFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainFragmentAdapter = MainFragmentAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = mainFragmentAdapter


        // Attach tab_layout
        /**
         * Attaching TabLayout to ViewPager2. You have to set up the text
         * and drawable here because the icon/text won't show up if you just
         * put it in the XML file.
         */
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setTint(Color.parseColor("#D6D6D6"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("Not yet implemented")
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
}
