package marco.a.aguilar.hourly.persistence

import androidx.room.TypeConverter
import marco.a.aguilar.hourly.enums.TaskType

class Converters {

    @TypeConverter fun taskTypeToInt(taskType: TaskType): Int = taskType.ordinal

    // Just in case you run into issues with this later:
    // https://stackoverflow.com/questions/57326789/how-to-save-enum-field-in-the-database-room
    @TypeConverter fun intToTaskType(taskInteger: Int): TaskType = enumValues<TaskType>()[taskInteger]

}