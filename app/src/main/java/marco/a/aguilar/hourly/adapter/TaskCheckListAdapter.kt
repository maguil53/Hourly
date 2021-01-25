package marco.a.aguilar.hourly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.Task

class TaskCheckListAdapter(private var mTasks: List<Task>)
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

        val taskDescription = mTasks[position].description
        val isComplete = mTasks[position].isComplete

        holder.itemView.textview_checklist_task_description.text = taskDescription
        holder.itemView.checkbox_checklist_task_iscomplete.isChecked = isComplete


    }

    override fun getItemCount() = mTasks.size
}