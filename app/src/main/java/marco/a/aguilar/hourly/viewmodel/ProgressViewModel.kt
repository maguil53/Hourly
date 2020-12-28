    package marco.a.aguilar.hourly.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.persistence.AppDatabase
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
): AndroidViewModel(application) {


    private val repository: HourBlockRepository = HourBlockRepository.getInstance(getApplication())

    // Original
    // Will create database
//    var hourBlocks: LiveData<List<HourBlock>> = repository.getHourBlocks()

    /**
     * Backing Field, helps us encapsulate our MutableLiveData object so that only
     * our ViewModel has control over manipulating the data inside (posting data).
     * Read more here (Encapsulating LiveData):
     *  https://developer.android.com/codelabs/kotlin-android-training-live-data#5
     *
     * Now we can't call hourBlocks.value = ... inside our Fragment (good).
     * Guide says we don't wanna do this in Production
     */
//    private var _hourBlocks = MutableLiveData<List<HourBlock>>(repository.getHourBlocks().value)
//    val hourBlocks: LiveData<List<HourBlock>>
//        get() = _hourBlocks

//    private var _hourBlocks = MutableLiveData<List<HourBlock>>(repository.getHourBlocks())

    /**
     * working version
     */
    lateinit var hourBlocks: LiveData<List<HourBlock>>

//    val hourBlocks: MutableLiveData<List<HourBlock>>

    /**
     * There's no need to create a postValue call here because we just want to read get
     * our data from the database. Once the database is created it should already call
     * onCreate and will contain our prepopulated data.
     *
     * Our Database is returning the LiveData object that we need to observe. You're pretty much
     * fucking everything up by calling
     *  _hourBlocks = MutableLiveData<List<HourBlock>>(ListOf(...))
     *
     *  Why? Because you're creating a NEW INSTANCE!!!! We're observing this instance
     *  instead of observing the one given from the database.
     *  That's why in ProgressViewModel it creates the database.
     *  Because we're returning the LiveData object from Room AND OBSERVING IT
     *  I think the observe is what triggers the call to the database.
     *
     *  To us it looked like we were observing the LiveData from the database because
     *  it wasn't null, and that's because we were initializing a different instance here.
     *
     *  Later on in our app if you want to update the live data, just make an insert or
     *  something to the database, this will trigger the LiveData to tell the rest of the
     *  app something updated. No need to call hourBlocks.value = ....
     *
     *  Okay I just changed hourBlocks to hold the LiveData object from repository instead
     *  and it worked! onCreated was called!
     */




    init {
        Log.d(TAG, "ProgressViewModel: init ")
        Log.d(TAG, "ProgressViewModel: init: Checking repository instance: " + repository)

        viewModelScope.launch {
            hourBlocks = repository.getHourBlocks()
        }
//        hourBlocks = repository.getHourBlocks()


        /**
         * Dispatchers.IO runs everything in a background thread.
         * Checking for null here prevents the app from crashing
         * because the _hourBlocks value is never updated unless the value from
         * repository.getHourBlocks() is NOT NULL, but it is. Which means the problem
         * might be one of the following:
         *      1) The read might not be done processing, meaning we need to wait for a response
         *      to actually post the value
         *      2) We're not getting anything from the database
         */
//        var newHourBlocks: List<HourBlock>? = null
//        val job = viewModelScope.launch(Dispatchers.IO) {
//            newHourBlocks = repository.getHourBlocks().value
//        }
//
//        runBlocking {
//            job.join()
//            Log.d(TAG, "ProgressViewModel: Resuming code in coroutine scope")
//            // Still null, so I think the issue might be from database or repo
//            // Wait, we're never calling OnCreate so maybe we should figure that out before testing again
//            Log.d(TAG, "ProgressViewModel: checking newHourBlocks " + newHourBlocks)
//            if(newHourBlocks != null) {
//                Log.d(TAG, "ProgressViewModel viewModelScope: Posting to _HourBlocks ")
//                _hourBlocks.value = newHourBlocks
//            }
//        }



//        var newHourBlocks: List<HourBlock>? = null
//        runBlocking {
//            val job = viewModelScope.launch(Dispatchers.IO) {
//                Log.d(TAG, "ProgressViewModel viewModelScope: starting coroutine/job ")
//                newHourBlocks = repository.getHourBlocks().value
//            }
//            job.join()
//            // This is null apparently
//            if(newHourBlocks != null) {
//                Log.d(TAG, "ProgressViewModel viewModelScope: Posting to _HourBlocks ")
//                Log.d(TAG, "ProgressViewModel viewModelScope: newHourBlocks size " + newHourBlocks!!.size)
//                _hourBlocks.value = newHourBlocks
//            }
//
//        }




        /**
         * So I think now since only ViewModel is in charge of posting, we should
         * create some coroutine/background thread that calls repo.getHour....
         * and posts the value to _hourBLocks
         */


        /**
         * Can't post value from Background thread apparently, but if
         * I just use viewModelScope.launch() without Dispatchers.IO, then the database
         * never gets accessed and hence created.
         */
//        viewModelScope.launch(Dispatchers.IO) {
//            _hourBlocks.value = repository.getHourBlocks().value
//        }
    }



    /**
     * Factory for constructing ProgressViewModel with "application" parameter
     *
     * This is working. ViewModel is created
     * Log.d(ContentValues.TAG, "Creating ProgressViewModel From Factory")
     */
    class ProgressViewModelFactory(private val application: Application): ViewModelProvider.Factory {

        override fun <T: ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                Log.d(ContentValues.TAG, "Creating ProgressViewModel From Factory")
                return ProgressViewModel(application) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }

    }

}