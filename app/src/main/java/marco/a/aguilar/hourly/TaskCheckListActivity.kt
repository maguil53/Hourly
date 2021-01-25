package marco.a.aguilar.hourly

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task_check_list.*
import marco.a.aguilar.hourly.adapter.TaskCheckListAdapter
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TasksCompletedInfo


class TaskCheckListActivity : AppCompatActivity(),
    TaskCheckListAdapter.OnTaskTextViewClickedListener {

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
            // No way mTasksCompletedInfo is null here
            mCheckListTasks = (mTasksCompletedInfo!!.tasks)?.toMutableList() ?: mutableListOf<Task>()
        }

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

                val testView = viewManager.findViewByPosition(mCheckListTasks.size - 1)
                Log.d(TAG, "onCreate: " + testView)
                Log.d(TAG, "onCreate: mCheckListTasks.size" + mCheckListTasks.size)
            }
        }

        initRecyclerView()
    }

    fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TaskCheckListAdapter(mCheckListTasks, this)

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

    /**
     * Need to figure out how to find individual items
     * inside the recyclerview so we can set a focus on a newly
     * created item.
     *
     * Need to figure out how to put the Cursor all the way at the
     * end
     */
    override fun onTaskTextViewClicked(textView: TextView, editText: EditText) {
        editText.visibility = View.VISIBLE
        textView.visibility = View.GONE

        val stringValue = textView.text.toString()

        /**
         * setSelection() needs to come after requestFocus()
         */
        editText.requestFocus()
        editText.setSelection(editText.text.length)
        editText.append(stringValue)

        /**
         * This solved my issue! The keyboard now shows. Thanks to this article:
         *  https://android.jlelse.eu/android-how-to-synchronize-keyboard-with-edittext-focus-8c1113797a15
         */
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * Implement onBackPressed to hide the keyboard.
     */

    /**
     * This code was taken from this post:
     *  https://stackoverflow.com/questions/6677969/tap-outside-edittext-to-lose-focus
     *
     *  For some reason this was one of the few ways to make our EditText lose focus when clicking
     *  outside of the EditText.
     *
     *  Note: We can also use this code for our Temi application!
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus

            if(v != null) {
                if(v is EditText) {
                    val outRect: Rect = Rect()
                    v.getGlobalVisibleRect(outRect)

                    if(!outRect.contains(event.getRawX().toInt(), event.getRawY().toInt())) {
                        v.clearFocus()
                        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }
}