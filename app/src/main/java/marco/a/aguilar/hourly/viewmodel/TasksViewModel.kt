package marco.a.aguilar.hourly.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.repository.HourBlockRepository

class TasksViewModel(
    application: Application
): ViewModel() {

    private val repository: HourBlockRepository = HourBlockRepository.getInstance(application)

    val hourBlocks: LiveData<List<HourBlock>> = repository.getHourBlocks()

    /**
     * Factory for constructing TasksViewModel with "application" parameter
     */
    class TasksViewModelFactory(private val application: Application): ViewModelProvider.Factory {

        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(TasksViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TasksViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }


}