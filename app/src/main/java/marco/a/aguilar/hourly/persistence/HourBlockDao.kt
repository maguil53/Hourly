package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import marco.a.aguilar.hourly.models.HourBlock

@Dao
interface HourBlockDao {

    @Query("SELECT * FROM hour_blocks")
    fun getHourBlocks(): LiveData<List<HourBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmptyBlocks(hourBlocks: List<HourBlock>)
}