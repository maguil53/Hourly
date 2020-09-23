package marco.a.aguilar.hourly

import android.content.ContentValues.TAG
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import marco.a.aguilar.hourly.MainActivity.Companion.fragmentList

// FragmentStateAdapter takes in a fragment. So in order to
// create to call MainFragmentAdapter(this), we should create it inside a
// Fragment. Pretty sure we can do this inside an Activity too, but the Google
// documentation did it from a Fragment.
class MainFragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2 // Only 2 Tabs

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}