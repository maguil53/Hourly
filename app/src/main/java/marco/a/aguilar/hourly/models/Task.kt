package marco.a.aguilar.hourly.models

import marco.a.aguilar.hourly.enums.TaskType

data class Task(
    var type: TaskType,
    var description: String,
    var isComplete: Boolean = false
) { }