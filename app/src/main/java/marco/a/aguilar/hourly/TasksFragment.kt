package marco.a.aguilar.hourly

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import marco.a.aguilar.hourly.adapter.ProgressAdapter
import marco.a.aguilar.hourly.adapter.TasksAdapter
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.viewmodel.TasksViewModel

import java.util.*


@RequiresApi(Build.VERSION_CODES.N)
class TasksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */
    private val viewModel: TasksViewModel by lazy {

        val activity = requireNotNull(this.activity) {
            "You can only access the viewmodel after onActivityCreted()"
        }

        ViewModelProvider(this, TasksViewModel.TasksViewModelFactory(activity.application))
            .get(TasksViewModel::class.java)

    }

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
//            it.forEach { hourBlock ->
//                Log.d(ContentValues.TAG, "onViewCreated: hourBlock#${hourBlock.time} tasks: ")
//
//                hourBlock.tasks?.forEach {task ->
//                    Log.d(ContentValues.TAG, "\tTask Decription: ${task.description}")
//                }
//            }
        }
    }

    fun initRecyclerView(view: View) {

        viewManager = LinearLayoutManager(activity)
        var dummyBlocks = viewModel.hourBlocks.value ?: HourBlock.generateFakeHourBlocks()
//        viewAdapter = TasksAdapter(viewModel.hourBlocks.value!!)
        viewAdapter = TasksAdapter(dummyBlocks)


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