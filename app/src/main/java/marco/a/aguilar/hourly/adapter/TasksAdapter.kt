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

class TasksAdapter(private var tasksCompletedInfo: List<TasksCompletedInfo>,
                   var mOnHourTasksListener: OnHourTasksListener) :
        RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    /**
     * The name tasksItem doesn't reflect what the purpose of this Adapter.
     * Because technically these are still Hour Blocks, we're just showing
     * how many tasks it has.
     */
    class TasksViewHolder(private val tasksItem: View, private val onHourTasksListener: OnHourTasksListener):
        RecyclerView.ViewHolder(tasksItem), View.OnClickListener {

        init {
            tasksItem.setOnClickListener(this)
        }

        override fun onClick(itemView: View?) {
            Log.d(TAG, "onClick: TasksViewHolder")

            onHourTasksListener.onHourTasksClicked(adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {

        val tasksItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.tasks_item, parent, false)

        return TasksViewHolder(tasksItem, mOnHourTasksListener)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {

        val totalComplete = tasksCompletedInfo[position].totalComplete.toString()
        val blockId = tasksCompletedInfo[position].hourBlock.blockId

        val time = HourBlock.getTime(blockId)

        // May be null
        val hourBlockTasks = tasksCompletedInfo[position].tasks

        val tasksDescription = if(hourBlockTasks != null && hourBlockTasks.isNotEmpty())
                                    totalComplete + "/" + hourBlockTasks.size.toString() + " Complete"
                                else "No Tasks"

        holder.itemView.textview_tasks_number.text = tasksDescription
        holder.itemView.textview_tasks_hour.text = time
    }

    override fun getItemCount() = tasksCompletedInfo.size

    fun setTasksCompletedInfo(newTasksCompletedInfo: List<TasksCompletedInfo>) {
        tasksCompletedInfo = newTasksCompletedInfo
        notifyDataSetChanged()
    }

    interface OnHourTasksListener {
        fun onHourTasksClicked(position: Int)
    }

}