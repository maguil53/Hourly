package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import marco.a.aguilar.hourly.models.HourBlock

@Dao
interface HourBlockDao {

    @Query("SELECT * FROM hour_blocks")
    fun getHourBlocks(): LiveData<List<HourBlock>>
}