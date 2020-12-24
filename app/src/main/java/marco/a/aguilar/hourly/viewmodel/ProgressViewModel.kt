package marco.a.aguilar.hourly.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.repository.HourBlockRepository

/**
 * So we learned from this post
 *      https://stackoverflow.com/questions/54419236/why-a-viewmodel-factory-is-needed-in-android
 * If we want to have multiple arguments or pass SOME DATA to the constructor
 * of the ViewModel (our application context in order to pass it to our repository), then we
 * need to create a FACTORY class for our ViewModel.
 *      https://developer.android.com/reference/androidx/lifecycle/ViewModelProvider.Factory
 *
 * The "Guide to app architecture" guide by Google shows you how to connect the ViewModel
 * to the Repository using Dependency Injection (Hilt) so we don't really learn how to put
 * this all together. Luckily I found some good examples and guides.
 *      https://developer.android.com/codelabs/kotlin-android-training-repository#7
 *      https://developer.android.com/codelabs/kotlin-android-training-view-model?index=..%2F..android-kotlin-fundamentals#7
 */
class ProgressViewModel(
    application: Application
): ViewModel() {

    private val repository: HourBlockRepository = HourBlockRepository.getInstance(application)

    val hourBlocks: LiveData<List<HourBlock>> = repository.getHourBlocks()


    /**
     * Factory for constructing ProgressViewModel with "application" parameter
     */
    class ProgressViewModelFactory(private val application: Application): ViewModelProvider.Factory {

        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProgressViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }

}