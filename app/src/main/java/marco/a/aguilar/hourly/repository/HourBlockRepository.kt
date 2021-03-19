package marco.a.aguilar.hourly.repository

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.models.TasksCompletedInfo
import marco.a.aguilar.hourly.persistence.AppDatabase

class HourBlockRepository private constructor(context: Context) {

    private var database: AppDatabase = AppDatabase.getInstance(context)
    private var hourBlocks: LiveData<List<HourBlock>> = database.hourBlockDao().getHourBlocks()
    private var tasksCompletedInfo: LiveData<List<TasksCompletedInfo>> = database.taskDao().getTasksInfo()

    companion object {
        private var instance: HourBlockRepository? = null

        fun getInstance(context: Context): HourBlockRepository {
            return instance ?: HourBlockRepository(context)
        }
    }

    fun getHourBlocks(): LiveData<List<HourBlock>> {
        return hourBlocks
    }

    fun getTasksCompletedInfo(): LiveData<List<TasksCompletedInfo>> {
        return tasksCompletedInfo
    }

    fun updateTask(task: Task) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                database.taskDao().updateTask(task)
            }
        }
    }

    fun insertTask(task: Task) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                database.taskDao().insertTask(task)
            }
        }
    }

    fun insertTasks(tasks: List<Task>) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                database.taskDao().insertTasks(tasks)
            }
        }
    }

    fun deleteTask(task: Task) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                 database.taskDao().deleteTask(task)
            }
        }
    }

    fun clearTasks(task_block_id: Int) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                database.taskDao().clearTasks(task_block_id)
            }
        }
    }
}