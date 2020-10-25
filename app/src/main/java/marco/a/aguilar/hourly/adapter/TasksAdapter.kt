package marco.a.aguilar.hourly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.tasks_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.HourBlock

class TasksAdapter(private val hourBlocks: List<HourBlock>) :
        RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    class TasksViewHolder(private val tasksItem: View): RecyclerView.ViewHolder(tasksItem)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {

        val tasksItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.tasks_item, parent, false)

        return TasksViewHolder(tasksItem)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        holder.itemView.textview_tasks_hour.text = "1am"

        holder.itemView.textview_tasks_number.text = "No Tasks"
    }

    override fun getItemCount() = hourBlocks.size
}