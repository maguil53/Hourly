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
        val hour1Task1 = Task(TaskType.WORK, "Do homework")
        val hour1Task2 = Task(TaskType.WORK, "Take out the trash")
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

        return listOf(hour1, hour2, hour3)
    }
}