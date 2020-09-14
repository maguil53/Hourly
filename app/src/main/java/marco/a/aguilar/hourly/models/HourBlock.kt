package marco.a.aguilar.hourly.models

data class HourBlock(
   val id: Int,
   var isComplete: Boolean = false,
   val time: String,
   val taskList: List<String>
) { }