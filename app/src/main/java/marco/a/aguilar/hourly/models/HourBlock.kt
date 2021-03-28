package marco.a.aguilar.hourly.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import marco.a.aguilar.hourly.enums.BlockType
import marco.a.aguilar.hourly.enums.TaskType

// Room tables are case-sensitive
@Parcelize
@Entity(tableName = "hour_blocks")
data class HourBlock(
   @PrimaryKey @ColumnInfo(name = "block_id") var blockId: Int, // 1 - 24
   var time: Int, // 1 - 24
   @ColumnInfo(name = "is_complete") var isComplete: Boolean = false,
   var blockType: BlockType = BlockType.WORK,
   @Ignore var tasks: List<Task>? = null
) : Parcelable {


   constructor(blockId: Int, time: Int, isComplete: Boolean) : this(blockId, time, isComplete, BlockType.WORK,null){
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

   }

}