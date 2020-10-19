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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import marco.a.aguilar.hourly.adapter.ProgressAdapter
import marco.a.aguilar.hourly.viewmodel.ProgressViewModel
import java.util.*
import kotlin.math.log

class ProgressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        val view: View = inflater.inflate(R.layout.fragment_progress, container, false)

        initRecyclerView(view)

        // Inflate layout for this fragment
        return view
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

    fun initRecyclerView(view: View) {
        viewManager = GridLayoutManager(activity, 4)
        viewAdapter = ProgressAdapter(viewModel.hourBlocks.value!!)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_progress).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a grid layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }
    }


}