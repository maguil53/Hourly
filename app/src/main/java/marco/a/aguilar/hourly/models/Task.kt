package marco.a.aguilar.hourly.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import marco.a.aguilar.hourly.enums.TaskType

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "task_id") var taskId: Int,
    var type: TaskType, // Might have to create Converter for this column
    var description: String,
    @ColumnInfo(name = "task_block_id") var taskBlockId: Int, // Which Block it belongs to
    @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
) : Parcelable {

    companion object {

        /**
         * IMPORTANT:
         *      We might need to refactor this elegant piece of code to include logic
         *      related to the TaskType. Since we're going to include a button that lets
         *      Users specify their sleep schedule (In which case TaskType will be RECOVER)
         *      then we need to first check if type == TaskType.Recover. If it is Recover,
         *      then this code should just evaluate the HourBlock as COMPLETE. Also, we want
         *      the user to immediately see that their sleep schedule is in place (Green Squares).
         *      So we're gonna have to change our logic in ProgressAdapter bc right now we're
         *      only coloring the blocks of Hours that have passed, but we don't want this for the
         *      Sleep Schedule...We might have to create a BlockType enums or use some easier
         *      method to do this.
         *
         *      Later on we should provide a "Set Sleep Schedule from [] - []? Any Tasks that
         *      are in these Hours will be deleted" Message before the User sets their sleep
         *      schedule blocks...We could also have them delete it before doing that but
         *      it would be too much of a hassle for them and it would require to much work
         *      to check all the tasks in an HourBlock
         */
        fun checkIfAllTasksAreComplete(tasks: List<Task>): Boolean {
            /**
             * Return false if tasks is empty because tasks.all()
             * will return true if the list is empty for some reason.
             */
            if(tasks.isNotEmpty()) {
                tasks.all { it.isComplete }
            }

            return false
        }

        fun generateDummyTasks(): List<Task> {
            return listOf(
                Task(99, TaskType.WORK, "Do the dishes",1, true),
                Task(102, TaskType.WORK, "Work on Hourly",1, true),
                Task(6, TaskType.WORK, "Watch Anime with the queen",1, false)
            )
        }


    }


}