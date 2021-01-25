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

class TaskCheckListAdapter(private var mTasks: List<Task>,
                           private var mOnTaskTextViewClickedListener: OnTaskTextViewClickedListener)
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
        val taskDescription = mTasks[position].description
        val isComplete = mTasks[position].isComplete

        holder.itemView.textview_checklist_task_description.text = taskDescription
        holder.itemView.checkbox_checklist_task_iscomplete.isChecked = isComplete

        val taskDescriptionTextView: TextView = holder.itemView.textview_checklist_task_description
        val taskDescriptionEditText: EditText = holder.itemView.edittext_checklist_task_description

        taskDescriptionTextView.setOnClickListener {
            mOnTaskTextViewClickedListener.onTaskTextViewClicked(taskDescriptionTextView, taskDescriptionEditText)
        }

        taskDescriptionEditText.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val finalEditText = view as EditText
                val finalString = finalEditText.text.toString()

                taskDescriptionTextView.text = finalString

                view.visibility = View.GONE
                taskDescriptionTextView.visibility = View.VISIBLE
                // Clear text or else it'll append the text twice.
                view.text.clear()
            }
        }

    }

    override fun getItemCount() = mTasks.size

    interface OnTaskTextViewClickedListener {
        fun onTaskTextViewClicked(textView: TextView, editText: EditText)
    }
}