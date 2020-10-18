package marco.a.aguilar.hourly

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import marco.a.aguilar.hourly.viewmodel.ProgressViewModel
import java.util.*
import kotlin.math.log

class ProgressFragment : Fragment() {

    private val viewModel: ProgressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val calendar: Calendar = Calendar.getInstance()

        /**
         * @4:16 the value for calendar.get(Calendar.HOUR_OF_DAY) was 16.
         * This means the value could be from 1-24
         */
        Log.d(TAG, "onCreateView: Calendar.Hour: " + calendar.get(Calendar.HOUR_OF_DAY))

        // Inflate layout for this fragment
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.hourBlocks.observe(viewLifecycleOwner) {
            // Update Hour blocks (AKA RecyclerView)
            Log.d(TAG, "onViewCreated: Updating UI because of hourBlocks")
            it.forEach { hourBlock ->
                Log.d(TAG, "onViewCreated: hourBlock#${hourBlock.time} tasks: ")

                hourBlock.taskList.forEach {task ->
                    Log.d(TAG, "\tTask Decription: ${task.description}")
                }
            }
        }
    }


}