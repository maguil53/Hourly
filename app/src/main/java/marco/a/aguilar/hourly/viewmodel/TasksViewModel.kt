package marco.a.aguilar.hourly.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.repository.HourBlockRepository

class TasksViewModel(
    application: Application
): ViewModel() {

    private val repository: HourBlockRepository = HourBlockRepository.getInstance(application)

    /**
     * Having this line like this will crash the app, but that's fine because otherwise
     * it WON'T create the database, the question is, why?
     */
    var hourBlocks: LiveData<List<HourBlock>> = MutableLiveData<List<HourBlock>>()

    init {

        /**
         * For some odd reason, this plus the 2 other lines above call my database to be created!!!
         */

        hourBlocks  = repository.getHourBlocks()


    }

    /**
     * But writing it like this won't call onCreate in the database
            var hourBlocks = MutableLiveData<List<HourBlock>>()

            init {

                viewModelScope.launch {
                    hourBlocks.value = repository.getHourBlocks().value
                }


            }
     */

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