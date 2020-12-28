package marco.a.aguilar.hourly.persistence

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.utils.SingletonHolder
import java.util.concurrent.Executors

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


//    companion object {



//        private const val DATABASE_NAME: String = "hourly_db"

        companion object : SingletonHolder<AppDatabase, Context>({
            Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "hourly_db")
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d(TAG, "onCreate: Database created...calling callback")

                            /**
                             * This still crashes the app but at least it tells us
                             * "getDatabase called recursively" error
                             *
                             * Okay so this works if we put every thing inside
                             *      Executors.newSingleThreadExecutor().execute{}
                             * Now we just need to figure out why and
                             */
                            Executors.newSingleThreadExecutor().execute {
                                AppDatabase.getInstance(it.applicationContext).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
                            }
//                            AppDatabase.getInstance(it.applicationContext).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
                        }

                    }).build()
        })


//        fun getInstance(context: Context): AppDatabase {
//            return instance ?: synchronized(this) {
//                /**
//                 * This is being called twice.....
//                 * So I think our Singleton is completely fucked.
//                 */
//                Log.d(TAG, "getInstance: Creating Database....")
//                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//                    .addCallback(object : RoomDatabase.Callback() {
//
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//
//                            Log.d(TAG, "onCreate: Database created...calling callback")
//
////                            db.execSQL("INSERT INTO hour_blocks (block_id, time, is_complete) VALUES (0,0, false)")
//                            db.execSQL("INSERT INTO hour_blocks (block_id, time, is_complete) VALUES (0,0, 'false')")
//
//                            /**
//                             * Okay so my fucking issue is definitely that we're looping around
//                             * this when calling:
//                             *      getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
//                             *
//                             * Which means we most likely didn't fuckin set up the Singleton pattern correctly.
//                             */
//
//                            /**
//                             * Duh, this isn't a loop because it's called onCreate!!! When the
//                             * database is created.
//                             *
//                             * Okay so this is DEFINITELY what's causing my app to crash.
//                             * This issue might be due to my constructor or the way I'm creating
//                             * my HourBlocks. Or maybe I'm doing something that Room can't understand,
//                             * which is probably one of the reasons I mentioned above.
//                             *
//                             * Okay we just got more information, so the second time I run the app
//                             * the app doesn't crash. This is because the database is already created
//                             * so this method is never called. So even more proof that it's because
//                             * of the code written here. Anyway, we'll either have to use
//                             * onOpen or just keep deleting our database every time we want to test this out
//                             *
//                             *
//                             * Okay so I just ran some tests:
//                             *  1) Removed tasks field because I thought maybe I used the
//                             *  @Ignore annotation wrong.
//                             *  2) Tried modifying the Primary and Secondary constructors
//                             *
//                             * So reading my logs for the fifth time I'm starting to think it's because
//                             * I'm trying to access the database while it's already performing an operation
//                             * I got some help from this post too.
//                             *      https://stackoverflow.com/questions/7930139/android-database-locked
//                             */
//                            //                            db.execSQL("INSERT INTO hour_blocks VALUES ('0', '0', 'false')")
//                            //                            db.beginTransaction()
//
//                            //                            coroutineScope.launch(Dispatchers.IO) {
//                            //                                getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
//                            //                            }
//
//
//
//                            /**
//                             * This works!!! But for some fuckin reason my database isn't letting
//                             * the other modules that it updated.
//                             *  Maybe I'm not setting up the LiveData correctly. Or we're missing something
//                             *  from room?
//                             *
//                             *  Okay, so the above doesn't work but the runnable shit does, why???
//                             *  Maybe I'm not using coroutines correctly.
//                             */
//
//                            /**
//                             * This is the only one that successfully enters info into the database
//                             * without crashing everything, not sure if this is the best way to do
//                             * it, but it works and we need to figure out why. Most Likely I'm not
//                             * creating the coroutine correctly. Anywho
//                             * if this is the only way to successfully enter into the database, then we
//                             * need to figure out a way to read from the database again or update it
//                             * to get the liveupdate update...idk
//                             *
//                             * I think this is being created first but because we're using a thread
//                             * the SELECT is being called before INSERT
//                             */
////                                Executors.newSingleThreadExecutor().execute {
////                                    getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
////                                }
//
////                            runBlocking {
////                                launch(Dispatchers.Default) {
////                                    Log.d(TAG, "onCreate: Calling insert...")
////                                    getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
////                                }
////                            }
////                                getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
////                                TestAsync(getInstance(context)).execute()
//
////                                runBlocking {
////                                    val job = launch(Dispatchers.IO) {
////                                        getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
////                                    }
////
////                                    job.join()
////                                    Log.d(TAG, "onCreate: Done running job")
////                                }
//
////                                runBlocking {
////                                    val job = launch(Dispatchers.IO) {
////                                        Log.d(TAG, "onCreate: launching job")
////                                        getInstance(context).hourBlockDao().insertAllEmptyBlocks(HourBlock.prepopulateHourBlocks())
////                                    }
////
////                                    job.join()
////                                    Log.d(TAG, "onCreate: Done running job")
////                                }
//
//
//
//
//                        }
//                    }).build()
//            }
//
//        }

//    }




}