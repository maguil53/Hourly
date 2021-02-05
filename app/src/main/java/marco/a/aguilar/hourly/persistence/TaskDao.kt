package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TasksCompletedInfo

@Dao
interface TaskDao {

    /**
     * Making this just in case later we need a LiveData version
     * of our list of tasks
     */
    @Query("SELECT * FROM tasks WHERE task_block_id = :task_block_id")
    fun getLiveDataTasksForHourBlock(task_block_id: Int): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE task_block_id = :task_block_id")
    fun getTasksForHourBlock(task_block_id: Int): List<Task>

    /**
     * Todo(): Delete Later. For now we're just using this
     * until we implement a way to generate tasks for an HourBlock
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDummyTasks(Task: List<Task>)



    /**
     * This works.
     */
    @Transaction
    @Query("SELECT * FROM hour_blocks")
    fun getTasksInfo(): LiveData<List<TasksCompletedInfo>>

}