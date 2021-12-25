package marco.a.aguilar.hourly

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task_check_list.*
import marco.a.aguilar.hourly.adapter.TaskCheckListAdapter
import marco.a.aguilar.hourly.enums.BlockType
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TaskCheckItem
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.repository.HourBlockRepository


/**
 * Todo: Wipe the Tasks Table from the Database after the 24th hour, users shouldn't
 *  see the tasks they wrote from the previous day.
 *
 *  Todo: Bug, creating a new task and then deleting it before you leave the Activity will
 *   persist the task even though you deleted it.
 */

class TaskCheckListActivity : AppCompatActivity(),
    TaskCheckListAdapter.OnTaskCheckItemInteractionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var viewAdapter: TaskCheckListAdapter

    private lateinit var mTasksCompletedInfo: TasksCompletedInfo
    private var mTaskCheckItemList = mutableListOf<TaskCheckItem>()
    private var mMostRecentFocusedTaskCheckItem : TaskCheckItem? = null

    private var mTasksToBeInserted = mutableListOf<Task>()

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

        initAddTaskFab()

        initShortcutButtons()

        initRecyclerView()

        /**
         * Update UI if BlockType.RECOVER
         *  - Disable/Hide FAB
         *  - Hide RecyclerView
         *  - Show "Sleep!" TextView
         */
        if(mTasksCompletedInfo.hourBlock.blockType == BlockType.RECOVER) {
            fab_task_checklist.hide()
            recycler_view_checklist.visibility = View.GONE
            textview_sleep_text.visibility = View.VISIBLE
        }

    }

    private fun initAddTaskFab() {
        fab_task_checklist.setOnClickListener {
            val blockId = mTasksCompletedInfo.hourBlock.blockId

            if (mTaskCheckItemList.size >= 15) {
                Toast.makeText(this, "You can only have 15 tasks", Toast.LENGTH_SHORT).show()
            } else {
                val newTask = Task("", blockId, false)
                // Setting isNewItem to true so the RecyclerView know how to handle this
                mTaskCheckItemList.add(TaskCheckItem(task = newTask, isNewItem = true))

                viewAdapter.notifyItemInserted(mTaskCheckItemList.size - 1)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard(this)
        insertTasks()
    }

    /**
     * Inserting Tasks once the User leaves the activity now. This helps us access the database
     * once, just incase the user decides to add more than one task. It also fixes our previous
     * bug where the Task when remain unchecked if the user checked it right after creating it.
     */
    private fun insertTasks() {
        if(mTasksToBeInserted.size > 0) {
            mRepository.insertTasks(mTasksToBeInserted)
        }
    }

    private fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TaskCheckListAdapter(mTaskCheckItemList, this)

        // Scrolls to recently added item.
        viewAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.scrollToPosition(viewAdapter.itemCount - 1)
            }
        })

        recyclerView = recycler_view_checklist.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        ItemTouchHelper(getItemTouchHelperSimpleCallback(this)).attachToRecyclerView(recyclerView)
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

        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    override fun onTaskCheckItemFocusChanged(position: Int, editText: EditText, textView: TextView, hasFocus: Boolean) {
        if(!hasFocus) {
            enableFab()
            disableShortcutButtons()

            editText.visibility = View.GONE
            textView.visibility = View.VISIBLE

            val editTextContent = editText.text.toString().trim()
            editText.text.clear()

            // Return if the item has already been deleted by shortcut button
            if(!mTaskCheckItemList.contains(mMostRecentFocusedTaskCheckItem))
                return

            val taskCheckItem = mTaskCheckItemList[position]

            // Edit adapter content if recycler view is not computing layout.
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

                            mTasksToBeInserted.add(taskCheckItem.task)

                            viewAdapter.notifyDataSetChanged()
                        }
                    }
                    // Old item
                    !taskCheckItem.isNewItem -> {
                        if(editTextContent.isNotEmpty() && editTextContent != textView.text.toString()) {
                            textView.text = editTextContent
                            taskCheckItem.task.description = editTextContent

                            updateTask(taskCheckItem.task)
                        }
                    }

                }
            }

        } else {
            disableFab()
            enableShortcutButtons()

            mMostRecentFocusedTaskCheckItem = mTaskCheckItemList[position]
        }

    }

    override fun onTaskChecked(position: Int, isComplete: Boolean) {
        val updatedTask = mTaskCheckItemList[position].task
        updatedTask.isComplete = isComplete

        mRepository.updateTask(updatedTask)
    }

    override fun onBackPressed() {
        hideKeyboard(this)

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tasks_check_list, menu)

        menu?.findItem(R.id.clear_tasks)?.icon?.let {
            DrawableCompat.setTint(
                it,
                ContextCompat.getColor(this, R.color.white)
            )
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(this)
                onBackPressed()
                true
            }
            R.id.clear_tasks -> {
                clearTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clearTasks() {
        if(mTaskCheckItemList.isEmpty()) {
            Toast.makeText(this, "There are no Tasks.", Toast.LENGTH_SHORT).show()
        } else {
            mRepository.clearTasks(mTaskCheckItemList[0].task.taskBlockId)
            // Clear mTaskCheckItemList after database has been cleared.
            mTaskCheckItemList.clear()
            viewAdapter.notifyDataSetChanged()
            /**
             * Clear mTasksToBeInserted so they aren't entered into the database
             * if the user presses the clear button before leaving the activity.
             */
            mTasksToBeInserted.clear()
        }
    }

    private fun removeUnsavedTaskCheckItem(newTaskCheckItem: TaskCheckItem) {
        mTaskCheckItemList.remove(newTaskCheckItem)
        viewAdapter.notifyDataSetChanged()
        hideKeyboard(this)
    }

    // Removes saved Task from database
    private fun removeSavedTaskCheckItem(oldTaskCheckItem: TaskCheckItem) {
        hideKeyboard(this)

        deleteTask(oldTaskCheckItem.task)

        mTaskCheckItemList.remove(oldTaskCheckItem)
        viewAdapter.notifyDataSetChanged()
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun disableFab() {
        fab_task_checklist.isEnabled = false
        fab_task_checklist.visibility = View.GONE
    }

    private fun enableFab() {
        fab_task_checklist.isEnabled = true
        fab_task_checklist.visibility = View.VISIBLE
    }

    private fun enableShortcutButtons() {
        shortcut_buttons_container.visibility = View.VISIBLE
    }

    private fun disableShortcutButtons() {
        shortcut_buttons_container.visibility = View.GONE
    }

    private fun updateTask(updatedTask: Task) {
        mRepository.updateTask(updatedTask)
    }

    fun deleteTask(deletedTask: Task) {
        mRepository.deleteTask(deletedTask)
    }

    // Makes EditText lose focus when clicking outside of it.
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus

            if(v != null) {
                if(v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)

                    val shortCutContainerRect = Rect()
                    shortcut_buttons_container.getGlobalVisibleRect(shortCutContainerRect)

                    if(!outRect.contains(event.rawX.toInt(), event.rawY.toInt())
                        && !shortCutContainerRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        v.clearFocus()
                        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    // Implementing Swipe left to delete with ItemTouchHelper.SimpleCallback
    private fun getItemTouchHelperSimpleCallback(context: Context): ItemTouchHelper.SimpleCallback {
        return object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val deletedTask = mTaskCheckItemList[viewHolder.adapterPosition].task
                    deleteTask(deletedTask)

                    mTaskCheckItemList.removeAt(viewHolder.adapterPosition)
                    viewAdapter.notifyDataSetChanged()
                }
            }
    }

}