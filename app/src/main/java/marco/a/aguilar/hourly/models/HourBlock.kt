package marco.a.aguilar.hourly.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import marco.a.aguilar.hourly.enums.TaskType

// Room tables are case-sensitive
@Parcelize
@Entity(tableName = "hour_blocks")
data class HourBlock(
   @PrimaryKey @ColumnInfo(name = "block_id") var blockId: Int, // 1 - 24
   var time: Int, // 1 - 24
   @ColumnInfo(name = "is_complete") var isComplete: Boolean = false,
   @Ignore var tasks: List<Task>? = null
) : Parcelable {

   /**
    * So the @Ignore annotation was giving me a lot of trouble but here's what I think
    * is happening:
    *    The Primary Constructor is being used to insert items into our Database.
    *    The Secondary Constructor is used to create HourBlock items when taking things out of the
    *    database.
    *
    *    The only issue here is that the Secondary Constructor sets our tasks to null.
    *    As of right now I don't see an issue because our Progress View will only need isComplete
    *    to determine the color of a block.
    *
    *    However, later on in TasksView we might need the Primary Constructor to get the number of
    *    Tasks completed.
    *    WAIT... I think we can have another secondary constructor or BETTER, have the Primary Constructor
    *    have a numberOfTasksCompleted field so that we can simply do a JOIN in our HourBlockDAO and we can
    *    have it STILL RETURN AN HOURBLOCK, because Room will know how to make an HourBlock with that column
    */

   constructor(blockId: Int, time: Int, isComplete: Boolean) : this(blockId, time, isComplete, null){
   }

   companion object {
      /**
       * For our Progress view we're going to have to check if tasks is null first,
       * because we can't color a block just based on isComplete. If there are no
       * tasks assigned to a block yet then we have to color it Grey. Same goes for if
       * the hour hasn't passed by yet.
       */
      fun prepopulateHourBlocks(): List<HourBlock> {
         return listOf(
            HourBlock(1, 1), HourBlock(2, 2),
            HourBlock(3, 3), HourBlock(4, 4),
            HourBlock(5, 5), HourBlock(6, 6),
            HourBlock(7, 7), HourBlock(8, 8),
            HourBlock(9, 9), HourBlock(10, 10),
            HourBlock(11, 11), HourBlock(12, 12),
            HourBlock(13, 13), HourBlock(14, 14),
            HourBlock(15, 15), HourBlock(16, 16),
            HourBlock(17, 17), HourBlock(18, 18),
            HourBlock(19, 19), HourBlock(20, 20),
            HourBlock(21, 21), HourBlock(22, 22),
            HourBlock(23, 23), HourBlock(24, 24)
         )
      }

      fun generateBlankHourBlocks(): List<HourBlock> {
         return listOf(
            HourBlock(1, 1), HourBlock(2, 2),
            HourBlock(3, 3), HourBlock(4, 4),
            HourBlock(5, 5), HourBlock(6, 6),
            HourBlock(7, 7), HourBlock(8, 8),
            HourBlock(9, 9), HourBlock(10, 10),
            HourBlock(11, 11), HourBlock(12, 12),
            HourBlock(13, 13), HourBlock(14, 14),
            HourBlock(15, 15), HourBlock(16, 16),
            HourBlock(17, 17), HourBlock(18, 18),
            HourBlock(19, 19), HourBlock(20, 20),
            HourBlock(21, 21), HourBlock(22, 22),
            HourBlock(23, 23), HourBlock(24, 24)
         )
      }

      fun getTime(blockId: Int): String {
         var time = ""

         val hour = if(blockId % 12 == 0) 12 else (blockId % 12)
         time += hour

         time += when(blockId) {
            in 12..23 -> " pm"
            else -> " am"
         }

         return time
      }


      /**
       * For now we'll need to specify Task ID but once we start adding Tasks
       * to our database we'll use the auto-generate property to create the Task IDs
       */
      fun generateFakeHourBlocks(): List<HourBlock> {
         val hour1Task1 = Task(TaskType.WORK, "Do homework", 1, true)
         val hour1Task2 = Task(TaskType.WORK, "Take out the trash", 1, true)
         val hour1TaskList: List<Task> = listOf(hour1Task1, hour1Task2)
         val hour1 = HourBlock(1, 1, true, hour1TaskList)

         val hour2Task1 = Task(TaskType.WORK, "Go for a run", 2)
         val hour2Task2 = Task(TaskType.WORK, "Take a shower", 2)
         val hour2TaskList: List<Task> = listOf(hour2Task1, hour2Task2)
         val hour2 = HourBlock(2, 2, false, hour2TaskList)

         val hour3Task1 = Task(TaskType.WORK, "Eat some food", 3)
         val hour3Task2 = Task(TaskType.WORK, "Meditate", 3)
         val hour3Task3 = Task(TaskType.WORK, "Watch anime!", 3)
         val hour3TaskList: List<Task> = listOf(hour3Task1, hour3Task2, hour3Task3)
         val hour3 = HourBlock(3, 3, false, hour3TaskList)

         val hour4 = HourBlock(4, 4, false, hour3TaskList)
         val hour5 = HourBlock(5, 5, false, hour3TaskList)
         val hour6 = HourBlock(6, 6, false, hour3TaskList)
         val hour7 = HourBlock(7, 7, false, hour3TaskList)
         val hour8 = HourBlock(8, 8, false, hour3TaskList)
         val hour9 = HourBlock(9, 9, false, hour3TaskList)
         val hour10 = HourBlock(10, 10, false, hour3TaskList)
         val hour11 = HourBlock(11, 11, false, hour3TaskList)
         val hour12 = HourBlock(12, 12, false, hour3TaskList)
         val hour13 = HourBlock(13, 13, false, hour3TaskList)
         val hour14 = HourBlock(14, 14, false, hour3TaskList)
         val hour15 = HourBlock(15, 15, false, hour3TaskList)
         val hour16 = HourBlock(16, 16, false, hour3TaskList)
         val hour17 = HourBlock(17, 17, false, hour3TaskList)
         val hour18 = HourBlock(18, 18, false, hour3TaskList)
         val hour19 = HourBlock(19, 19, false, hour3TaskList)
         val hour20 = HourBlock(20, 20, false, hour3TaskList)
         val hour21 = HourBlock(21, 21, false, hour3TaskList)
         val hour22 = HourBlock(22, 22, false, hour3TaskList)
         val hour23 = HourBlock(23, 23, false, hour3TaskList)
         val hour24 = HourBlock(24, 24, false, hour3TaskList)

         /**
          * Only generating 22 for testing purposes.
          */
         return listOf(hour1, hour2, hour3, hour4, hour5, hour6,
            hour7, hour8, hour9, hour10, hour11, hour12,
            hour13, hour14, hour15, hour16, hour17,
            hour18, hour19, hour20, hour21, hour22)
      }
   }

}