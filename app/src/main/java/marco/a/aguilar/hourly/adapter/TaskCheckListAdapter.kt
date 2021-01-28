package marco.a.aguilar.hourly.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TaskCheckItem

class TaskCheckListAdapter(private var mTaskCheckItemList: List<TaskCheckItem>,
                           private var mOnTaskCheckItemInteractionListener: OnTaskCheckItemInteractionListener)
    : RecyclerView.Adapter<TaskCheckListAdapter.TaskCheckListViewHolder>() {


    class TaskCheckListViewHolder(private val taskItem: View) : RecyclerView.ViewHolder(taskItem) {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskCheckListViewHolder {
        val checkListItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.checklist_item, parent, false)

        return TaskCheckListViewHolder(checkListItem)
    }

    override fun onBindViewHolder(
        holder: TaskCheckListViewHolder,
        position: Int
    ) {
        val taskDescription = mTaskCheckItemList[position].task.description
        val isComplete = mTaskCheckItemList[position].task.isComplete
        val isNewTaskCheckItem = mTaskCheckItemList[position].isNewItem

        val taskDescriptionTextView: TextView = holder.itemView.textview_checklist_task_description
        val taskDescriptionEditText: EditText = holder.itemView.edittext_checklist_task_description

        holder.itemView.checkbox_checklist_task_iscomplete.isChecked = isComplete
        taskDescriptionTextView.text = taskDescription

        taskDescriptionTextView.setOnClickListener {
            mOnTaskCheckItemInteractionListener.onTaskCheckItemClicked(taskDescriptionEditText,
                taskDescriptionTextView, mTaskCheckItemList[position].isNewItem)
        }

        taskDescriptionEditText.setOnFocusChangeListener { view, hasFocus ->
            mOnTaskCheckItemInteractionListener.onTaskCheckItemFocusChanged(position, view as EditText,
                taskDescriptionTextView, hasFocus)
        }

        /**
         * Steps To Reproduce Bug:
         *      1) Keep adding items until you're able to scroll
         *      2) Scroll down to newest item
         *      3) Click FAB
         *      4) Don't enter anything and remove focus
         *      5) Scroll up and you'll see that the first item is gone
         *
         * Notes: The contents are still there in OldString, because if we focus
         * and then remove focus, then the old text will reappear.
         *
         * FIXED:
         *      I believe this occurred because the RecyclerView recycles views and the
         *      view from the empty new deleted item was being used for our old items,
         *      which is why the EditText for the old view had the Visibility set to
         *      VISIBLE
         *
         * Going to add this comment to our commit for learning purposes.
         */
        if(!isNewTaskCheckItem) {
            taskDescriptionTextView.visibility = View.VISIBLE
            taskDescriptionEditText.visibility = View.GONE
        } else {
            // Set focus on new item by performing click event.
            taskDescriptionTextView.performClick()
        }

    }

    override fun getItemCount() = mTaskCheckItemList.size


    interface OnTaskCheckItemInteractionListener {

        // Used to toggle between EditText and TextView
        fun onTaskCheckItemClicked(editText: EditText, textView: TextView, isNewItem: Boolean)

        /**
         * Used to check if the position of Item is the last one
         * and if isNewItem is equal to true, if it is, then we change
         * isNewItem to false in the TaskCheckItem List of our Activity and
         * call notifyItemChanged(position)
         */
        fun onTaskCheckItemFocusChanged(position: Int, editText: EditText, textView: TextView, hasFocus: Boolean)

    }
}