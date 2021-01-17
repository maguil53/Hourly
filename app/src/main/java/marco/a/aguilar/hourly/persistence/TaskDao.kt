package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task

@Dao
interface TaskDao {

    /**
     * Making this just in case later we need a LiveData version
     * of our list of tasks
     */
    @Query("SELECT * FROM tasks WHERE block_id = :block_id")
    fun getLiveDataTasksForHourBlock(block_id: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE block_id = :block_id")
    fun getTasksForHourBlock(block_id: Int): List<Task>

    /**
     * Todo(): Delete Later. For now we're just using this
     * until we implement a way to generate tasks for an HourBlock
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDummyTasks(Task: List<Task>)
}