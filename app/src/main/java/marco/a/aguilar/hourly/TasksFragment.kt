package marco.a.aguilar.hourly

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import marco.a.aguilar.hourly.adapter.TasksAdapter
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.viewmodel.TasksViewModel


@RequiresApi(Build.VERSION_CODES.N)
class TasksFragment : Fragment(), TasksAdapter.OnHourTasksListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: TasksAdapter
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

        viewModel.tasksCompletedInfoList.observe(viewLifecycleOwner) {
            /**
             * Calculates how many tasks are completed in an HourBlock. It
             * just takes the list of tasks from taskCompleteInfo and counts the tasks that have
             * isComplete set to true. Then it sets the final value to the instance variable,
             * totalComplete.
             */
            viewLifecycleOwner.lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    it.forEach { taskCompleteInfo ->
                        val numOfCompleted = taskCompleteInfo.tasks?.sumBy { if(it.isComplete) 1 else 0 } ?: 0

                        taskCompleteInfo.totalComplete = numOfCompleted
                    }
                }

                /**
                 * Once we set totalComplete, we use Dispatchers.Main for our
                 * CoroutineScope context so that we can update our UI on the Main Thread,
                 * or else we'll get an error that says:
                 *      "Only the original thread that created a view hierarchy can touch its views."
                 */
                withContext(Dispatchers.Main) {
                    viewAdapter.setTasksCompletedInfo(it)
                }
            }

        }
    }

    private fun initRecyclerView(view: View) {
        viewManager = LinearLayoutManager(activity)
        /**
         * I think this is what's causing the little stutter when swiping from ProgressFragment to
         * TasksFragment. Instead, we're going to initialize our Adapter with some blank data, just
         * so the transition is a little more smooth. Then our LiveData will update the UI when our
         * List of TasksCompletedInfo is ready.
         */
//        val blankTasksCompletedInfo = viewModel.tasksCompletedInfoList.value ?: TasksCompletedInfo.generateBlankTasksCompletedInfo()
//        viewAdapter = TasksAdapter(blankTasksCompletedInfo, this)
        viewAdapter = TasksAdapter(TasksCompletedInfo.generateBlankTasksCompletedInfo(), this)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_tasks).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onHourTasksClicked(position: Int) {
        val tasksCompletedInfo = viewModel.tasksCompletedInfoList.value?.get(position)
        val intent = Intent(activity, TaskCheckListActivity::class.java)

        if (tasksCompletedInfo != null)
            intent.putExtra("TasksCompletedInfo", tasksCompletedInfo)

        startActivity(intent)
    }

}