package marco.a.aguilar.hourly.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task

@Database(entities = [HourBlock::class, Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hourBlockDao(): HourBlockDao
    abstract fun taskDao(): TaskDao


}