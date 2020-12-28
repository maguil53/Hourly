package marco.a.aguilar.hourly.repository

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.persistence.AppDatabase

/**
 * The "object" keyword treats this class like a Singleton. So now we don't
 * have to call HourBlockRepository(), instead we retrieve the single instance
 * of this class by calling HourBlockRepository. This implementation is also
 * thread-safe.
 *
 * https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations
 * https://blog.mindorks.com/how-to-create-a-singleton-class-in-kotlin
 * https://medium.com/swlh/singleton-class-in-kotlin-c3398e7fd76b
 *
 * Problem, I want to pass the context to the Repository but it doesn't seem like
 * there's a way to do this with the "object" keyword. (There's probably a way to do
 * this but for now I'm going to go with what I know)
 *
 * TODO()
 *  1) Prepopulate database
 *  2) Change Line 52 so that it doesn't use generateHourBlocks() anymore
 */
class HourBlockRepository private constructor(context: Context) {


    private var database: AppDatabase
//    private var hourBlocks: LiveData<List<HourBlock>>

    /**
     * For some reason, moving database over here and calling the Dao here
     * fixed our issue and the Database was created this time. However,
     * we're getting a different error now, it's having trouble inserting
     * data I think. It just says "Exception while computing live data."
     */
    init {
        Log.d(TAG, "HourBlockRepository: init: initializing Repo")
        database = AppDatabase.getInstance(context)

        Log.d(TAG, "HourBlockRepository: init: calling database.hourBlockDao().getHourBlocks()")
//        hourBlocks = database.hourBlockDao().getHourBlocks()


    }

    companion object {
        private var instance: HourBlockRepository? = null

        fun getInstance(context: Context): HourBlockRepository {
            return instance ?: HourBlockRepository(context)
        }

    }

    fun getHourBlocks(): LiveData<List<HourBlock>> {

        return database.hourBlockDao().getHourBlocks()
//        return hourBlocks
    }


}