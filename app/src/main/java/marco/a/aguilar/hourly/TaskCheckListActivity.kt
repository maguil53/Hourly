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


class TaskCheckListActivity : AppCompatActivity(),
    TaskCheckListAdapter.OnTaskCheckItemInteractionListener {

    private val TAG = "TaskCheckListActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: TaskCheckListAdapter
    private lateinit var mTasksCompletedInfo: TasksCompletedInfo

    private var mTaskCheckItemList = mutableListOf<TaskCheckItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_check_list)

        // Not sure why we have to add !! if we already checked for null
        if(intent.extras != null) {
            mTasksCompletedInfo = intent.getParcelableExtra("Tasks")!!

            if(mTasksCompletedInfo.tasks != null) {
                mTaskCheckItemList = mTasksCompletedInfo.tasks!!.map {
                    TaskCheckItem(it)
                }.toMutableList()
            }
        }


        setSupportActionBar(findViewById(R.id.my_toolbar))
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Only 15 items are allowed
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

        initRecyclerView()
    }


    fun initRecyclerView() {
        viewManager = LinearLayoutManager(this)
        viewAdapter = TaskCheckListAdapter(mTaskCheckItemList, this)

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
     * Todo: When the list of Tasks is too long, creating a new item won't immediately
     * focus unless you scroll to it. So just scroll to that position.
     *
     * So far this is not working:
     *  if(isNewItem) {
     *      viewManager.scrollToPosition(mTaskCheckItemList.size - 1)
     *  }
     *
     *  smoothScrollToPosition() isn't working either...
     */
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
            var finalString = editText.text.toString().trim()
            val oldString = textView.text.toString()
            val taskCheckItem = mTaskCheckItemList[position]
            val lastIndex = mTaskCheckItemList.size - 1

            if(finalString.isEmpty() && !recyclerView.isComputingLayout) {

                // If new item is empty, delete it
                if(taskCheckItem.isNewItem) {
                    mTaskCheckItemList.remove(taskCheckItem)
                    viewAdapter.notifyItemRemoved(lastIndex)

                    // I think we should return here since there's not much else we should do
                    return
                } else {
                    finalString = oldString
                }
            }

            // If the new item isn't empty, add it to our list and mark it as old.
            if(taskCheckItem.isNewItem && position == lastIndex) {
                mTaskCheckItemList[lastIndex].isNewItem = false
                // Don't forget to set the new description.
                mTaskCheckItemList[lastIndex].task.description = finalString

                if(!recyclerView.isComputingLayout)
                    viewAdapter.notifyItemChanged(lastIndex)
            }

            textView.text = finalString

            editText.visibility = View.GONE
            textView.visibility = View.VISIBLE

            // Clear text or else it'll append the text twice.
            editText.text.clear()
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