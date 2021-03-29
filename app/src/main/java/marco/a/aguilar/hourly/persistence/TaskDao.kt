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
    suspend fun insertDummyTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Insert
    suspend fun insertTask(task: Task)

    @Insert
    suspend fun insertTasks(tasks: List<Task>)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE task_block_id = :task_block_id")
    suspend fun clearTasks(task_block_id: Int)

    @Query("DELETE FROM tasks WHERE task_block_id IN (:sleepHourBlockIds)")
    suspend fun clearSleepHourTasks(sleepHourBlockIds: List<Int>)

    /**
     * This works.
     */
    @Transaction
    @Query("SELECT * FROM hour_blocks")
    fun getTasksInfo(): LiveData<List<TasksCompletedInfo>>

}