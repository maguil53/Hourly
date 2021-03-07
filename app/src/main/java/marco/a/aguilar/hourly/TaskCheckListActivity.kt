package marco.a.aguilar.hourly

import android.app.Activity
import android.content.Context
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task_check_list.*
import marco.a.aguilar.hourly.adapter.TaskCheckListAdapter
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TaskCheckItem
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.repository.HourBlockRepository

/**
 * todo: Add a listener for each item's checkbox so we can mark it as complete
 *
 * todo: fix bug that puts shortcut buttons on top of a TaskCheckItem if the list is long
 */

class TaskCheckListActivity : AppCompatActivity(),
    TaskCheckListAdapter.OnTaskCheckItemInteractionListener {

    private val TAG = "TaskCheckListActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: TaskCheckListAdapter

    private lateinit var mTasksCompletedInfo: TasksCompletedInfo
    private var mTaskCheckItemList = mutableListOf<TaskCheckItem>()
    private var mMostRecentFocusedTaskCheckItem : TaskCheckItem? = null

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

        initShortcutButtons()

        initRecyclerView()
    }


    private fun initRecyclerView() {
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

        // Used for swiping left to delete
        val simpleCallback = getItemTouchHelperSimpleCallback(this)
        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }

    private fun initShortcutButtons() {
        /**
         * The only issue now is. That if users try to add a task, then hit the delete button after
         * focus has been lost, then the item won't be deleted because it's still trying to save
         * onto the database. So we need to have some kind of mechanism that waits for our our task
         * to be saved before we can leave the activity or press the delete button..
         *
         * Kind how like Notion does.
         * We can start looking for answers here:
         *      https://stackoverflow.com/questions/58248191/how-to-get-a-callback-when-room-completed-deleting-or-adding-of-item
         */
        button_shortcut_delete.setOnClickListener {
            mMostRecentFocusedTaskCheckItem?.let {
                if(!it.isNewItem) {
                    removeSavedTaskCheckItem(it)
                } else {
                    // New TaskCheckItem
                    removeUnsavedTaskCheckItem(it)
                }
            }
        }

        button_shortcut_accept.setOnClickListener {
            // Must place hideKeyboard() before clearing focus
            hideKeyboard(this)
            currentFocus?.clearFocus()
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
            enableFAB()
            disableShortcutButtons()

            var editTextContent = editText.text.toString().trim()
            val textViewContent = textView.text.toString()

            // This should go at the top
            editText.visibility = View.GONE
            textView.visibility = View.VISIBLE

            // Clear text once we are done using it.
            editText.text.clear()

            /**
             * Make sure to return if the item has already been deleted by shortcut button
             */
            if(!mTaskCheckItemList.contains(mMostRecentFocusedTaskCheckItem))
                return

            val taskCheckItem = mTaskCheckItemList[position]

            if(!recyclerView.isComputingLayout) {
                when {
                    taskCheckItem.isNewItem -> {
                        if(editTextContent.isEmpty()) {

                            // Do nothing in DB, just delete it from list and notify RV
                            mTaskCheckItemList.remove(taskCheckItem)
                            viewAdapter.notifyDataSetChanged()
                        } else {
                            // Put new item's content into textView
                            textView.text = editTextContent

                            taskCheckItem.isNewItem = false
                            taskCheckItem.task.description = editTextContent

                            // block_id is assigned when we create the task so we can just insert
                            insertTask(taskCheckItem.task)

                            viewAdapter.notifyDataSetChanged()
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

        } else {
            disableFAB()
            enableShortcutButtons()

            Log.d(TAG, "onTaskCheckItemFocusChanged: Focused...")
            // Replace currently focused task
            mMostRecentFocusedTaskCheckItem = mTaskCheckItemList[position]
            Log.d(TAG, "onTaskCheckItemFocusChanged: $mMostRecentFocusedTaskCheckItem")
        }

    }


    override fun onBackPressed() {
        hideKeyboard(this)

        super.onBackPressed()
    }

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

    override fun onStop() {
        super.onStop()

        hideKeyboard(this)
    }

    fun removeUnsavedTaskCheckItem(newTaskCheckItem: TaskCheckItem) {
        mTaskCheckItemList.remove(newTaskCheckItem)
        viewAdapter.notifyDataSetChanged()
        hideKeyboard(this)
    }

    // Removes saved Task from database
    fun removeSavedTaskCheckItem(oldTaskCheckItem: TaskCheckItem) {
        hideKeyboard(this)

        deleteTask(oldTaskCheckItem.task)

        mTaskCheckItemList.remove(oldTaskCheckItem)
        viewAdapter.notifyDataSetChanged()
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
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

    fun enableShortcutButtons() {
        shortcut_buttons_container.visibility = View.VISIBLE
    }

    fun disableShortcutButtons() {
        shortcut_buttons_container.visibility = View.GONE
    }

    fun updateTask(updatedTask: Task) {
        mRepository.updateTask(updatedTask)
    }

    fun insertTask(newTask: Task) {
        mRepository.insertTask(newTask)
    }

    fun deleteTask(deletedTask: Task) {
        mRepository.deleteTask(deletedTask)
    }

    /**
     * This code was taken from this post:
     *  https://stackoverflow.com/questions/6677969/tap-outside-edittext-to-lose-focus
     *
     *  This is one of the few ways to make our EditText lose focus when clicking
     *  outside of the EditText.
     *
     *  Note: We can also use this code for our Temi application!
     *
     *  Update(2/17/21)
     *      Added another condition to the if statement so that it the keyboard won't close if
     *      the buttons inside the shortcuts container are clicked. This also help fix the bug
     *      where the buttons weren't triggering the click event.
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus

            if(v != null) {
                if(v is EditText) {
                    val outRect: Rect = Rect()
                    v.getGlobalVisibleRect(outRect)

                    val shortCutContainerRect = Rect()
                    shortcut_buttons_container.getGlobalVisibleRect(shortCutContainerRect)

                    if(!outRect.contains(event.getRawX().toInt(), event.getRawY().toInt())
                        && !shortCutContainerRect.contains(event.getRawX().toInt(), event.getRawY().toInt())) {
                        v.clearFocus()
                        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    /**
     * Implementing Swipe left to delete with ItemTouchHelper.SimpleCallback
     */
    private fun getItemTouchHelperSimpleCallback(context: Context): ItemTouchHelper.SimpleCallback {
        return object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

            /**
             * Not Using removeSavedTask() here because we're using viewHolder.adapterPosition
             * to specify which item was removed.
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedTask = mTaskCheckItemList[viewHolder.adapterPosition].task
                    deleteTask(deletedTask)

                    mTaskCheckItemList.removeAt(viewHolder.adapterPosition)
                    viewAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }
    }

}