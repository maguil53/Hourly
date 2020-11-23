package marco.a.aguilar.hourly.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

// Room tables are case-sensitive
@Entity(tableName = "hour_blocks")
data class HourBlock(
   @PrimaryKey val id: Int, // 1 - 24
   val time: Int, // 1 - 24
   @Ignore val taskList: List<Task>,
   @ColumnInfo(name = "is_complete") var isComplete: Boolean = false
) { }