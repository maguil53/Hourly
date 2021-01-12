package marco.a.aguilar.hourly.persistence

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.utils.SingletonHolder

@Database(entities = [HourBlock::class, Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hourBlockDao(): HourBlockDao
    abstract fun taskDao(): TaskDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "hourly_db")
                .addCallback(object : RoomDatabase.Callback() {

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        /**
                         * Todo:
                         *  1) Check if onCreate callback is called more than once if we close the app
                         *  and open it again. I'm worried that once we start saving information our
                         *  database will get wiped.
                         */
                        Log.d(TAG, "onCreate: Database created...calling callback")

                        /**
                         * Yaaaay, we did this using Coroutines instead! Used suspend keyword in HourBlockDao.
                         * Thanks to this article, I understood Coroutines better:
                         *      https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb
                         */
                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                AppDatabase.getInstance(it.applicationContext).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
                            }
                        }
                    }
                }).build()
    })
}