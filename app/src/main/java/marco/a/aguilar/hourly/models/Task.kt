package marco.a.aguilar.hourly.models

import android.content.ContentValues.TAG
import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import marco.a.aguilar.hourly.enums.TaskType

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "task_id") var taskId: Int,
    var type: TaskType, // Might have to create Converter for this column
    var description: String,
    @ColumnInfo(name = "block_id") var blockId: Int, // Which Block it belongs to
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
) {

    companion object {

        fun checkIfAllTasksAreComplete(tasks: List<Task>): Boolean {
            return tasks.all { it.isComplete }
        }

        fun generateDummyTasks(): List<Task> {
            return listOf(
                Task(99, TaskType.WORK, "random",1, true),
                Task(102, TaskType.WORK, "random",1, true),
                Task(6, TaskType.WORK, "random",1, false)
            )
        }


    }


}