package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import marco.a.aguilar.hourly.enums.BlockType
import marco.a.aguilar.hourly.models.HourBlock

/**
 * Apparently SQLite doesn't have a boolean data type. Room maps
 * 1 to true, and 0 to false
 */
@Dao
interface HourBlockDao {

    @Query("SELECT * FROM hour_blocks")
    fun getHourBlocks(): LiveData<List<HourBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmptyBlocks(hourBlocks: List<HourBlock>)

    @Query("UPDATE hour_blocks SET is_complete = :is_complete WHERE block_id = :block_id")
    suspend fun updateIsComplete(is_complete: Boolean, block_id: Int)

    @Query("UPDATE hour_blocks SET block_type = :blockType WHERE block_id IN (:sleepHours)")
    fun setRecoverHourBlocks(sleepHours: List<Int>, blockType: BlockType = BlockType.RECOVER)

    @Query("UPDATE hour_blocks SET block_type = :work WHERE block_type = :sleep")
    fun revertSleepHours(work: BlockType = BlockType.WORK, sleep: BlockType = BlockType.RECOVER)
}