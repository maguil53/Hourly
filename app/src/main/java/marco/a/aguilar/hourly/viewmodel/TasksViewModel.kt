package marco.a.aguilar.hourly.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.repository.HourBlockRepository

class TasksViewModel(
    savedInstanceState: SavedStateHandle
): ViewModel() {

    private val repository: HourBlockRepository = HourBlockRepository()

    val hourBlocks: LiveData<List<HourBlock>> = repository.getHourBlocks()

}