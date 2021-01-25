package marco.a.aguilar.hourly

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task_check_list.*
import kotlinx.android.synthetic.main.activity_task_check_list.my_toolbar
import kotlinx.android.synthetic.main.toolbar_main.*
import marco.a.aguilar.hourly.adapter.TaskCheckListAdapter
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TasksCompletedInfo


class TaskCheckListActivity : AppCompatActivity() {

    private val TAG = "TaskCheckListActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: TaskCheckListAdapter
    private var mTasksCompletedInfo: TasksCompletedInfo? = null

    private var mCheckListTasks = mutableListOf<Task>()

    /**
     * I have a bit of an issue but I think I know how to solve it.
     *
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_check_list)

        mTasksCompletedInfo = intent.getParcelableExtra("Tasks")

        /**
         * TasksCompletedInfo will most likely never be null since we prepopulate the db with HourBlocks.
         * We only need to check List<Task> for null.
         *
         * Also, I don't think we're ever using the List<Task> inside HourBlock so we might want
         * to get rid of that too.
         */
        if(mTasksCompletedInfo != null) {
            Log.d(TAG, "onCreate: tasksCompletedInfo: $mTasksCompletedInfo")
            // No way mTasksCompletedInfo is null here
            mCheckListTasks = (mTasksCompletedInfo!!.tasks)?.toMutableList() ?: mutableListOf<Task>()
        }

        Log.d(TAG, "onCreate: ")

        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**
         * Create a member variable and get the size of our tasks list.
         * If it's more than 15 then create a a Toast and Show it to the user!
         */
        fab_task_checklist.setOnClickListener {
            val blockId = mTasksCompletedInfo?.hourBlock?.blockId

            if(mCheckListTasks.size >= 15) {
                Toast.makeText(this, "You can only have 15 tasks", Toast.LENGTH_SHORT).show()
            } else {
                mCheckListTasks.add(Task(TaskType.WORK, "", blockId!!, false))
                viewAdapter.notifyDataSetChanged()
            }
        }

        initRecyclerView()
    }

    fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TaskCheckListAdapter(mCheckListTasks)

        recyclerView = recycler_view_checklist.apply {
            /**
             * Not gonna use setHasFixedSize(true) since I'm not sure if our
             * content will change since the user will be able to keep adding
             * tasks
             */
            layoutManager = viewManager
            adapter = viewAdapter
        }


    }
}