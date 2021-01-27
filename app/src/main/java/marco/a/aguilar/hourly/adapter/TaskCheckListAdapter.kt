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
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TaskCheckItem

class TaskCheckListAdapter(private var mTaskCheckItemList: List<TaskCheckItem>,
                           private var mOnTaskCheckItemInteractionListener: OnTaskCheckItemInteractionListener)
    : RecyclerView.Adapter<TaskCheckListAdapter.TaskCheckListViewHolder>() {


    class TaskCheckListViewHolder(private val taskItem: View) : RecyclerView.ViewHolder(taskItem) {

    }

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

        holder.itemView.textview_checklist_task_description.text = taskDescription
        holder.itemView.checkbox_checklist_task_iscomplete.isChecked = isComplete

        val taskDescriptionTextView: TextView = holder.itemView.textview_checklist_task_description
        val taskDescriptionEditText: EditText = holder.itemView.edittext_checklist_task_description


        taskDescriptionTextView.setOnClickListener {
            mOnTaskCheckItemInteractionListener.onTaskCheckItemClicked(taskDescriptionEditText,
                taskDescriptionTextView, mTaskCheckItemList[position].isNewItem)
        }

        taskDescriptionEditText.setOnFocusChangeListener { view, hasFocus ->
            mOnTaskCheckItemInteractionListener.onTaskCheckItemFocusChanged(position, view as EditText,
                taskDescriptionTextView, hasFocus)
        }

        /**
         * Since we want to set the focus on the newly created item
         */
        if(mTaskCheckItemList[position].isNewItem) {
            taskDescriptionTextView.performClick()
        }

    }

    override fun getItemCount() = mTaskCheckItemList.size


    interface OnTaskCheckItemInteractionListener {

        /**
         * Will be used to toggle between EditText and TextView (So Far)
         */
        fun onTaskCheckItemClicked(editText: EditText, textView: TextView, isNewItem: Boolean)

        /**
         * Will be used to check if the position of Item is the last one
         * and if isNewItem is equal to true, if it is, then we change
         * isNewItem to false in the TaskCheckItem List of our Activity and
         * call notifyItemChanged(position)
         */
        fun onTaskCheckItemFocusChanged(position: Int, editText: EditText, textView: TextView, hasFocus: Boolean)

    }
}