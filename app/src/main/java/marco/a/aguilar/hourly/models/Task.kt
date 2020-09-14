package marco.a.aguilar.hourly.models

import marco.a.aguilar.hourly.enums.TaskType

data class Task(
    var isComplete: Boolean = false,
    var type: TaskType,
    var description: String
) { }