package marco.a.aguilar.hourly.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.persistence.Converters

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "task_id") var taskId: Int,
    var type: TaskType, // Might have to create Converter for this column
    var description: String,
    @ColumnInfo(name = "block_id") var blockId: Int, // Which Block it belongs to
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
) { }