package marco.a.aguilar.hourly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import marco.a.aguilar.hourly.enums.TaskType
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.models.Task
import marco.a.aguilar.hourly.repository.HourBlockRepository

class ProgressViewModel(
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val repository: HourBlockRepository = HourBlockRepository()

    val hourBlocks: LiveData<List<HourBlock>> = repository.getHourBlocks()


}