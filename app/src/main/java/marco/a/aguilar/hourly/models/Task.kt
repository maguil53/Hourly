package marco.a.aguilar.hourly.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import marco.a.aguilar.hourly.enums.TaskType

/**
 * AutoGenerate will take care of value for task_id, I put it at the end
 * so we don't have to worry about that value when initializing a Task object.
 */
@Parcelize
@Entity(tableName = "tasks")
data class Task(
    var description: String,
    @ColumnInfo(name = "task_block_id") var taskBlockId: Int, // Which Block it belongs to
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "task_id") var taskId: Int = 0
) : Parcelable {

    companion object {

        /**
         * Return "false" if tasks is empty because tasks.all()
         * will return "true" if the list is empty.
         */
        fun checkIfAllTasksAreComplete(tasks: List<Task>): Boolean {

            if(tasks.isNotEmpty()) {
                return tasks.all { it.isComplete }
            }

            return false
        }

    }


}