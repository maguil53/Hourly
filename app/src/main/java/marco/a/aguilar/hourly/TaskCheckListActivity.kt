package marco.a.aguilar.hourly

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
import marco.a.aguilar.hourly.models.TaskCheckItem
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.repository.HourBlockRepository


class TaskCheckListActivity : AppCompatActivity(),
    TaskCheckListAdapter.OnTaskCheckItemInteractionListener {

    private val TAG = "TaskCheckListActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: TaskCheckListAdapter

    private lateinit var mTasksCompletedInfo: TasksCompletedInfo
    private var mTaskCheckItemList = mutableListOf<TaskCheckItem>()

    private lateinit var mRepository: HourBlockRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_check_list)

        mRepository = HourBlockRepository.getInstance(this)

        // Not sure why we have to add !! if we already checked for null
        if(intent.extras != null) {
            mTasksCompletedInfo = intent.getParcelableExtra("TasksCompletedInfo")!!

            if(mTasksCompletedInfo.tasks != null) {
                mTaskCheckItemList = mTasksCompletedInfo.tasks!!.map {
                    TaskCheckItem(it)
                }.toMutableList()
            }
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_task_checklist.setOnClickListener {
            val blockId = mTasksCompletedInfo.hourBlock.blockId

            if(mTaskCheckItemList.size >= 15) {
                Toast.makeText(this, "You can only have 15 tasks", Toast.LENGTH_SHORT).show()
            } else {
                val newTask = Task(TaskType.WORK, "", blockId, false)
                // Setting isNewItem to true so the RecyclerView know how to handle this
                mTaskCheckItemList.add(TaskCheckItem(newTask, true))

                viewAdapter.notifyItemInserted(mTaskCheckItemList.size - 1)
            }
        }

        /**
         * todo: Add a listener for each item's checkbox so we can mark it as complete
         */

        initRecyclerView()
    }


    fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TaskCheckListAdapter(mTaskCheckItemList, this)

        /**
         * Okay, for some reason, if I scroll to position this way then it works.
         * I think my issue was that the Adapter wasn't finished inserting the item and
         * creating the UI fast enough (some kind of asynchronous issue). Some people
         * suggested creating a delay before calling scrollToPosition() but I think
         * using RecyclerView.AdapterDataObserver is better.
         */
        viewAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.scrollToPosition(viewAdapter.itemCount - 1)
            }
        })

        /**
         * The Scope Function apply() allows us to set the values for our
         * recyclerView's member variables
         */
        recyclerView = recycler_view_checklist.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    override fun onTaskCheckItemClicked(editText: EditText, textView: TextView, isNewItem: Boolean) {
        editText.visibility = View.VISIBLE
        textView.visibility = View.GONE

        val stringValue: String = textView.text.toString()

        // setSelection() needs to come after requestFocus()
        editText.requestFocus()
        editText.setSelection(editText.text.length)
        editText.append(stringValue)

        /**
         * This solved my issue! The keyboard now shows. Thanks to this article:
         *  https://android.jlelse.eu/android-how-to-synchronize-keyboard-with-edittext-focus-8c1113797a15
         */
        // Might need to figure out a different way to do this since, toggle isn't really what we want.
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * Without !recyclerView.isComputingLayout we get a
     *      "Cannot call this method while RecyclerView is computing a layout or scrolling"
     * when try remove/add item from recyclerview
     */
    override fun onTaskCheckItemFocusChanged(position: Int, editText: EditText, textView: TextView, hasFocus: Boolean) {

        if(!hasFocus) {
            var editTextContent = editText.text.toString().trim()
            val textViewContent = textView.text.toString()
            val taskCheckItem = mTaskCheckItemList[position]

            enableFAB()

            /**
             * Okay so we're going to need an Update and Add function in our Dao
             * for now. Worry about adding Delete when we add the swipe function.
             * Also, if we add a delete button, we should make it perform a swipe
             * action so that the user knows they can swipe to delete, like YouTube
             * does when deleting a video from your playlist.
             *
             * If New, then we call Insert
             * If Old, then we call Update whether it's new or not
             * We might have to specify block_id or not
             */
            if(!recyclerView.isComputingLayout) {
                when {
                    taskCheckItem.isNewItem -> {
                        if(editTextContent.isEmpty()) {
                            // Do nothing in DB, just delete it from list and notify RV
                            mTaskCheckItemList.remove(taskCheckItem)
                            viewAdapter.notifyItemRemoved(mTaskCheckItemList.lastIndex)
                        } else {
                            // Put new item's content into textView
                            textView.text = editTextContent

                            taskCheckItem.isNewItem = false
                            taskCheckItem.task.description = editTextContent

                            // block_id is assigned when we create the task so we can just insert
                            insertTask(taskCheckItem.task)

                            viewAdapter.notifyItemChanged(mTaskCheckItemList.lastIndex)
                        }
                    }
                    // Old item
                    !taskCheckItem.isNewItem -> {
                        if(editTextContent.isNotEmpty() && editTextContent != textViewContent) {
                            textView.text = editTextContent
                            taskCheckItem.task.description = editTextContent

                            updateTask(taskCheckItem.task)
                        }
                    }

                }
            }

            // This should go at the top
            editText.visibility = View.GONE
            textView.visibility = View.VISIBLE

            // Clear text once we are done using it.
            editText.text.clear()
        } else {
            disableFAB()
        }

    }

    // This doesn't work
    override fun onBackPressed() {
        super.onBackPressed()

        hideKeyboard(this)
    }

    // This doesn't work
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(this)
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // This doesn't work
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Calling onStop")

        hideKeyboard(this)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun disableFAB(): Unit {
        fab_task_checklist.isEnabled = false
        fab_task_checklist.visibility = View.GONE
    }

    fun enableFAB(): Unit {
        fab_task_checklist.isEnabled = true
        fab_task_checklist.visibility = View.VISIBLE
    }

    fun updateTask(updatedTask: Task) {
        mRepository.updateTask(updatedTask)
    }

    fun insertTask(newTask: Task) {
        mRepository.insertTask(newTask)
    }

    /**
     * This code was taken from this post:
     *  https://stackoverflow.com/questions/6677969/tap-outside-edittext-to-lose-focus
     *
     *  This is one of the few ways to make our EditText lose focus when clicking
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