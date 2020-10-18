package marco.a.aguilar.hourly.models

data class HourBlock(
   val id: Int, // 1 - 24
   val time: Int, // 1 - 24
   val taskList: List<Task>,
   var isComplete: Boolean = false
) { }