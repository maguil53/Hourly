package marco.a.aguilar.hourly.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task

/**
 * This class seems to require a bit more of a set up so
 * that's why I'm not using the "object" keyword.
 * Also more importantly abstract classes can't be instantiated.
 */
@Database(entities = [HourBlock::class, Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun hourBlockDao(): HourBlockDao
    abstract fun taskDao(): TaskDao

    companion object {

        private const val DATABASE_NAME: String = "hourly_db"
        private var instance: AppDatabase? = null

        /**
         * Not exactly sure if this is how you do it but I used these sources to help me out:
         * https://github.com/mitchtabian/Local-db-Cache-Retrofit-REST-API-MVVM/blob/setting-widgets-properties/app/src/main/java/com/codingwithmitch/foodrecipes/persistence/RecipeDatabase.java
         * https://github.com/android/sunflower/blob/main/app/src/main/java/com/google/samples/apps/sunflower/data/AppDatabase.kt
         */
        fun getInstance(context: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }


}