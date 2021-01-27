package marco.a.aguilar.hourly.models

data class TaskCheckItem (
    val task: Task,
    var isNewItem: Boolean = false
)