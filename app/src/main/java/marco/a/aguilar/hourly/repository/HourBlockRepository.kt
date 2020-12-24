package marco.a.aguilar.hourly.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    
    private val database = AppDatabase.getInstance(context)

    companion object {
        private var instance: HourBlockRepository? = null

        fun getInstance(context: Context): HourBlockRepository {
            return instance ?: HourBlockRepository(context)
        }

    }

    fun getHourBlocks(): LiveData<List<HourBlock>> {

        val hourBlocks = MutableLiveData<List<HourBlock>>()

        /**
         * So our issue now is that database.hourBlockDao().getHourBlocks().value
         * is returning a null value. Obviously this will happen because
         * we have nothing in our database the first time we open it.
         * So what we have to do now is Prepopulate our database
         */
        hourBlocks.value = database.hourBlockDao().getHourBlocks().value ?: generateFakeHourBlocks()


        return hourBlocks
    }

    private fun generateFakeHourBlocks(): List<HourBlock> {
        // Creating fake hour blocks
        /**
         * Creating fake hour blocks.
         * For now we'll need to specify Task ID but once we start adding Tasks
         * to our database we'll use the auto-generate property to create the Task IDs
         */
        val hour1Task1 = Task(1, TaskType.WORK, "Do homework", 1, true)
        val hour1Task2 = Task(2, TaskType.WORK, "Take out the trash", 1, true)
        val hour1TaskList: List<Task> = listOf(hour1Task1, hour1Task2)
        val hour1 = HourBlock(1, 1, true, hour1TaskList)

        val hour2Task1 = Task(3, TaskType.WORK, "Go for a run", 2)
        val hour2Task2 = Task(4, TaskType.WORK, "Take a shower", 2)
        val hour2TaskList: List<Task> = listOf(hour2Task1, hour2Task2)
        val hour2 = HourBlock(2, 2, false, hour2TaskList)

        val hour3Task1 = Task(5, TaskType.WORK, "Eat some food", 3)
        val hour3Task2 = Task(6, TaskType.WORK, "Meditate", 3)
        val hour3Task3 = Task(7, TaskType.WORK, "Watch anime!", 3)
        val hour3TaskList: List<Task> = listOf(hour3Task1, hour3Task2, hour3Task3)
        val hour3 = HourBlock(3, 3, false, hour3TaskList)

        val hour4 = HourBlock(4, 4, false, hour3TaskList)
        val hour5 = HourBlock(5, 5, false, hour3TaskList)
        val hour6 = HourBlock(6, 6, false, hour3TaskList)
        val hour7 = HourBlock(7, 7, false, hour3TaskList)
        val hour8 = HourBlock(8, 8, false, hour3TaskList)
        val hour9 = HourBlock(9, 9, false, hour3TaskList)
        val hour10 = HourBlock(10, 10, false, hour3TaskList)
        val hour11 = HourBlock(11, 11, false, hour3TaskList)
        val hour12 = HourBlock(12, 12, false, hour3TaskList)
        val hour13 = HourBlock(13, 13, false, hour3TaskList)
        val hour14 = HourBlock(14, 14, false, hour3TaskList)
        val hour15 = HourBlock(15, 15, false, hour3TaskList)
        val hour16 = HourBlock(16, 16, false, hour3TaskList)
        val hour17 = HourBlock(17, 17, false, hour3TaskList)
        val hour18 = HourBlock(18, 18, false, hour3TaskList)
        val hour19 = HourBlock(19, 19, false, hour3TaskList)
        val hour20 = HourBlock(20, 20, false, hour3TaskList)
        val hour21 = HourBlock(21, 21, false, hour3TaskList)
        val hour22 = HourBlock(22, 22, false, hour3TaskList)
        val hour23 = HourBlock(23, 23, false, hour3TaskList)
        val hour24 = HourBlock(24, 24, false, hour3TaskList)


        return listOf(hour1, hour2, hour3, hour4, hour5, hour6,
            hour7, hour8, hour9, hour10, hour11, hour12,
            hour13, hour14, hour15, hour16, hour17,
            hour18, hour19, hour20, hour21, hour22,
            hour23, hour24)
    }
}