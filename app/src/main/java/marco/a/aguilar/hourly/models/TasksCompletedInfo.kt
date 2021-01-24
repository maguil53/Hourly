package marco.a.aguilar.hourly.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TasksCompletedInfo (
    @Embedded val hourBlock: HourBlock,
    @Relation(
        parentColumn = "block_id",
        entityColumn = "task_block_id"
    )
    val tasks: List<Task>?,
    @Ignore var totalComplete: Int = 0
) : Parcelable {

    /**
     * Using this secondary constructor for getTasksInfo() in our TaskDao.kt.
     * The reason for this is because our primary constructor will be used to set the value
     * for totalComplete when we finish calculating this value inside TasksFragment. Once
     * we set this value. we can send the TasksCompletedInfo list to our TasksAdapter to set up
     * our View.
     */
    constructor(hourBlock: HourBlock, tasks: List<Task>?): this(hourBlock, tasks, 0) {}

    companion object {
        fun generateBlankTasksCompletedInfo(): List<TasksCompletedInfo> {
            return listOf(
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null),
                TasksCompletedInfo(HourBlock(0, 0), null), TasksCompletedInfo(HourBlock(0, 0), null)
            )
        }
    }
}