package marco.a.aguilar.hourly.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tasks_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.utils.TaskUtil

class TasksAdapter(private var tasksCompletedInfo: List<TasksCompletedInfo>) :
        RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    /**
     * The name tasksItem doesn't reflect what the purpose of this Adapter.
     * Because technically these are still Hour Blocks, we're just showing
     * how many tasks it has.
     */
    
    class TasksViewHolder(private val tasksItem: View): RecyclerView.ViewHolder(tasksItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {

        val tasksItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.tasks_item, parent, false)

        return TasksViewHolder(tasksItem)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {

        val totalComplete = tasksCompletedInfo[position].totalComplete
        val blockId = tasksCompletedInfo[position].hourBlock.blockId

        holder.itemView.textview_tasks_number.text = totalComplete.toString()
        holder.itemView.textview_tasks_hour.text = blockId.toString()
    }

    override fun getItemCount() = tasksCompletedInfo.size

    fun setTasksCompletedInfo(newTasksCompletedInfo: List<TasksCompletedInfo>) {
        tasksCompletedInfo = newTasksCompletedInfo
        notifyDataSetChanged()
    }
}