package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import marco.a.aguilar.hourly.models.HourBlock

@Dao
interface HourBlockDao {

    /**
     * Don't use suspend Keyword here.
     * It wasn't building but after removing the suspend everything
     * worked just fine. It's not needed when returning LiveData.
     * suspend and LiveData seem not to work together
     */
    @Query("SELECT * FROM hour_blocks")
    fun getHourBlocks(): LiveData<List<HourBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmptyBlocks(hourBlocks: List<HourBlock>)
}