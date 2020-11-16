package marco.a.aguilar.hourly

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import marco.a.aguilar.hourly.adapter.TasksAdapter
import marco.a.aguilar.hourly.viewmodel.TasksViewModel

class TasksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Change this to TasksViewModel later. Using it to test out RecyclerView for now
    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        initRecyclerView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.hourBlocks.observe(viewLifecycleOwner) {
            // Update Hour blocks (AKA RecyclerView)
            Log.d(ContentValues.TAG, "onViewCreated: Updating UI because of hourBlocks")
            it.forEach { hourBlock ->
                Log.d(ContentValues.TAG, "onViewCreated: hourBlock#${hourBlock.time} tasks: ")

                hourBlock.taskList.forEach {task ->
                    Log.d(ContentValues.TAG, "\tTask Decription: ${task.description}")
                }
            }
        }
    }

    fun initRecyclerView(view: View) {

        viewManager = LinearLayoutManager(activity)
        viewAdapter = TasksAdapter(viewModel.hourBlocks.value!!)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks).apply {
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