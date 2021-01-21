package marco.a.aguilar.hourly.models

import androidx.room.Embedded
import androidx.room.Relation

data class TaskCompletedInfo (
    @Embedded val hourBlock: HourBlock,
    @Relation(
        parentColumn = "block_id",
        entityColumn = "task_block_id"
    )
    val tasks: List<Task>? // Not sure if this will work
)