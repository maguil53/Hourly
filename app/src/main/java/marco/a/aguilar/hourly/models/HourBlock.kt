package marco.a.aguilar.hourly.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

// Room tables are case-sensitive
@Entity(tableName = "hour_blocks")
data class HourBlock(
   @PrimaryKey @ColumnInfo(name = "block_id") var blockId: Int, // 1 - 24
   var time: Int, // 1 - 24
   @ColumnInfo(name = "is_complete") var isComplete: Boolean = false,
   @Ignore var tasks: List<Task>? = null
) {

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


}