package marco.a.aguilar.hourly.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import marco.a.aguilar.hourly.models.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getTasks(): LiveData<List<Task>>
}