package marco.a.aguilar.hourly.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task

class HourBlockRepository {

    fun getHourBlocks(): LiveData<List<HourBlock>> {

        val hourBlocks = MutableLiveData<List<HourBlock>>()

        // wow, I think I just needed to do ".value"
        hourBlocks.value = generateHourBlocks()

        return hourBlocks
    }

    private fun generateHourBlocks(): List<HourBlock> {
        // Creating fake hour blocks
        val hour1Task1 = Task(TaskType.WORK, "Do homework", true)
        val hour1Task2 = Task(TaskType.WORK, "Take out the trash", true)
        val hour1TaskList: List<Task> = listOf(hour1Task1, hour1Task2)
        val hour1 = HourBlock(1, 1, hour1TaskList)

        val hour2Task1 = Task(TaskType.WORK, "Go for a run")
        val hour2Task2 = Task(TaskType.WORK, "Take a shower")
        val hour2TaskList: List<Task> = listOf(hour2Task1, hour2Task2)
        val hour2 = HourBlock(2, 2, hour2TaskList)

        val hour3Task1 = Task(TaskType.WORK, "Eat some food")
        val hour3Task2 = Task(TaskType.WORK, "Meditate")
        val hour3Task3 = Task(TaskType.WORK, "Watch anime!")
        val hour3TaskList: List<Task> = listOf(hour3Task1, hour3Task2, hour3Task3)
        val hour3 = HourBlock(3, 3, hour3TaskList)

        val hour4 = HourBlock(4, 4, hour3TaskList)
        val hour5 = HourBlock(5, 5, hour3TaskList)
        val hour6 = HourBlock(6, 6, hour3TaskList)
        val hour7 = HourBlock(7, 7, hour3TaskList)
        val hour8 = HourBlock(8, 8, hour3TaskList)
        val hour9 = HourBlock(9, 9, hour3TaskList)
        val hour10 = HourBlock(10, 10, hour3TaskList)
        val hour11 = HourBlock(11, 11, hour3TaskList)
        val hour12 = HourBlock(12, 12, hour3TaskList)
        val hour13 = HourBlock(13, 13, hour3TaskList)
        val hour14 = HourBlock(14, 14, hour3TaskList)
        val hour15 = HourBlock(15, 15, hour3TaskList)
        val hour16 = HourBlock(16, 16, hour3TaskList)
        val hour17 = HourBlock(17, 17, hour3TaskList)
        val hour18 = HourBlock(18, 18, hour3TaskList)
        val hour19 = HourBlock(19, 19, hour3TaskList)
        val hour20 = HourBlock(20, 20, hour3TaskList)
        val hour21 = HourBlock(21, 21, hour3TaskList)
        val hour22 = HourBlock(22, 22, hour3TaskList)
        val hour23 = HourBlock(23, 23, hour3TaskList)
        val hour24 = HourBlock(24, 24, hour3TaskList)


        return listOf(hour1, hour2, hour3, hour4, hour5, hour6,
            hour7, hour8, hour9, hour10, hour11, hour12,
            hour13, hour14, hour15, hour16, hour17,
            hour18, hour19, hour20, hour21, hour22,
            hour23, hour24)
    }
}